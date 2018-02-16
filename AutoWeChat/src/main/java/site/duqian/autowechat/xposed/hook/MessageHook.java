package site.duqian.autowechat.xposed.hook;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import site.duqian.autowechat.android.receiver.BroadcastUtil;
import site.duqian.autowechat.model.robot.ReplyBean;
import site.duqian.autowechat.model.robot.RobotUtil;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.ThreadManager;
import site.duqian.autowechat.xposed.VersionParam;
import site.duqian.autowechat.xposed.utils.XSharedPrefUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static site.duqian.autowechat.xposed.Main.list_msg;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

/**
 * Created by duqian on 2017/3/1.
 */

public class MessageHook extends BaseHook {

    private static final String TAG = MessageHook.class.getSimpleName();
    private Context mContext;

    public MessageHook(ClassLoader classLoader,Context context) {
        super(classLoader);
        mContext = context;
        if (handler==null){
            handler=new Handler(Looper.getMainLooper());
        }
    }

    public void hookChattingUI_A() {
        XposedHelpers.findAndHookMethod(VersionParam.activity_chatingui_a, classLoader, "bCS", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);//使得bCS()方法为真,
                LogUtils.debug(TAG,"hookChattingUI_A= "+param.getResult());
            }
        });
    }

    //hook聊天列表里面的消息
    public void hookConversationItem() {
        findAndHookMethod(VersionParam.conversationClass, classLoader, VersionParam.con_GetCursorMethod, Cursor.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                synchronized (MessageHook.class){
                    String field_content  = (String) getObjectField(param.thisObject, "field_content");
                    String field_username  = (String) getObjectField(param.thisObject, "field_username");
                    int field_unReadCount  = (int) getObjectField(param.thisObject, "field_unReadCount");
                    if (field_unReadCount == 0|| TextUtils.isEmpty(field_content)){
                        return;
                    }

                    if (field_username.contains("wechat")){
                        return;
                    }

                    if (!list_msg.contains(field_content)) {
                        list_msg.add(field_content);
                        boolean autoReplyMsg = autoReplyMsg(field_content);
                        if (autoReplyMsg) {
                            getReply(field_username,field_content);
                        }
                        LogUtils.debug(TAG,field_unReadCount+"field_content="+field_content+",field_username="+field_username);

                        catchMessage(field_username, field_content);//发送拦截的消息，到微信自动化APP处理回复,弃用了

                    }

                    //防止消息过多
                    if (list_msg.size()>60){
                        list_msg.clear();
                    }
                }
            }
        });
    }

    private static Handler handler;
    private static Object requestCaller;
    //子线程请求,获取机器人回复
    private void getReply(String field_username, String field_content) {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                String replyContent = RobotUtil.askTuringRobot(field_content);
                sendReplyDelay(field_username,replyContent);
            }
        });
    }

    //延时回复
    private void sendReplyDelay(final String field_username,String replyContent) {
        final int delayTime = XSharedPrefUtil.getDelayTime();
        LogUtils.debug(TAG,"reply = "+replyContent +",delay=" +delayTime);
        //微信发消息
        wechatSendMessage(field_username, replyContent,delayTime);
    }

    private void wechatSendMessage(String field_username, String replyContent, int delayTime) {
        if (requestCaller==null){
            requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, classLoader), VersionParam.getNetworkByModelMethod);
        }
        Object messageRequest = newInstance(findClass(VersionParam.con_MessageClass, classLoader), field_username, replyContent,1);//type

        //发送消息
        callMethod(requestCaller, VersionParam.con_NetworkMethod, messageRequest, delayTime);
        list_msg.add(replyContent);//防止回复自己发送的内容

        LogUtils.debug(TAG,"wechatSendMessage :" + requestCaller+"，messageRequest:" + messageRequest);
    }


    private void catchMessage(String talker, String content) {
        boolean isListenerMessage = XSharedPrefUtil.isListenerMessage();
        if (isListenerMessage) {
            sendBroadcastTask(talker, content);
        }
    }

    // 机器人回复
    private  boolean autoReplyMsg(String content) {
        final boolean isOpenRobotReply = XSharedPrefUtil.isOpenRobotReply();
        if (!isOpenRobotReply) {
            LogUtils.debug(TAG,"isOpenRobotReply="+isOpenRobotReply);
            return false;
        }

        //自动回复
        final String keywords = XSharedPrefUtil.getFilterKeywords();
        if (keywords.contains(content)){
            LogUtils.debug(TAG,keywords+"   包含过滤信息，不回复: " + content);
            return false;
        }

        String lastReplyContent = XSharedPrefUtil.getLastReplyContent();
        if (lastReplyContent.equals(content)){
            LogUtils.debug(TAG,"lastReplyContent repeated!"+lastReplyContent);
            return false;
        }

        return true;
    }

    private void sendBroadcastTask(String talker, String content) {
        //int delayMillis = XSharedPrefUtil.getDelayTime() + 1000;
        final String message = "hook "+talker+" message:" + content;
        LogUtils.debug(TAG," "+message);

        //发送广播，传递信息
        try {
            ReplyBean replyBean = new ReplyBean(talker,content);
            BroadcastUtil.sendXposedBroadcast(mContext,replyBean);
        } catch (Throwable e) {
            LogUtils.debug(TAG,"hook sendXposedBroadcast error  "+e.toString());
        }
    }

}