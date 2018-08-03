package site.duqian.wchook.xposed;

/**
 * Created by duqian on 2017/5/10.
 */

public class VersionParam {

    public static final String PACKAGE_NAME = "com.tencent.mm";

    //conversation
    public static String conversationClass = PACKAGE_NAME+".e.b.ag";
    public static String con_GetCursorMethod = "b";
    public static String con_NetworkMethod = "a";
    public static String con_MessageClass = PACKAGE_NAME+".modelmulti.i";
    public static String networkRequest = PACKAGE_NAME+".model.ak";
    public static String requestMethod = "vw";

    //nearby friends
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
                con_NetworkMethod = "a";//a
                con_MessageClass = PACKAGE_NAME+".modelmulti.i";
                networkRequest = PACKAGE_NAME+".model.ak";
                requestMethod = "vw";
                con_NetworkMethod = "a";

                SayHiModel= PACKAGE_NAME+".pluginsdk.model.m";
                BaseRequestBean = PACKAGE_NAME+".v.k";
                NEARBY_PROTOCAL = ".protocal.c.afk";
                NEARBY_PROTOCAL_POSITION = ".mm.plugin.nearby.a.d";
                NEARBY_PROTOCAL_POSITION2 = ".mm.plugin.nearby.a.c";
                break;
            case "6.6.7":
                con_MessageClass = PACKAGE_NAME+".modelmulti.i";
                conversationClass = PACKAGE_NAME+".g.c.am";
                con_GetCursorMethod = "d";

                networkRequest = PACKAGE_NAME+".model.au";
                requestMethod = "DF";
                con_NetworkMethod = "a";
                break;
}
    }
}
