package site.duqian.wchook.xposed;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import de.robv.android.xposed.XC_MethodHook;
import site.duqian.wchook.base.BaseHook;
import site.duqian.wchook.common.ApiUtil;
import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.ThreadManager;
import site.duqian.wchook.wechat.WeChatHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Dusan (duqian) on 2017/5/11 - 11:46.
 * E-mail: duqian2010@gmail.com
 * Description:微信消息注入
 * remarks:
 */

public class HookMessage extends BaseHook {

    private static final String TAG = HookMessage.class.getSimpleName() + "-wc-dq";
    private static Handler handler;
    private static Object requestCaller;
    //private static List<String> list_msg = new ArrayList<>();

    private SettingsHelper mSettings;

    public HookMessage(ClassLoader classLoader, Context context, SettingsHelper mSettings) {
        super(classLoader, context);
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        this.mSettings = mSettings;
    }

    //hook聊天列表里面的消息
    public void hookConversationItem() {
        LogUtils.debug(TAG, "dq hookMessage");
        findAndHookMethod(VersionParam.conversationClass, classLoader, VersionParam.con_GetCursorMethod, Cursor.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    hookNewMessage(param);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.debug(TAG, "dq hookMessage error " + e);
                }
            }
        });
    }

    private void hookNewMessage(XC_MethodHook.MethodHookParam param) {
        synchronized (HookMessage.class) {
            String field_content = (String) getObjectField(param.thisObject, "field_content");
            String field_username = (String) getObjectField(param.thisObject, "field_username");
            int field_unReadCount = (int) getObjectField(param.thisObject, "field_unReadCount");
            int field_isSend = (int) getObjectField(param.thisObject, "field_isSend");
            LogUtils.debug(TAG, "hookMessage " + field_username + ",field_isSend=" + field_isSend + "content=" + field_content + ",field_unReadCount=" + field_unReadCount);
            getMsgType(param);
            if (field_unReadCount == 0 || TextUtils.isEmpty(field_content)) {//msgType=1表示文本
                return;
            }
            if (field_isSend == 1) {//自己发的不回复
                return;
            }
            if (field_username.contains("微信团队") || field_username.toLowerCase().contains("wechat")
                    || field_username.contains("@chatroom")) {
                return;
            }
            //I've accepted your friend request. Now let's chat! 我通过了你的朋友验证请求，现在我们可以开始聊天了
            if (field_content.contains("通过了你的朋友验证请求") || field_content.contains("accepted your friend request")) {
                return;
            }

            getReply(field_username, field_content);
        }
    }

    private void getMsgType(XC_MethodHook.MethodHookParam param) {
        String field_msgType = (String) getObjectField(param.thisObject, "field_msgType");
        int msgType = -1;
        if (!TextUtils.isEmpty(field_msgType)) {
            try {
                msgType = Integer.parseInt(field_msgType);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        LogUtils.debug(TAG, "hookMessage msgType=" + msgType);

    }

    //子线程请求,获取机器人回复
    private void getReply(String field_username, String field_content) {
        boolean autoReplyMsg = autoReplyMsg(field_content);
        LogUtils.debug(TAG, field_content + ",autoReplyMsg=" + autoReplyMsg);

        if (!autoReplyMsg) {
            return;
        }
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                String replyContent = ApiUtil.init().askTuringRobot(field_content);
                if (!TextUtils.isEmpty(replyContent)) {
                    //延时回复
                    int delayTime = WeChatHelper.getRandom(100, 1000);
                    LogUtils.debug(TAG, "reply = " + replyContent + ",delay=" + delayTime);
                    wechatSendMessage(field_username, replyContent, delayTime);
                } else {
                    LogUtils.debug(TAG, "reply = null");
                }
            }
        });
    }

    //微信发消息
    private void wechatSendMessage(String field_username, String replyContent, int delayTime) {
        LogUtils.debug(TAG, "------wechatSendMessage start-------");

        if (requestCaller == null) {
            requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, classLoader), VersionParam.requestMethod);
        }

        Object masssendObj = newInstance(findClass("com.tencent.mm.plugin.masssend.a.a", classLoader));//
        setObjectField(masssendObj, "laj", field_username);
        setObjectField(masssendObj, "lak", 1);//发送给一个人，为0
        setObjectField(masssendObj, "filename", replyContent);
        setObjectField(masssendObj, "msgType", 1);
        LogUtils.debug(TAG, "dq wechat masssendObj :" + masssendObj.toString());

        Object messageRequestObj = newInstance(findClass("com.tencent.mm.plugin.masssend.a.f", classLoader), masssendObj, false);//

        //Object messageRequestObj = newInstance(findClass(VersionParam.con_MessageClass, classLoader), field_username, replyContent, 1);//type

        callMethod(requestCaller, VersionParam.con_NetworkMethod, messageRequestObj, delayTime);
        //list_msg.add(replyContent);//缓存发送的内容
        LogUtils.debug(TAG, "dq wechat SendMessage :" + requestCaller.toString() + "，messageRequestObj:" + messageRequestObj.toString());
    }

    //AI机器人回复
    private boolean autoReplyMsg(String content) {
        final boolean isOpenRobotReply = XspUtil.init(mSettings).isOpenRobotReply();
        if (!isOpenRobotReply) {
            LogUtils.debug(TAG, "dq isOpenRobotReply=" + isOpenRobotReply);
            return false;
        }

        //过滤信息
        final String keywords = XspUtil.init(mSettings).getFilterKeywords();
        if (keywords.contains(content)) {
            LogUtils.debug(TAG, keywords + ",包含过滤信息，不回复: " + content);
            return false;
        }

        return true;
    }

}