package site.duqian.autowechat.model;

/**
 * Created by duqian on 2017/1/16.
 */

public class Constant {

    //action
    public static final int  ACTION_DEFAULT = 0;
    public static final int  ACTION_OPEN_WECHAT= 100;
    public static final int  ACTION_ENTER_PYQ = 101;
    public static final int  ACTION_SEND_MESSAGE = 1021;
    public static final int  ACTION_AUTO_REPLY = 102;
    public static final int  ACTION_LUCKY_MONEY = 103;
    public static final int  ACTION_SEND_PYQ = 104;

    public static String packageName = "site.duqian.autowechat";
    public static String processName = "site.duqian.robotservice";
    public static final String PREFERENCE_NAME = "config";//"config";
    public static final String UI_LUANCHER = "site.duqian.autowechat.android.MainActivity";

    //sp
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    public static final String SP_MSG_KEYWORDS = "sp_msg_keywords";
    public static final String SP_REPLY_CONTENT = "sp_reply_content";
    public static final String SP_LUCKY_MONEY = "SP_LUCKY_MONEY";
    public static final String SP_ROBOT_REPLEY = "SP_ROBOT_REPLEY";
    public static final String SP_ACCESSIBILITY_REPLEY = "SP_ACCESSIBILITY_REPLEY";
    public static final String SP_WECHAT_VERSION_NAME = "SP_WECHAT_VERSION_NAME";
    public static final String SP_XPOSED_OPENED =  "SP_XPOSED_OPENED";
    public static final String SP_IS_WTOOL_OK =  "SP_IS_WTOOL_OK";
    public static final String SP_Listener_OK =  "SP_Listener_OK";
    public static final String SP_LAST_REPLY =  "SP_LAST_REPLY";
    public static final int PAGE_ABOUT = 10;
    public static final int PAGE_AUTO_REPLEY = 11;
    public static final int PAGE_HOOK = 12;
    public static final String PAGE_TYPE = "PAGE_TYPE";

    public static final String ACTION_MEASSAG_RECEIVER = "action_message_receiver";//微信消息广播
    public static final String isListenerMessage = "isListenerMessage";


}
