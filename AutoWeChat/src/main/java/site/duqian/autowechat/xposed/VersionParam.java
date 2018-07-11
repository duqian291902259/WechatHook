package site.duqian.autowechat.xposed;


public class VersionParam {
    public static final String PACKAGE_NAME = "com.tencent.mm";

    //UI
    public static String activity_launcher_ui =  PACKAGE_NAME+".ui.LauncherUI";
    public static String activity_contactInfoUI =  PACKAGE_NAME+".plugin.profile.ui.ContactInfoUI";
    public static String activity_chatroomInfoUI =  PACKAGE_NAME+".plugin.chatroom.ui.ChatroomInfoUI";

    // database
    public static String SQLiteDatabaseClass = "com.tencent.mmdb.database.SQLiteDatabase";
    public static String storageClass = PACKAGE_NAME +".storage.q";
    public static String storageMethod = PACKAGE_NAME +".bh.g";//MicroMsg.SqliteDB


    //luckymoney
    public static String receiveUIFunctionName = "d";
    public static String receiveUIParamName = PACKAGE_NAME+".v.k";
    public static String networkRequest = PACKAGE_NAME+".model.ak";
    public static String getNetworkByModelMethod = "vw";
    public static String getMessageClass = PACKAGE_NAME+".e.b.by";
    public static boolean hasTimingIdentifier = false;

    //conversation
    public static String conversationClass = PACKAGE_NAME+".e.b.ag";
    public static String con_GetCursorMethod = "b";
    public static String con_MessageClass1 = PACKAGE_NAME+".ab.a";
    public static String con_NetworkMethod = "a";//a
    public static String con_MessageClass = PACKAGE_NAME+".modelmulti.i";
    public static String activity_chatingui = PACKAGE_NAME+".ui.chatting.ChattingUI";
    public static String activity_chatingui_a = activity_chatingui+".a";

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

                //luckymoney
                receiveUIFunctionName = "d";
                receiveUIParamName = PACKAGE_NAME+".v.k";
                networkRequest = PACKAGE_NAME+".model.ak";
                getNetworkByModelMethod = "vw";
                getMessageClass = PACKAGE_NAME+".e.b.by";
                break;

        }
    }
}
