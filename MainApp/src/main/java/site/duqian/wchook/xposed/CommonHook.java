package site.duqian.wchook.xposed;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import site.duqian.wchook.utils.LogUtils;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 17:09.
 * E-mail: duqian2010@gmail.com
 * Description:HookUtil 工具类
 * remarks:
 */

public class CommonHook {
    private static final String TAG = CommonHook.class.getSimpleName();

    private static volatile CommonHook commonHook;

    private Handler handler;
    public static String wechatVersionName="";

    public CommonHook(){
        getHandler();
    }
    public static CommonHook getInstance() {
        if (commonHook == null) {
            synchronized (CommonHook.class) {
                if (commonHook == null) {
                    commonHook = new CommonHook();
                }
            }
        }
        return commonHook;
    }

    public void showToast(Context context,String content){
        getHandler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getHandler() {
        if (handler==null){
            handler=new Handler(Looper.getMainLooper());
        }
    }

    public Context getContext() {
        final Class<?> aClass = findClass("android.app.ActivityThread", null);
        Object[] object = new Object[0];
        final Object currentActivityThread = callStaticMethod(aClass, "currentActivityThread", object);
        return  (Context) callMethod(currentActivityThread, "getSystemContext", object);
    }


    public static void initWechatVersion(Context context) {
        if (context==null)return;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(VersionParam.PACKAGE_NAME, 0);
            if (pInfo != null) {
                wechatVersionName = pInfo.versionName;
                VersionParam.init(wechatVersionName);
                LogUtils.debug(TAG,"wechatVersionName="+ wechatVersionName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.debug(TAG, "initWechatVersion error " + e.toString());
        }
    }


    public static void hookTextView() {
        findAndHookMethod(TextView.class, "setText", CharSequence.class,
                TextView.BufferType.class, boolean.class, int.class, textMethodHook);
    }

    private static XC_MethodHook textMethodHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {

            try {
                Object object = methodHookParam.args[0];
                if (object == null) return;
                String abc = "";
                if (!object.getClass().getSimpleName().contains("SpannableString")) {
                    abc = (String) object;
                    if (!TextUtils.isEmpty(abc)) {
                        abc = ReplaceText(new String[]{"通讯录","Contacts","游戏", "扫一扫"},
                                new String[]{"杜乾","杜乾","被Android Developer 杜乾Hook了", "duqian2010@gmail.com"}, 4, abc);
                        methodHookParam.args[0] = abc;
                    }
                }
            } catch (Exception e) {
                //LogUtils.debug(TAG,"textMethodHook error "+e);
            }
        }
    };


    private static String ReplaceText(String[] oristr, String[] newstr, int num, String abc) {
        for (int i = 0; i < num; i++) {
            if (!oristr[i].equals(""))
                abc = abc.replaceAll(oristr[i], newstr[i]);
        }
        return abc;
    }


    public static String currentActivityName = "";

    public static void markAllActivity() {
        findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object thisObject = param.thisObject;
                String name = thisObject.getClass().getName();
                LogUtils.debug(TAG, " Activity= " + name);
                currentActivityName = name;
            }
        });
    }

    public static void log(String TAG, String s) {
        LogUtils.debug(TAG, s);
        XposedBridge.log(s);
    }

    public static void hook_method(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        try {
            findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    public static void hook_method(String className, ClassLoader classLoader, String methodName,
                                   Object... parameterTypesAndCallback) {
        try {
            findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    public static void hook_methods(String className, String methodName, XC_MethodHook xmh) {
        try {
            Class<?> clazz = Class.forName(className);

            for (Method method : clazz.getDeclaredMethods())
                if (method.getName().equals(methodName)
                        && !Modifier.isAbstract(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers())) {
                    XposedBridge.hookMethod(method, xmh);
                }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

}
