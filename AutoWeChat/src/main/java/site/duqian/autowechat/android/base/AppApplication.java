package site.duqian.autowechat.android.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import site.duqian.autowechat.utils.CrashHandler;
import site.duqian.autowechat.utils.LogUtils;


/**
 * Created by Du Qian 2015/4/3.
 */
public class AppApplication extends Application {

    private static AppApplication ba;
    private static int mMainThreadId = -1;
    public static Handler mMainThreadHandler;
    public  Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mMainThreadId = android.os.Process.myTid();
        mMainThreadHandler = new Handler();
        ba = this;
        mContext = getApplicationContext();
        //设置未捕获异常的处理器
        Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHandler());
        //LeakCanary.install(this);//leaks
    }

    private class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        //当线程出现了未捕获的异常执行的方法。
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            try {
                LogUtils.d("application exception:");
                CrashHandler.getInstance().saveCrashInfo2File(ex);
                // 退出程序,重启启动程序代码
                restartApp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void restartApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static AppApplication getInstance() {
        if (null == ba) {
            ba = new AppApplication();
        }
        return ba;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }
}