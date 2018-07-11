package site.duqian.autowechat.xposed.hook;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.xposed.VersionParam;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static android.widget.Toast.LENGTH_LONG;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by duqian on 2017/2/21.
 */

public class HookUtils {
    private static final String TAG = HookUtils.class.getSimpleName();
    public static final String HOOK_PACKAGE_NAME = "com.tencent.mm";
    public static boolean IS_XPOSED_OPENED = true;

    private static volatile HookUtils hookUtils;
    public static  HookUtils getInstance(){
        if (hookUtils==null){
            synchronized (HookUtils.class){
                if (hookUtils==null){
                    hookUtils = new HookUtils();
                }
            }
        }
        return hookUtils;
    }

    public Context getContext() {
        final Class<?> aClass = findClass("android.app.ActivityThread", null);
        final Object currentActivityThread = callStaticMethod(aClass, "currentActivityThread", new Object[0]);
        Context context = (Context) callMethod(currentActivityThread, "getSystemContext", new Object[0]);
        return context;
    }

    public static void getApplicationContext(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> ContextClass = findClass("android.content.ContextWrapper", lpparam.classLoader);
            findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context context  = (Context) param.getResult();
                    XposedBridge.log("getApplicationContext");
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("getApplicationContext error"+t);
        }
    }

    public static void initWechatVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(HOOK_PACKAGE_NAME, 0);
            if (pInfo != null) {
                String wechatVersionName = pInfo.versionName;
                LogUtils.debug(TAG,"wechat version:" + wechatVersionName);
                VersionParam.init(wechatVersionName);
                showText(context, wechatVersionName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.debug(TAG,"initWechatVersion error "+e.toString() );
        }
    }

    public static void showText(Context context, String content) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //LogUtils.debug(TAG,"showText" );
                Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void hookApplication(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("android.app.Application", classLoader, "onCreate", new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.thisObject;
                LogUtils.debug(TAG,"HookApplication before");

            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                LogUtils.debug(TAG,"HookApplication after ");
            }
        });
    }


    public static void hookWechatId(ClassLoader classLoader){
        getInfosFromUI(classLoader,VersionParam.activity_chatroomInfoUI);
        getInfosFromUI(classLoader,VersionParam.activity_contactInfoUI);
    }

    private static void getInfosFromUI(ClassLoader classLoader,String activity_name) {
        findAndHookMethod(activity_name, classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                String wechatId = activity.getIntent().getStringExtra("Contact_User");
                cmb.setText(wechatId);
                LogUtils.debug(TAG, " 微信ID:" + wechatId  );
                Toast.makeText(activity, "微信ID:" + wechatId, LENGTH_LONG).show();
            }
        });
    }

    public void hookTextView(){
        //LogUtils.debug(TAG,"hookTextView");
        findAndHookMethod(TextView.class, "setText", CharSequence.class,
                TextView.BufferType.class, boolean.class, int.class, textMethodHook);
    }

    private XC_MethodHook textMethodHook  = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {

            try {
                Object object = methodHookParam.args[0];
                if (object==null)return;
                String abc = "";
                if (!object.getClass().getSimpleName().contains("SpannableString")){
                    abc = (String) object;
                    if (!TextUtils.isEmpty(abc)) {
                        //LogUtils.debug(TAG,"原文本："+abc);
                        abc=ReplaceText(new String[]{"摇一摇","游戏","通讯录"},
                                new String[]{"Android Developer","duqian2010@gmail.com","杜小菜"},3, abc);
                        methodHookParam.args[0] = abc;
                    }
                }
                //CharSequence actualText = (CharSequence) object;
                //String abc = actualText.toString();
            }catch (Exception e){
                LogUtils.debug(TAG,"textMethodHook error "+e);
            }
        }
    };


    private String ReplaceText(String[] oristr, String[] newstr, int num, String abc){
        for(int i=0; i<num ; i++){
            if (!oristr[i].equals(""))
                abc=abc.replaceAll(oristr[i],newstr[i]);
        }
        return abc;
    }


    public static boolean debugApps = true ;
    public static final int DEBUG_ENABLE_DEBUGGER = 0x1;


    private static void hookDebug(XC_LoadPackage.LoadPackageParam lpparam) {
        if (debugApps){
            if(lpparam.appInfo == null || (lpparam.appInfo.flags &
                    (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) !=0){
                //LogUtils.debug(TAG,"hookDebugger "+lpparam.packageName);
                HookUtils.hookDebugger();
            }
        }
    }

    public static void hookDebugger(){
        XposedBridge.hookAllMethods(Process.class, "start", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int id = 5;
                int flags = (Integer) param.args[id];
                LogUtils.debug(TAG,"flags is : "+flags);
                if (debugApps) {
                    if ((flags & DEBUG_ENABLE_DEBUGGER) == 0) {
                        flags |= DEBUG_ENABLE_DEBUGGER;
                    }
                }
                param.args[id] = flags;
                LogUtils.debug(TAG,"flags changed : "+flags);

            }
        });
    }

}
