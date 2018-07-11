package site.duqian.autowechat.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import site.duqian.autowechat.wechat.WechatUI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by duqian on 2017/1/17.
 */

public class SystemUtil {
    private static String TAG = SystemUtil.class.getSimpleName();
    private static Vibrator sVibrator;
    private static KeyguardManager sKeyguardManager;
    private static PowerManager sPowerManager;

    public static boolean  isProcessRunning(Context context,String processName) {
        //int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            String processName1 = appProcess.processName;
            if (processName1.equals(processName) ) {
                return true;
            }
            //LogUtils.debug(TAG,"pid ="+appProcess.pid+","+processName1);
            //pid =8642,com.tencent.mm
        }
        return false;
    }


    /**
     * 判断服务是否正在运行
     * @param context
     * @param clazz
     * @return
     */
    public static boolean isServiceRunning(Context context,Class clazz) {
        return isServiceRunning(context,clazz.getName());
    }
    public static boolean isServiceRunning(Context context,String className) {
        boolean isRunning = false;
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    public static String getTime() {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        dateFormat.applyPattern("HH:mm:ss:SS");
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static void screenShot(Activity activity){
        View viewRoot = activity.getWindow().getDecorView().getRootView();
        viewRoot.setDrawingCacheEnabled(true);
        Bitmap screenShotAsBitmap = Bitmap.createBitmap(viewRoot.getDrawingCache());
        viewRoot.setDrawingCacheEnabled(false);
        SimpleDateFormat sDateFormat    =   new  SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        String date = sDateFormat.format(new Date());
        FileUitls.saveBitmap2File(screenShotAsBitmap,"screenshot_"+ date+".jpg");
    }

    //声音或者震动
    public static void notifyGotMessage(Context context, String text) {
        if (text.contains(WechatUI.TEXT_LUCKY_MONEY)){
            SystemUtil.vibrator(context);
        }
        SystemUtil.sound(context);
    }
    /** 打开通知栏设置*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public void openNotificationServiceSettings(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInputMethod(Context context) {
        //自动弹出键盘
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideInputMethod(Context context, EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //强制隐藏Android输入法窗口
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }


    public static void copyText(Context context,String content){
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!TextUtils.isEmpty(content)) {
            cmb.setText(content); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
            CharSequence text = cmb.getText();
        }
    }

    /** 播放声音*/
    public static void sound(Context context) {
        try {
            MediaPlayer player = MediaPlayer.create(context,
                    Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 振动*/
    public static void vibrator(Context context) {
        if(sVibrator == null) {
            sVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        sVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
    }

    /** 是否为夜间*/
    public static  boolean isNightTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour >= 23 || hour < 7) {
            return true;
        }
        return false;
    }

    public static KeyguardManager getKeyguardManager(Context context) {
        if(sKeyguardManager == null) {
            sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return sKeyguardManager;
    }

    public static PowerManager getPowerManager(Context context) {
        if(sPowerManager == null) {
            sPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }
        return sPowerManager;
    }

    /** 是否为锁屏或黑屏状态*/
    public static boolean isLockScreen(Context context) {
        KeyguardManager km = getKeyguardManager(context);

        return km.inKeyguardRestrictedInputMode() || !isScreenOn(context);
    }

    public static boolean isScreenOn(Context context) {
        PowerManager pm = getPowerManager(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
