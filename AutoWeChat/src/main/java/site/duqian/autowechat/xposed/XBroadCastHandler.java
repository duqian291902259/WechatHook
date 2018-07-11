package site.duqian.autowechat.xposed;

import android.app.Activity;
import android.content.IntentFilter;

import site.duqian.autowechat.android.receiver.XposedReceiver;
import site.duqian.autowechat.utils.LogUtils;

/**
 * 辅助类，绑定luancherUI对应的生命周期，实现广播的注册与解绑
 * Created by duqian on 2017/3/8.
 */

public class XBroadCastHandler {

    public static final String TAG = XBroadCastHandler.class.getSimpleName();

    private static volatile XBroadCastHandler xBroadCastHandler;
    public static  XBroadCastHandler getInstance(){
        if (xBroadCastHandler==null){
            synchronized (XBroadCastHandler.class){
                if (xBroadCastHandler==null){
                    xBroadCastHandler = new XBroadCastHandler();
                }
            }
        }
        return xBroadCastHandler;
    }

    private XposedReceiver receiver;
    public void onCreate(Activity activity){
        try {
            receiver = new XposedReceiver();
            IntentFilter filter = new IntentFilter(XposedReceiver.ACTION);
            //Application application = activity.getApplication();
            activity.registerReceiver(receiver, filter);
            LogUtils.debug(TAG,"onCreate ");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onDestory(Activity activity){
        try {
            if (receiver!=null) {
                //Application application = activity.getApplication();
                activity.unregisterReceiver(receiver);
                LogUtils.debug(TAG,"unregisterReceiver");
            }else{
                LogUtils.debug(TAG,"onDestory null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        LogUtils.debug(TAG,"onDestory ");
    }

}
