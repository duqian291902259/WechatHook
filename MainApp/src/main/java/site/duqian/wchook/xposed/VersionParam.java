package site.duqian.wchook.xposed;

/**
 * Created by duqian on 2017/5/10.
 */

public class VersionParam {

    public static final String PACKAGE_NAME = "com.tencent.mm";

    //conversation
    public static String conversationClass = PACKAGE_NAME+".e.b.ag";
    public static String con_GetCursorMethod = "b";
    public static String con_MessageClass1 = PACKAGE_NAME+".ab.a";//message
    public static String con_NetworkMethod = "a";//a
    public static String con_MessageClass = PACKAGE_NAME+".modelmulti.i";
    public static String activity_chatingui = PACKAGE_NAME+".ui.chatting.ChattingUI";
    public static String activity_chatingui_a = activity_chatingui+".a";
    public static String networkRequest = PACKAGE_NAME+".model.ak";
    public static String requestMethod = "vw";

    //nearby friends
    public static String NearbyFriendsUI = PACKAGE_NAME+".plugin.nearby.ui.NearbyFriendsUI";
    public static String ContactInfoUI = PACKAGE_NAME+".plugin.profile.ui.ContactInfoUI";
    public static String SayHiEditUI = PACKAGE_NAME+".ui.contact.SayHiEditUI";
    public static String SayHiModel= PACKAGE_NAME+".pluginsdk.model.m";//打招呼
    public static String BaseRequestBean = PACKAGE_NAME+".v.k";
    public static String NEARBY_CALLBACK_METHOD = "a";
    public static String NEARBY_PROTOCAL = ".protocal.c.afk";
    public static String NEARBY_PROTOCAL_POSITION = ".mm.plugin.nearby.a.d";
    public static String NEARBY_PROTOCAL_POSITION2 = ".mm.plugin.nearby.a.c";

    public static void init(String version) {
        switch (version) {
            case "6.3.32":
                //message
                conversationClass = PACKAGE_NAME+".e.b.ag";
                con_GetCursorMethod = "b";
                con_MessageClass1 = PACKAGE_NAME+".ab.a";
                con_NetworkMethod = "a";//a
                con_MessageClass = PACKAGE_NAME+".modelmulti.i";
                activity_chatingui = PACKAGE_NAME+".ui.chatting.ChattingUI";
                activity_chatingui_a = activity_chatingui+".a";
                networkRequest = PACKAGE_NAME+".model.ak";
                requestMethod = "vw";

                SayHiModel= PACKAGE_NAME+".pluginsdk.model.m";
                BaseRequestBean = PACKAGE_NAME+".v.k";
                NEARBY_PROTOCAL = ".protocal.c.afk";
                NEARBY_PROTOCAL_POSITION = ".mm.plugin.nearby.a.d";
                NEARBY_PROTOCAL_POSITION2 = ".mm.plugin.nearby.a.c";
                break;
            case "6.6.7":
                //message
                con_MessageClass = PACKAGE_NAME+".modelmulti.i";
                activity_chatingui = PACKAGE_NAME+".ui.chatting.ChattingUI";
                activity_chatingui_a = activity_chatingui+".a";
                conversationClass = PACKAGE_NAME+".e.b.ag";
                con_GetCursorMethod = "b";
                con_MessageClass1 = PACKAGE_NAME+".ab.a";

                networkRequest = PACKAGE_NAME+".model.au";
                requestMethod = "DF";
                con_NetworkMethod = "a";
                break;
}
    }
}
