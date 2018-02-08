package site.duqian.wchook.wechat;


/**
 * 辅助服务 wechat UI
 * Created by duqian on 2017/1/18.
 */

public class WechatUI {

    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    //ID
    public static String BASE_ID = "com.tencent.mm:id/";
    public static String ID_BACK = BASE_ID+"gq";//gd 返回按钮
    public static String ID_USER_NAME = BASE_ID+"lg";//username
    public static String ID_SAY_HELLO = BASE_ID+"ab0";//打招呼
    public static String ID_SEND_MESSAGE = BASE_ID+"ab4";//发消息
    public static String ID_SEND_HELLO_EDIT_VIEW = BASE_ID+"c4i";//editview id
    public static String ID_SEND_HELLO_BUTTON = BASE_ID+"g9";//发送按钮 id

    public static String ID_NEARBY_LIST_VIEW = BASE_ID+"bq2";//附近的人 listview id
    public static String ID_NEARBY_USERNAME= BASE_ID+"ajh";//附近的人列表，username
    public static String ID_NEARBY_USER_AVATAR = BASE_ID+"ajg";// 附近的人列表，头像

    public static void initWechatUI(String versionName) {
        if (versionName.contains("6.3.32")) {
            ID_USER_NAME = BASE_ID+"lg";//username
            ID_SAY_HELLO = BASE_ID+"ab0";//打招呼
            ID_SAY_HELLO = BASE_ID+"ab4";//发消息
            ID_SEND_HELLO_EDIT_VIEW = BASE_ID+"c4i";//editview id
            ID_SEND_HELLO_BUTTON = BASE_ID+"g9";//发送按钮 id
            ID_BACK = BASE_ID+"gq";//back

            ID_NEARBY_USER_AVATAR = BASE_ID+"ajg";//附近的人列表，个人头像
            ID_NEARBY_LIST_VIEW = BASE_ID+"bq2";//附近的人 listview id
            ID_NEARBY_USERNAME = BASE_ID+"ajh";//附近的人列表，username

        }else if (versionName.contains("6.5.7")){
            ID_USER_NAME = BASE_ID+"lg";//username
            ID_SAY_HELLO = BASE_ID+"adf";//打招呼
            //// TODO: 2017/5/10 发消息
            ID_SEND_HELLO_EDIT_VIEW = BASE_ID+"cap";//editview id
            ID_SEND_HELLO_BUTTON = BASE_ID+"gd";//发送按钮 id
            ID_BACK = BASE_ID+"gw";//back

            ID_NEARBY_USER_AVATAR = BASE_ID+"am2";//附近的人列表，个人头像
            ID_NEARBY_LIST_VIEW = BASE_ID+"bwn";//附近的人 listview id
            ID_NEARBY_USERNAME = BASE_ID+"am3";//附近的人列表，username
        }
    }

    //class
    public static final String CLASS_NAME_EDITVIEW = "android.widget.EditText";//编辑框
    public static final String CLASS_NAME_BUTTON = "android.widget.Button";//按钮
    public static final String CLASS_NAME_LIST_VIEW = "android.widget.ListView";//ListView

    //Text
    public static final String TEXT_DISCOVERY = "发现";
    public static final String TEXT_DISCOVERY_EN = "Discover";
    public static final String TEXT_NEARBY = "附近的人";
    public static final String TEXT_NEARBY_EN = "People Nearby";
    public static final String TEXT_SAY_HELLO = "打招呼";
    public static final String TEXT_SAY_HELLO_EN = "Send Greeting";
    public static final String TEXT_SEND_MESSAGE = "发消息";
    public static final String TEXT_SEND_MESSAGE_EN = "Message";//"Send Message";
    public static final String TEXT_SEND = "发送";
    public static final String TEXT_SEND_EN = "Send";
    public static final String TEXT_DETAIL = "详细资料";
    public static final String TEXT_DETAIL_EN = "Profile";

    //UI
    public static final String UI_LUANCHER = WECHAT_PACKAGE_NAME +".ui.LauncherUI";
    public static final String UI_SAYHI_EDIT = WECHAT_PACKAGE_NAME +".ui.contact.SayHiEditUI";
    public static final String UI_CONTACT_INFO = WECHAT_PACKAGE_NAME +".plugin.profile.ui.ContactInfoUI";
    public static final String UI_NEARBY_FRIENDS = WECHAT_PACKAGE_NAME +".plugin.nearby.ui.NearbyFriendsUI";
    public static final String UI_NearbySayHiList = WECHAT_PACKAGE_NAME +".plugin.nearby.ui.NearbySayHiListUI";
    public static final String UI_NearbyPersonalInfo = WECHAT_PACKAGE_NAME +".plugin.nearby.ui.NearbyPersonalInfoUI";
    public static final String UI_NearbyFriendShowSayHi = WECHAT_PACKAGE_NAME +".plugin.nearby.ui.NearbyFriendShowSayHiUI";

}
