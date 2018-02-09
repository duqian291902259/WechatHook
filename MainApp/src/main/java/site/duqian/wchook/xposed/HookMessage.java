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

/**
 * Created by Dusan (duqian) on 2017/5/11 - 11:46.
 * E-mail: duqian2010@gmail.com
 * Description:微信消息注入
 * remarks:
 */

public class HookMessage extends BaseHook {

    private static final String TAG = HookMessage.class.getSimpleName();
    private static Handler handler;
    private static Object requestCaller;
    private static List<String> list_msg = new ArrayList<>();

    private  SettingsHelper mSettings;
    public HookMessage(ClassLoader classLoader, Context context, SettingsHelper mSettings) {
        super(classLoader,context);
        if (handler==null){
            handler=new Handler(Looper.getMainLooper());
        }
        this.mSettings = mSettings;
    }

    //hook聊天列表里面的消息
    public void hookConversationItem() {
        LogUtils.debug(TAG,"duqian  hookMessage");
        findAndHookMethod(VersionParam.conversationClass, classLoader,
                VersionParam.con_GetCursorMethod, Cursor.class,
                new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                synchronized (HookMessage.class){
                    String field_content  = (String) getObjectField(param.thisObject, "field_content");
                    String field_username  = (String) getObjectField(param.thisObject, "field_username");
                    int field_unReadCount  = (int) getObjectField(param.thisObject, "field_unReadCount");
                    //LogUtils.debug(TAG,"hookMessage "+field_username+",content="+field_content);
                    if (field_unReadCount == 0|| TextUtils.isEmpty(field_content)){
                        return;
                    }

                    if (field_username.contains("微信团队")||field_username.toLowerCase().contains("wechat")
                            ||field_username.contains("@chatroom")){
                        return;
                    }
                    //I've accepted your friend request. Now let's chat! 我通过了你的朋友验证请求，现在我们可以开始聊天了
                    if (field_content.contains("通过了你的朋友验证请求")||field_content.contains("accepted your friend request")){
                        return;
                    }

                    if (!list_msg.contains(field_content)) {
                        list_msg.add(field_content);
                        boolean autoReplyMsg = autoReplyMsg(field_content);
                        if (autoReplyMsg) {
                            getReply(field_username,field_content);
                        }
                        LogUtils.debug(TAG,field_unReadCount+",autoReplyMsg="+autoReplyMsg);
                    }

                    if (list_msg.size()>60){
                        list_msg.clear();
                    }
                }
            }
        });
    }

    //子线程请求,获取机器人回复
    public void getReply(String field_username, String field_content) {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                String replyContent = ApiUtil.init().askTuringRobot(field_content);
                if (!TextUtils.isEmpty(replyContent)) {
                    //延时回复
                    int delayTime = WeChatHelper.getRandom(5678,12345);
                    LogUtils.debug(TAG,"reply = "+replyContent +",delay=" +delayTime);
                    wechatSendMessage(field_username, replyContent,delayTime);
                }else{
                    LogUtils.debug(TAG,"reply = null");
                }
            }
        });
    }

    //微信发消息
    private void wechatSendMessage(String field_username, String replyContent, int delayTime) {
        if (requestCaller==null){
            requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, classLoader), VersionParam.requestMethod);
        }
        Object messageRequest = newInstance(findClass(VersionParam.con_MessageClass, classLoader), field_username, replyContent,1);//type
        callMethod(requestCaller, VersionParam.con_NetworkMethod, messageRequest, delayTime);
        list_msg.add(replyContent);//防止回复自己发送的内容
        LogUtils.debug(TAG,"wechatSendMessage :" + requestCaller+"，messageRequest:" + messageRequest);
    }

    //AI机器人回复
    private  boolean autoReplyMsg(String content) {
        final boolean isOpenRobotReply = XspUtil.init(mSettings).isOpenRobotReply();
        if (!isOpenRobotReply) {
            LogUtils.debug(TAG,"isOpenRobotReply="+isOpenRobotReply);
            return false;
        }

        //过滤信息
        /*final String keywords = XspUtil.init(mSettings).getFilterKeywords();
        if (keywords.contains(content)){
            LogUtils.debug(TAG,keywords+"   包含过滤信息，不回复: " + content);
            return false;
        }*/

        return true;
    }

}