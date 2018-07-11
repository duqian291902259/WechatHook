package site.duqian.autowechat.xposed.utils;


import java.io.File;

import de.robv.android.xposed.XSharedPreferences;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.wechat.WeChatHelper;

public class XSharedPrefUtil {

    private static final String TAG = XSharedPrefUtil.class.getSimpleName();

    private static XSharedPreferences instance = null;

    private static XSharedPreferences getInstance() {
        if (instance == null) {
            //shared_prefs();
            instance = new XSharedPreferences(Constant.packageName,Constant.PREFERENCE_NAME);
            instance.makeWorldReadable();
        } else {
            instance.reload();
        }
        return instance;
    }

    private static void shared_prefs() {
        String filePath = "/data/data/site.duqian.autowechat/shared_prefs/config.xml";
        File file = new File(filePath);
        if (file.exists()){
            LogUtils.debug(TAG,"filePath="+filePath);
        }
    }

    public static boolean isOpenRobotReply() {
        return getInstance().getBoolean("KEY_ROBOT_REPLEY", true);
    }

    public static boolean isOpenLuckyMoney() {
        return getInstance().getBoolean("SP_LUCKY_MONEY", true);
    }

    public static String getLastReplyContent() {
        return getInstance().getString("SP_LAST_REPLY","");
    }

    public static boolean isListenerOK() {
        return getInstance().getBoolean("SP_Listener_OK", false);
    }

    public static boolean isListenerMessage() {
        return getInstance().getBoolean("isListenerMessage", false);
    }

    public static boolean quickOpen() {
        return getInstance().getBoolean("quick_open", true);
    }

    public static boolean delay() {
        return getInstance().getBoolean("delay", true);
    }

    public static int delayMin() {
        return getInstance().getInt("min_value", 5);
    }

    public static int delayMax() {
        return getInstance().getInt("max_value", 20);
    }


    public static String  getFilterKeywords() {
        return getInstance().getString("filter_keywords", "stop，停，4");
    }

    public static int getDelayTime() {
        return WeChatHelper.getRandom(delayMin(), delayMax())*1000;//毫秒
    }

    public static int getLuckyMoneyDelayTime() {
        return WeChatHelper.getRandom(300, 888);//毫秒
    }

}


