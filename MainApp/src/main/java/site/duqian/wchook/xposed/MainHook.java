package site.duqian.wchook.xposed;

import android.content.Context;

import site.duqian.wchook.utils.LogUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 16:29.
 * E-mail: duqian2010@gmail.com
 * Description:MainHook 入口函数
 * remarks:
 */
public class MainHook implements IXposedHookLoadPackage {
    private final String TAG = MainHook.class.getSimpleName();
    private static SettingsHelper mSettings = new SettingsHelper();
    private static String WECHAT_PACKAGE = "com.tencent.mm";
    private Context mContext;
    private HookMessage hookMessage;
    private NearbyHook nearbyHook;
    private CommonHook commonHook;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!WECHAT_PACKAGE.equals(lpparam.packageName)) {
            return;
        }
        LogUtils.debug("dq xposed wechat="+lpparam.packageName);
        ClassLoader classLoader = lpparam.classLoader;
        commonHook = CommonHook.getInstance();
        if (mContext == null) {
            mContext = commonHook.getContext();
            //获取wechat版本
            CommonHook.initWechatVersion(mContext);
            commonHook.showToast(mContext,"dq wechat hooked "+CommonHook.wechatVersionName);
        }

        CommonHook.hookTextView();

        CommonHook.markAllActivity();

        HookPosition.hookPostion(classLoader, mSettings);

        try {
            if (hookMessage==null) {
                hookMessage = new HookMessage(classLoader, mContext,mSettings);
            }
            hookMessage.hookConversationItem();
        } catch (Exception e) {
            LogUtils.debug(TAG, "dq hook error " + e);
        }

        try {
            if (nearbyHook==null) {
                nearbyHook = new NearbyHook(classLoader, mContext);
            }
            nearbyHook.hookSayHiModel();

        } catch (Exception e) {
            LogUtils.debug(TAG, "nearbyHook error " + e);
        }

    }

}