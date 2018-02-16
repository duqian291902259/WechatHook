package site.duqian.autowechat.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.model.robot.RobotUtil;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.NotificationUtils;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.SystemUtil;
import site.duqian.autowechat.wechat.WeChatHelper;
import site.duqian.autowechat.wechat.WechatUI;


/**
 * Created by duqian on 2017/1/13.
 */

public class WechatService extends AccessibilityService {

    private static String TAG = WechatService.class.getSimpleName();
    public static int ACTION = 0;
    private Context context;
    public static boolean pasted = false;

    private static WechatService service;
    private static String[] keywords;
    private String replyContent;
    private boolean isOpenLuckyMoney;
    private boolean isAutoReply = true;
    private boolean isXposedOpen;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        service = this;
        context = getApplicationContext();
        getConfigText();
        LogUtils.debug(TAG, "onServiceConnected");
    }

    private void getConfigText() {
        replyContent = Config.getReplyContent();
        SystemUtil.copyText(context, replyContent);
        final String keyword = Config.getKeywords();
        keywords = keyword.split("，");
    }

    public void setDefalut() {
        ACTION = Constant.ACTION_DEFAULT;
        pasted = false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //LogUtils.debug(TAG,ACTION+event.toString());
        //LogUtils.debug(TAG,"ACTION="+ACTION);
        if (Constant.ACTION_SEND_MESSAGE == ACTION) {
            CommonUtil.init().clickMessage(service);
        } else if (Constant.ACTION_SEND_PYQ == ACTION) {
            PyqUtil.init().sendPYQ(event, service);
        } else if (ACTION == Constant.ACTION_ENTER_PYQ) {
            PyqUtil.init().enterPYQ(event, service);
        }
        handleEvent(event);
    }

    private void handleEvent(AccessibilityEvent event) {
        final CharSequence className = event.getClassName();
        if (className.toString().contains(WechatUI.WECHAT_PACKAGE_NAME)) {
            LogUtils.debug(TAG, "handleEvent className=" + className);
        }
        //是否开启了抢红包功能
        isOpenLuckyMoney = SPUtils.getBoolean(context, Constant.SP_LUCKY_MONEY, false);
        final int eventType = event.getEventType();
        //通知栏事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            handleNotificationEvent(event);
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (isOpenLuckyMoney) {//抢红包  && ACTION== Constant.ACTION_LUCKY_MONEY
                LuckyMoneyUtil.init().openHongBao(event, service);
            }
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            handleContentChanged(event, service);
        } else if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            if (ACTION == Constant.ACTION_AUTO_REPLY || Constant.ACTION_SEND_MESSAGE == ACTION) {
                CommonUtil.init().sendText(service);
            }
        }
    }

    private void handleContentChanged(AccessibilityEvent event, WechatService service) {
        if (isOpenLuckyMoney && ACTION == Constant.ACTION_LUCKY_MONEY) {//聊天界面抢红包
            LuckyMoneyUtil.init().handleChatListHongBao(service);
        } else if (!pasted && ACTION == Constant.ACTION_AUTO_REPLY) {//
            CommonUtil.init().pasteText(service);
        }
        if (Constant.ACTION_SEND_MESSAGE == ACTION) {
            CommonUtil.init().testSendMsg(service);
        }
        //刚打开红包就回到主界面
        if (LuckyMoneyUtil.isOpend) {
            CommonUtil.init().backWeChatHomePage(service);
        }

    }

    private void handleNotificationEvent(AccessibilityEvent event) {
        isAutoReply = SPUtils.getBoolean(context, Constant.SP_ACCESSIBILITY_REPLEY, true);
        //isXposedOpen = SPUtils.getBoolean(context, Constant.SP_XPOSED_OPENED, false);
        Parcelable data = event.getParcelableData();
        if (data == null || !(data instanceof Notification)) {
            return;
        }
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            String text = String.valueOf(texts.get(0));//包括微信名  :
            LogUtils.debug(TAG, "通知栏信息：" + text);
            if (isAutoReply) {//&& !isXposedOpen
                if (!handleNewYearMsg(text)) {
                    new RobotUtil(context).requestTuringRobot(text);
                }
            }
            notificationEvent(text, (Notification) data);
        }
    }

    private boolean handleNewYearMsg(String text) {
        String replyContent = Config.getReplyContent();
        String[] keywords = Config.getKeywords().split("，");
        for (String keyword: keywords) {
            if (text.contains(keyword)) {
                SystemUtil.copyText(context, replyContent);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前服务是否正在运行
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if (service == null) {
            return false;
        }
        return AccessibilityHelper.isServiceRunning(service);
    }

    //处理通知栏消息
    private void notificationEvent(String ticker, Notification notification) {
        String text = ticker.trim();
        PendingIntent pendingIntent = null;
        /*for (String keyword: keywords) {
            if (text.contains(keyword)) {
                ACTION = Constant.ACTION_AUTO_REPLY;
                pasted = false;
                //LogUtils.debug(TAG, "拦截指定的微信信息：" + text);
                getConfigText();
                pendingIntent = notification.contentIntent;
            }
        }*/

        if (text.contains(WechatUI.TEXT_LUCKY_MONEY2)) {
            ACTION = Constant.ACTION_LUCKY_MONEY;
            LuckyMoneyUtil.isReceivingHongbao = true;
            LuckyMoneyUtil.isOpend = false;
            pendingIntent = notification.contentIntent;
        } else {
            if (isAutoReply) {//不要冲突 &&!isXposedOpen
                ACTION = Constant.ACTION_AUTO_REPLY;
                pasted = false;
                pendingIntent = notification.contentIntent;
            }
        }
        if (pendingIntent != null) {
            SystemUtil.notifyGotMessage(context, text);//通知
            WeChatHelper.initWechat(context);
            NotificationUtils.send(pendingIntent);
        }
    }

    @Override
    public void onInterrupt() {
        LogUtils.debug(TAG, "onInterrupt");
    }

}
