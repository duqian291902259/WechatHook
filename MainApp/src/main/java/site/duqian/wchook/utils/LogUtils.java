package site.duqian.wchook.utils;

import android.util.Log;

/**
 * 调试时打印Log，为了安全，App发布后，改为false;
 * @author duqian
 */
public class LogUtils {


	private static boolean debug = true;//debug测试时为,true,发布后改为：false;


	public static final int LEVEL_NONE = 0;
	public static final int LEVEL_VERBOSE = 1;
	public static final int LEVEL_DEBUG = 2;
	public static final int LEVEL_INFO = 3;
	public static final int LEVEL_WARN = 4;
	public static final int LEVEL_ERROR = 5;
	private static String mTag = "duqian";
	private static int mDebuggable = LEVEL_ERROR;

	public static void v(String msg) {
		if (mDebuggable >= LEVEL_VERBOSE) {
			Log.v(mTag, msg);
		}
	}

	/** 以级别为 d 的形式输出LOG */
	public static void d(String msg) {
		if (mDebuggable >= LEVEL_DEBUG) {
			Log.d(mTag, msg);
		}
	}

	public static void i(String msg) {
		if (mDebuggable >= LEVEL_INFO) {
			Log.i(mTag, msg);
		}
	}

	public static void w(String msg) {
		if (mDebuggable >= LEVEL_WARN) {
			Log.w(mTag, msg);
		}
	}

	public static void e(String msg) {
		if (mDebuggable >= LEVEL_ERROR) {
			Log.e(mTag, msg);
		}
	}



	//设置全局的Log
    public static  void debug(String Tag,String value){
		LogUtils.d(Tag+" "+value);
	}

    public static  void debug(String value){
        LogUtils.d("duqian "+value);
    }

    public static  void debug(Object Tag, String value){
        LogUtils.d(Tag,value);
    }


    //object
    public static void i(Object obj,String info){
        if (debug) {
            Log.i(obj.getClass().getSimpleName(),info);
        }
    }
    public static void v(Object obj,String info){
        if (debug) {
            Log.v(obj.getClass().getSimpleName(),info);
        }
    }
    public static void e(Object obj,String info){
        if (debug) {
            Log.e(obj.getClass().getSimpleName(),info);
        }
    }
    public static void d(Object obj,String info){
        if (debug) {
            Log.d(obj.getClass().getSimpleName(),info);
        }
    }
    public static void w(Object obj,String info){
        if (debug) {
            Log.w(obj.getClass().getSimpleName(),info);
        }
    }
}
