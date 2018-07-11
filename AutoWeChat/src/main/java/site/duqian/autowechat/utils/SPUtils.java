package site.duqian.autowechat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import site.duqian.autowechat.model.Constant;

/**
 * 保存各种配置信息
 *  //SharedPreferences
 * @author duqian
 */
public class SPUtils {

    private static SharedPreferences sp;
    //private static String SP_NAME = "config";
    private static String SP_NAME = Constant.PREFERENCE_NAME;
    //private static int MODE = Context.MODE_PRIVATE;
    private static int MODE = Context.MODE_WORLD_READABLE;

    public static void putBoolean(Context context, String key, Boolean value) {
        getSP(context);
        sp.edit().putBoolean(key, value).apply();//不能放里面,fuck
    }

    private static void getSP(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, MODE);
        }
    }

    public static Boolean getBoolean(Context context, String key, Boolean defValue) {
        getSP(context);
        return sp.getBoolean(key, defValue);
    }

    public static void putString(Context context, String key, String value) {
        getSP(context);
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defValue) {
        getSP(context);
        return sp.getString(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        getSP(context);
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int defValue) {
        getSP(context);
        return sp.getInt(key, defValue);
    }

    public static void putLong(Context context, String key, long value) {
        getSP(context);
        sp.edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key, long defValue) {
        getSP(context);
        return sp.getLong(key, defValue);
    }


}
