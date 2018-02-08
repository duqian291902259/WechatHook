package site.duqian.wchook.android;

import android.content.Context;
import android.text.TextUtils;

import site.duqian.wchook.common.ApiUtil;
import site.duqian.wchook.model.NonoCallBack;
import site.duqian.wchook.utils.AdbUtil;
import site.duqian.wchook.utils.CommandResult;
import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.ShellUtils;
import site.duqian.wchook.utils.SystemUtil;
import site.duqian.wchook.utils.ThreadManager;
import site.duqian.wchook.utils.ToastUtil;
import site.duqian.wchook.wechat.WechatUI;
import site.duqian.wchook.xposed.SettingsHelper;

/**
 * Created by duqian on 2017/5/11.
 */

public class TestFun {
    private static final String TAG = TestFun.class.getSimpleName();

    private static TestFun testFun = null;
    private static SettingsHelper mSettings = null;
    private static Context mContext;

    public static TestFun init(Context context,SettingsHelper settings) {
        if (testFun == null) {
            synchronized (SettingsHelper.class) {
                if (testFun == null) {
                    testFun = new TestFun(context,settings);
                }
            }
        }
        return testFun;
    }
    public TestFun(Context context,SettingsHelper settings){
        mSettings = settings;
        mContext = context;
    }

    public void testRequest() {
        ApiUtil.init().askRobot("你好", new NonoCallBack() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    String reply = "reply = " + result ;
                    LogUtils.debug(TAG, reply);
                    ToastUtil.toast(mContext,reply);
                }else{
                    LogUtils.debug(TAG,"reply = null");
                }
            }
        });
    }

    public void requestShellPermissions() {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                String cmd = "adb version";
                String move = "input swipe 367 300 367 50";
                String[] cmds = new String[]{cmd,move};
                CommandResult commandResult =  ShellUtils.execCommand(cmd, true);
                int result  = commandResult.getResult();
                if (result!=0){
                    ToastUtil.toast(mContext,"没有获取root权限！功能受限！");
                }
                boolean xposedInstalled = SystemUtil.isXposedInstalled(mContext);
                if (!xposedInstalled){
                    ToastUtil.toast(mContext,"尚未安装xposed框架!功能受限");
                }

                LogUtils.debug(TAG, "xposedInstalled="+xposedInstalled+",shell result = "+commandResult);
            }
        });
    }


    public void closeWX() {
        AdbUtil.killProcess(WechatUI.WECHAT_PACKAGE_NAME);
    }
}
