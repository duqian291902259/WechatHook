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
    private static String wechat_package = "com.tencent.mm";
    private Context mContext;
    private HookMessage hookMessage;
    private NearbyHook nearbyHook;
    private CommonHook commonHook;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!wechat_package.equals(lpparam.packageName)) {
            return;
        }
        LogUtils.debug("nono xposed "+lpparam.packageName);
        ClassLoader classLoader = lpparam.classLoader;
        commonHook = CommonHook.getInstance();
        if (mContext == null) {
            mContext = commonHook.getContext();
            //获取wechat版本
            CommonHook.initWechatVersion(mContext);
            commonHook.showToast(mContext,"nono wechat "+CommonHook.wechatVersionName);
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
            LogUtils.debug(TAG, "hook error " + e);
        }

        try {
            if (nearbyHook==null) {
                nearbyHook = new NearbyHook(classLoader, mContext);
            }
            nearbyHook.hookSayHiModel();
            /*nearbyHook.hookNearbyNetCallBack();
            nearbyHook.hookSendGreeting("v1_214c8c42adcd20e738d4dae2abaa9edd88d020a4cbb1cfb4c8a6cf62d67d6a9f56ac1cf4888d7bc86ea23cc8b08ad63c@stranger","hello ,greeting u",0);
            if (VersionParam.NearbyFriendsUI.equals(currentActivityName)) {
                nearbyHook.hookGetFriendsByPosition(114.248529, 22.233347);
            }*/
        } catch (Exception e) {
            LogUtils.debug(TAG, "nearbyHook error " + e);
        }

    }

}