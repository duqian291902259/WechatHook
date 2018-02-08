package site.duqian.wchook.model;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 19:11.
 * E-mail: duqian2010@gmail.com
 * Description:Constant 常量
 * remarks:
 */

public class Constant {
    public static String packageName="site.duqian.wchook";
    public static final String PREFERENCE_NAME="config";
    public static final String SP_LATITUDE = "latitude";
    public static final String SP_LONGITUDE = "longitude";
    public static final String SP_ADDRESS = "address";
    public static final String SP_DURATION = "duration";
    public static final String SP_GREETING_CONTENT = "greeting_content";
    public static final String SP_IS_AUTO_REPLY = "is_auto_reply";//是否自动回复
    public static final String SP_IS_REPLY_BY_XP = "is_reply_by_xp";//xposed
    public static final String SP_IS_REPLY_BY_AS = "is_reply_by_as";//辅助
    public static final String MAP_DEFAULT_LATITUDE = "23.13080840";
    public static final String MAP_DEFAULT_LONGITUDE = "113.4014138";
    public static final String NOTIFACTION_ADDED_FRIEND = "通过了你的朋友验证请求";
    public static final String NOTIFACTION_ADDED_FRIEND_EN = "accepted your friend request";
    //我通过了你的朋友验证请求，现在我们可以开始聊天了;
    //I've accepted your friend request. Now let's chat!


    public static final int ACTION_DEFAULT = 1000;
    public static final int ACTION_SAY_HELLO = 1001;
    public static final String ACTION_MEASSAG_RECEIVER = "site.duqian.wchook.send_message";

    public static final String GOOGLEMAP_APP_KEY = "AIzaSyBjYioQioQ1orOj_hujoRw6B7zwmTihYQg";
    public static final String GOOGLEMAP_KEY     = "AIzaSyBjYioQioQ1orOj_hujoRw6B7zwmTihYQg";

}
