package site.duqian.autowechat.wechat;


/**
 * 辅助服务 wechat UI
 * Created by duqian on 2017/1/18.
 */

public class WechatUI {

    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    public static String BASE_ID = "com.tencent.mm:id/";

    //ID
    public static String ID_HB_DETAIL_BACK = BASE_ID+"gs";//红包详情页后退 id
    public static String ID_HB_CLOSE = BASE_ID+"bed";//手慢了，提示界面关闭id
    public static String ID_HB_OPEN_1 = BASE_ID+"b43";//拆红包 id
    public static String ID_HB_OPEN_2 = BASE_ID+"b2c";//开红包 id

    public static String ID_MSG_WRAP = BASE_ID+"adq";//消息最外层按钮
    public static String ID_CHAT_EDITVIEW = BASE_ID+"a27";//文本框
    public static String ID_MSG_SEND = BASE_ID+"a2c";//发送信息
    public static String ID_BACK = BASE_ID+"gb";//返回后退 id

    public static String ID_SEND_PYQ = BASE_ID+"f6";//发朋友圈(右侧相机按钮) id
    public static String ID_PHOTO_CHECKBOX_WRAP = BASE_ID+"b2c";//check图片，外层view id
    public static String ID_SEND_EDIT_VIEW = BASE_ID+"cio";//发文本 editview id
    public static String ID_SEND_PYQ_BUTTON = BASE_ID+"g_";//发送按钮 id

    public static String ID_SEND_TEXT_TIPS = BASE_ID+"b_e";//发文本有时候会有提示 id
    public static String ID_I_KNOW = BASE_ID+"a_y";//从相册中选 提示对话框 id

    public static void initWechatUI(String versionName) {
        if (versionName.contains("6.5.4")) {
            ID_MSG_WRAP = BASE_ID+"af9";//消息最外层按钮
            ID_CHAT_EDITVIEW = BASE_ID+"a2v";//文本框
            ID_MSG_SEND = BASE_ID+"a31";//发送信息

            ID_SEND_PYQ = BASE_ID+"f_";//发朋友圈(右侧相机按钮) id
            ID_PHOTO_CHECKBOX_WRAP = BASE_ID+"b5c";//check图片，外层view id
            ID_SEND_EDIT_VIEW = BASE_ID+"cn4";//发文本 editview id
            ID_SEND_PYQ_BUTTON = BASE_ID+"gd";//发送按钮 id

        }else if (versionName.contains("6.5.3")) {//wechat version 6.5.3
            ID_MSG_WRAP = BASE_ID+"adq";//消息最外层按钮
            ID_CHAT_EDITVIEW = BASE_ID+"a27";//文本框
            ID_MSG_SEND = BASE_ID+"a2c";//发送信息
            ID_BACK = BASE_ID+"gb";//返回后退 id

            ID_SEND_PYQ = BASE_ID+"f6";//发朋友圈(右侧相机按钮) id
            ID_PHOTO_CHECKBOX_WRAP = BASE_ID+"b2c";//check图片，外层view id
            ID_SEND_EDIT_VIEW = BASE_ID+"cio";//发文本 editview id
            ID_SEND_PYQ_BUTTON = BASE_ID+"g_";//发送按钮 id


        } else if (versionName.contains("6.3.32")) {
            ID_MSG_WRAP = BASE_ID+"adc";//消息最外层按钮
            ID_CHAT_EDITVIEW = BASE_ID+"a2_";//文本框
            ID_MSG_SEND = BASE_ID+"a2f";//发送信息

            ID_SEND_PYQ = BASE_ID+"f5";//发朋友圈(右侧相机按钮) id
            ID_PHOTO_CHECKBOX_WRAP = BASE_ID+"b1j";//check图片，外层view id
            ID_SEND_EDIT_VIEW = BASE_ID+"che";//发文本 editview id
            ID_SEND_PYQ_BUTTON = BASE_ID+"g9";//发送按钮 id

        }
    }

    //class
    public static final String CLASS_NAME_EDITVIEW = "android.widget.EditText";//编辑框
    public static final String CLASS_NAME_BUTTON = "android.widget.Button";//按钮
    //Text
    public static final String TEXT_LUCKY_MONEY = "微信红包";
    public static final String TEXT_LUCKY_MONEY2 = "[微信红包]";
    public static final String TEXT_RECEIVE_MONEY = "领取红包";
    public static final String TEXT_DISCOVERY = "发现";
    public static final String TEXT_FRIENDS = "朋友圈";
    public static final String TEXT_OPEN_HB = "拆红包";
    public static final String TEXT_I_KNOWN = "我知道了";
    public static final String TEXT_SEND = "发送";
    public static final String TEXT_SELECT_FROM_PHOTO = "相册";//从相册选择,小视频
    public static final String TEXT_SELECT_COMPLETE = "完成";
    //UI
    public static final String UI_LUANCHER = WECHAT_PACKAGE_NAME +".ui.LauncherUI";
    public static final String UI_OPEN_HB = WECHAT_PACKAGE_NAME +".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    public static final String UI_HB_DETATL = WECHAT_PACKAGE_NAME +".plugin.luckymoney.ui.LuckyMoneyDetailUI";
    public static final String UI_SNS_TIME_LINE = WECHAT_PACKAGE_NAME +".plugin.sns.ui.SnsTimeLineUI";
    public static final String UI_SNS_UPLOAD = WECHAT_PACKAGE_NAME +".plugin.sns.ui.SnsUploadUI";
    public static final String UI_PHOTO_VIDEO = WECHAT_PACKAGE_NAME +".ui.base.k";
    public static final String UI_AlbumPreview = WECHAT_PACKAGE_NAME +".plugin.gallery.ui.AlbumPreviewUI";
    public static final String UI_BASE = WECHAT_PACKAGE_NAME +".ui.base";
    public static final String UI_WxViewPager = WECHAT_PACKAGE_NAME +".ui.mogic.WxViewPager";

}
