package site.duqian.autowechat.xposed;

import android.content.Context;

import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.xposed.hook.ActivityHook;
import site.duqian.autowechat.xposed.hook.HookUtils;
import site.duqian.autowechat.xposed.hook.LuckyMoneyHook;
import site.duqian.autowechat.xposed.hook.MessageHook;
import site.duqian.autowechat.xposed.utils.XposedUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static site.duqian.autowechat.xposed.hook.HookUtils.initWechatVersion;

/**
 * xposed hook 入口
 * Created by duqian on 2017/2/20.
 */

public class Main implements IXposedHookLoadPackage,IXposedHookInitPackageResources {

    private static final String TAG = Main.class.getSimpleName();
    private Context mContext = null;
    private MessageHook messageHook;
    public static List<String> list_msg = new ArrayList<>();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!HookUtils.HOOK_PACKAGE_NAME.equals(lpparam.packageName)){
            return;
        }
        //LogUtils.debug(TAG,"handleLoadPackage "+lpparam.packageName);
        ClassLoader classLoader = lpparam.classLoader;
        if (mContext==null) {
            mContext = HookUtils.getInstance().getContext();
        }
        //获取wechat版本
        initWechatVersion(mContext);

        HookUtils.getInstance().hookTextView();

        XposedUtil.markAllActivity();

        new ActivityHook(classLoader, mContext).hookLauncherUI();

        try {
            if (messageHook==null) {
                messageHook = new MessageHook(classLoader, mContext);
            }
            //messageHook.hookChattingUI_A();
            messageHook.hookConversationItem();
        }catch (Exception e){
            LogUtils.debug(TAG,"MessageHook error "+e);
        }

        new LuckyMoneyHook(classLoader, mContext).hookLuckyMoney();

       /* try {
            DatabaseHook.hookDatabase(classLoader, mContext);
        }catch (Exception e){
            LogUtils.debug(TAG,"hookDatabase error "+e);
        }*/

    }


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!HookUtils.HOOK_PACKAGE_NAME.equals(resparam.packageName)){
            return;
        }
        //LogUtils.debug(TAG,"handleInitPackageResources "+resparam.res.getPackageName());
    }

}
