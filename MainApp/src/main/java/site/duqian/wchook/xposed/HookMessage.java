package site.duqian.wchook.xposed;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

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
    private static List<String> list_msg = new ArrayList<>();

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

    /**
     * 用的是群发api，所以发送不能太频繁,会被禁用，导致发送失败
     *
     * @param param
     */
    private void hookNewMessage(XC_MethodHook.MethodHookParam param) {
        synchronized (HookMessage.class) {
            String field_content = (String) getObjectField(param.thisObject, "field_content");
            String field_username = (String) getObjectField(param.thisObject, "field_username");
            int field_unReadCount = (int) getObjectField(param.thisObject, "field_unReadCount");
            int field_isSend = (int) getObjectField(param.thisObject, "field_isSend");
            int msgType = getMsgType(param);
            LogUtils.debug(TAG, "received msg msgType=" + msgType + ",talker=" + field_username + ",field_isSend=" + field_isSend + "content=" + field_content + ",field_unReadCount=" + field_unReadCount);

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

            //getReply(field_username, field_content);
            if (!list_msg.contains(field_content)) {
                list_msg.add(field_content);
                getReply(field_username, field_content);
            }

            if (list_msg.size() > 100) {
                list_msg.clear();
            }
        }
    }

    private int getMsgType(XC_MethodHook.MethodHookParam param) {
        String field_msgType = (String) getObjectField(param.thisObject, "field_msgType");
        int msgType = -1;
        if (!TextUtils.isEmpty(field_msgType)) {
            try {
                msgType = Integer.parseInt(field_msgType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return msgType;
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

    /**
     * hook了微信群发消息的api，这个接口发送的前提是微信coreNetwork 初始化完成之后才可以
     * 不太适合于自动回复，可以自己先微信打开群发助手发几条信息，确保网络基类初始化并连接了，调用改方法主动发送消息
     * 图片消息类似，只是msgType和构造方法不一样
     * @param field_username 接收者id
     * @param replyContent 文本内容
     * @param delayTime 延时暂时未使用
     */
    private void wechatSendMessage(String field_username, String replyContent, int delayTime) {
        LogUtils.debug(TAG, "------wechatSendMessage start-------");

        if (requestCaller == null) {
            requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, classLoader), VersionParam.requestMethod);
        }

        Object masssendObj = newInstance(findClass("com.tencent.mm.plugin.masssend.a.a", classLoader));//
        setObjectField(masssendObj, "laj", field_username);//可以多人，分号分开";",如：xxx;BBB
        setObjectField(masssendObj, "lak", 1);//发送x个人，两个人为2
        setObjectField(masssendObj, "filename", replyContent);
        setObjectField(masssendObj, "msgType", 1);
        LogUtils.debug(TAG, "dq masssendObj=" + masssendObj.toString());

        Object messageRequestObj = newInstance(findClass("com.tencent.mm.plugin.masssend.a.f", classLoader), masssendObj, true);

        callMethod(requestCaller, VersionParam.con_NetworkMethod, messageRequestObj, 0);//delayTime

        //list_msg.add(replyContent);//缓存发送的内容,防止重复发送重复内容

        LogUtils.debug(TAG, "dq requestCaller=" + requestCaller.toString() + "，messageRequestObj:" + messageRequestObj.toString());
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