package site.duqian.wchook.base;

import android.app.Application;
import android.content.Context;

import site.duqian.wchook.utils.LogUtils;

/**
 * Created by duqian on 2017/5/8.
 */

public class MyApplication extends Application {

    public static Context mContext;
    public static final String  TAG ="MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
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
                LogUtils.debug(TAG,"app error:"+ex.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
