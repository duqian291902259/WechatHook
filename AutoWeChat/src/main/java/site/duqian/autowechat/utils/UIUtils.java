package site.duqian.autowechat.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import site.duqian.autowechat.android.base.AppApplication;

import java.io.DataOutputStream;

public class UIUtils {

    private static final String TAG = UIUtils.class.getSimpleName();

    public static Context getContext() {
		return AppApplication.getInstance();
	}

	public static long getMainThreadId() {
		return AppApplication.getMainThreadId();
	}


	/** dip转换px */
	public static int dip2px(Context context,int dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/** pxz转换dip */
	public static int px2dip(Context context,int px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/** 获取主线程的handler */
	public static Handler getHandler() {
		return AppApplication.getMainThreadHandler();
	}

	/** 延时在主线程执行runnable */
	public static boolean postDelayed(Runnable runnable, long delayMillis) {
		return getHandler().postDelayed(runnable, delayMillis);
	}

	/** 在主线程执行runnable */
	public static boolean post(Runnable runnable) {
		return getHandler().post(runnable);
	}


	public static View inflate(int resId){
		return LayoutInflater.from(getContext()).inflate(resId,null);
	}

	/** 获取资源 */
	public static Resources getResources() {
		return getContext().getResources();
	}

	/** 获取文字 */
	public static String getString(int resId) {
		return getResources().getString(resId);
	}


	//判断当前的线程是不是在主线程 
	public static boolean isRunInMainThread() {
		return android.os.Process.myTid() == getMainThreadId();
	}
    
	public static void runInMainThread(Runnable runnable) {
		if (isRunInMainThread()) {
			runnable.run();
		} else {
			post(runnable);
		}
	}

    //adb命令 滑动屏幕
    public static void swipeScreen(int left,int top,int right,int bottom){
        //adb push core code
        String command = "adb shell input swipe " + left + top + right + bottom;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");// the phone must be root,it can exctue the adb command
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.debug(TAG,"swipe error "+e.toString());
        }
    }
    //adb命令 向上滑动屏幕
    public static void swipeUp(){
        swipeScreen(50,500,50,20);
        LogUtils.debug(TAG,"swipeUp");
    }

    //adb命令 屏幕截图
    public static void screenshot(){
        try {
            String command = "adb shell /system/bin/screencap -p "+ Environment.getExternalStorageDirectory()+"screenshot.png";
            Process process = null;
            DataOutputStream os = null;
            process = Runtime.getRuntime().exec("su");// the phone must be root,it can exctue the adb command
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.debug(TAG,"swipe error "+e.toString());
        }
    }


}


