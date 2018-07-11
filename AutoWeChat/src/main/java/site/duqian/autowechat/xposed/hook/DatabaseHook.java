package site.duqian.autowechat.xposed.hook;

import android.content.ContentValues;
import android.content.Context;

import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.xposed.VersionParam;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 *
 * Created by duqian on 2017/3/16.
 */

public class DatabaseHook  {

    private static final String TAG = DatabaseHook.class.getSimpleName();

    public static void hookDatabase(ClassLoader classLoader, Context mContext) {

        XposedHelpers.findAndHookConstructor(VersionParam.storageClass, classLoader, VersionParam.storageMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                LogUtils.debug(TAG,"db="+param.args[0]);
            }
        });

        /*
        public final long insertWithOnConflict(String str, String str2, ContentValues contentValues, int i)
         */
        XposedHelpers.findAndHookMethod(VersionParam.SQLiteDatabaseClass, classLoader, "insertWithOnConflict",
            String.class, String.class, ContentValues.class, Integer.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    LogUtils.debug(TAG,"insertWithOnConflict="+param.args[0]);
                    String p1 = String.valueOf(param.args[0]);
                    String p2 =  String.valueOf(param.args[1]);
                    ContentValues p3 = (ContentValues) param.args[2];
                    int p4 = (int) param.args[3];
                    LogUtils.debug(TAG,"DB => insert p1 = "+p1+", p2 = "+p2+", p3 = "+p3.toString()+", p4 = "+p4);

                }});

        /*
        public final int updateWithOnConflict(String str, ContentValues contentValues, String str2, String[] strArr, int i)
         */
        XposedHelpers.findAndHookMethod(VersionParam.SQLiteDatabaseClass, classLoader, "updateWithOnConflict",
            String.class,  ContentValues.class, String.class, String[].class,Integer.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    LogUtils.debug(TAG,"updateWithOnConflict="+param.args[0]);

                    String p1 = String.valueOf(param.args[0]);
                    ContentValues p2 = (ContentValues) param.args[1];
                    String p3 =  String.valueOf(param.args[2]);
                    String[] p4 = (String[]) param.args[3];
                    int p5 = (int) param.args[4];
                    LogUtils.debug(TAG,"DB => insert p1 = "+p1+", p3 = "+p3+", p2 = "+p2.toString()+", p4 = "+p4+", p5 = "+p5);

             }});

         /*
        public final int delete(String str, String str2, String[] strArr)
         */
        XposedHelpers.findAndHookMethod(VersionParam.SQLiteDatabaseClass, classLoader, "delete",
                String.class, String.class, String[].class,new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        LogUtils.debug(TAG,"delete="+param.args[0]);
                        String p1 = String.valueOf(param.args[0]);
                        String p2 =  String.valueOf(param.args[1]);
                        String[] p3 = (String[]) param.args[2];
                        LogUtils.debug(TAG,"DB => insert p1 = "+p1+", p2 = "+p2+", p3 = "+p3.toString());

                    }});
         /*
        private int executeSql(String str, Object[] objArr)
         */
        XposedHelpers.findAndHookMethod(VersionParam.SQLiteDatabaseClass, classLoader, "executeSql",
                String.class, Object[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        LogUtils.debug(TAG,"executeSql="+param.args[0]);
                        super.beforeHookedMethod(param);
                        String p1 = String.valueOf(param.args[0]);
                        Object[] p2 = (Object[]) param.args[1];
                        LogUtils.debug(TAG,"DB => insert p1 = "+p1+", p2 = "+p2.toString());

                    }});



    }
}
