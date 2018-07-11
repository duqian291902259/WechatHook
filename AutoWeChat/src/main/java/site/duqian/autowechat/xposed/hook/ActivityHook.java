package site.duqian.autowechat.xposed.hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import site.duqian.autowechat.android.receiver.XposedReceiver;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.xposed.VersionParam;
import site.duqian.autowechat.xposed.XBroadCastHandler;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * hook 微信LauncherUI 注册广播接受者 方便与本应用组件进行交互，broadcast和service相互唤醒
 * Created by duqian on 2017/3/8.
 */

public class ActivityHook extends BaseHook{

    private static final String TAG = ActivityHook.class.getSimpleName();

    private final XBroadCastHandler mXBroadCastHandler ;

    private Context mContext;

    public ActivityHook(ClassLoader classLoader,Context context) {
        super(classLoader);
        mContext = context;
        mXBroadCastHandler = XBroadCastHandler.getInstance();
        //LogUtils.debug(TAG,"ActivityHook init ");
    }

    public void hookLauncherUI() {
        XposedHelpers.findAndHookMethod(VersionParam.activity_launcher_ui, classLoader, "onCreate", Bundle.class, new ActivityOnCreateHook());
        findAndHookMethod(VersionParam.activity_launcher_ui, classLoader,"onDestroy",  new ActivityOnDestroyHook());
    }

    private class ActivityOnCreateHook extends XC_MethodHook {

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Activity activity = (Activity) param.thisObject;
            if (activity != null) {
                LogUtils.debug(TAG,"hookLauncherUI ");
                HookUtils.showText(mContext, "hook LauncherUI");
                mXBroadCastHandler.onCreate(activity);
                //BroadcastUtil.sendXposedBroadcast(activity.getApplicationContext());
                //sendBroadcast(activity);
            }

        }
    }

    private void sendBroadcast(Activity activity) {
        try {
            Intent intent = new Intent(XposedReceiver.ACTION);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            activity.sendBroadcast(intent);
            LogUtils.debug(TAG,"hook LauncherUI sendBroadcast ");
        } catch (Throwable e) {
            LogUtils.debug(TAG,"hook LauncherUI sendBroadcast error "+e.toString());
        }
    }

    private class ActivityOnDestroyHook extends XC_MethodHook {

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            Activity activity = (Activity) param.thisObject;
            mXBroadCastHandler.onDestory(activity);
        }
    }

}
