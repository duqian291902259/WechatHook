package site.duqian.autowechat.accessibility;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.UIUtils;
import site.duqian.autowechat.wechat.WeChatHelper;
import site.duqian.autowechat.wechat.WechatUI;

import java.util.List;

/**
 * 抢红包
 * Created by duqian on 2017/1/19.
 */

public class LuckyMoneyUtil {
    private static final String TAG = LuckyMoneyUtil.class.getSimpleName();
    private static final int WECHAT_MIN_VERSION = 700;// wechat 版本：6.3.9
    public static boolean isOpend = false;
    public static boolean isReceivingHongbao =true;
    private static LuckyMoneyUtil luckyMoneyUtil;

    private Handler mHandler = null;
    public LuckyMoneyUtil(){
        mHandler = UIUtils.getHandler();
        //mHandler = new Handler(Looper.getMainLooper());
    }
    public static synchronized LuckyMoneyUtil init() {
        if(luckyMoneyUtil == null) {
            synchronized (LuckyMoneyUtil.class) {
                if (luckyMoneyUtil == null) {  //第二层校验
                    luckyMoneyUtil = new LuckyMoneyUtil();
                }
            }
        }
        return luckyMoneyUtil;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void openHongBao(AccessibilityEvent event, WechatService service) {
        final CharSequence className = event.getClassName();
        //LogUtils.debug(TAG,"openHongBao className="+className);
        if(WechatUI.UI_OPEN_HB.equals(className)) {
            handleLuckyMoneyReceive(service); //拆红包
        } else if(WechatUI.UI_HB_DETATL.equals(className)) {
            if (isOpend) {
                back(service);//拆开红包后，进入红包详情后返回
            }
        } else if(WechatUI.UI_LUANCHER.equals(className)) {
            if (isOpend){
                CommonUtil.init().backWeChatHomePage(service);
            }
            handleChatListHongBao(service); //在聊天界面,点红包
        }
    }

    private void back(WechatService service) {
        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo(service);
        if (nodeInfo == null) return;
        AccessibilityNodeInfo targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_HB_DETAIL_BACK);
        if(targetNode != null) {
            AccessibilityHelper.performClick(targetNode);
            isOpend = true;
        }
        //AccessibilityHelper.performHome(service);
    }

    @Nullable
    private AccessibilityNodeInfo getAccessibilityNodeInfo(WechatService service) {
        AccessibilityNodeInfo nodeInfo = service.getRootInActiveWindow();
        if(nodeInfo == null) {
            LogUtils.debug(TAG, "rootWindow is null");
            return null;
        }
        return nodeInfo;
    }

    //开红包咯
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private  void handleLuckyMoneyReceive(WechatService service) {
        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo(service);
        if (nodeInfo == null) return;
        AccessibilityNodeInfo targetNode = null;
        int wechatVersion = WeChatHelper.getWechatVersion(UIUtils.getContext());
        //LogUtils.debug(TAG,"wechat verion="+wechatVersion);
        //拆红包
        if (wechatVersion < WECHAT_MIN_VERSION) {
            targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_OPEN_HB);
        } else {
            String buttonId = WechatUI.ID_HB_OPEN_1;
            if(wechatVersion == WECHAT_MIN_VERSION) {
                buttonId =  WechatUI.ID_HB_OPEN_2;
            }
            targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, buttonId);
            if(targetNode == null) {
                //分别对应固定金额的红包 拼手气红包
                AccessibilityNodeInfo textNode = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, "发了一个红包", "给你发了一个红包", "发了一个红包，金额随机");
                if(textNode != null) {
                    for (int i = 0; i < textNode.getChildCount(); i++) {
                        AccessibilityNodeInfo node = textNode.getChild(i);
                        if (WechatUI.CLASS_NAME_BUTTON.equals(node.getClassName())) {
                            targetNode = node;
                            break;
                        }
                    }
                }
            }

            if(targetNode == null) {
                targetNode = AccessibilityHelper.findNodeInfosByClassName(nodeInfo, WechatUI.CLASS_NAME_BUTTON);
            }
        }

        if(targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;
            long sDelayTime = WeChatHelper.getRandom(50,50);
            if(sDelayTime != 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityHelper.performClick(n);
                        isOpend = true;
                    }
                }, sDelayTime);
            } else {
                AccessibilityHelper.performClick(n);
            }
            LogUtils.debug(TAG,"open money targetNode="+sDelayTime);
        }else{
            targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_HB_CLOSE);
            if (targetNode!=null){
                AccessibilityHelper.performClick(targetNode);
                isOpend = true;
            }
            LogUtils.debug(TAG,"open lucky money ,targetNode = null");
        }

        if (!isOpend){
            //手慢了，红包派完了，或者超过了24小时
            targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_HB_CLOSE);
            if (targetNode!=null&&WechatService.ACTION == Constant.ACTION_LUCKY_MONEY){
                AccessibilityHelper.performClick(targetNode);
                isOpend = true;
            }
        }
        LogUtils.debug(TAG,"open lucky money ,Node="+targetNode);
    }

    /**
     * 收到聊天里的红包
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void handleChatListHongBao(WechatService service) {
        //LogUtils.debug(TAG,"handleChatListHongBao");
        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo(service);
        if (nodeInfo == null) return;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(WechatUI.TEXT_RECEIVE_MONEY);
        if(list != null && list.isEmpty()) {
            // 从消息列表查找红包  null?
            AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_LUCKY_MONEY);
            //AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_MSG_WXHB);
            if(node != null) {
                isReceivingHongbao = true;
                AccessibilityHelper.performClick(nodeInfo);
            }
            //LogUtils.debug(TAG,"HB node="+node);
        } else if(list != null) {
            if (isReceivingHongbao){
                //最新的红包领起
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityNodeInfo node = list.get(list.size() - 1);
                        AccessibilityHelper.performClick(node);
                        isReceivingHongbao = false;
                    }
                },WeChatHelper.getRandom(0,50));
            }
            LogUtils.debug(TAG,"HB list != null "+isReceivingHongbao);
        }
    }

}
