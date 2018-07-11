package site.duqian.autowechat.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import site.duqian.autowechat.R;

import java.util.Random;

/**
 * Created by duqian on 16/01/23.
 * Android通知栏封装
 */
public class NotificationUtils {

    private static final int SmallIcon = R.mipmap.ic_launcher;
    public static final int NotificationNumber = 1;
    public static final int RequestCode = 0;
    private static NotificationManager mManager;
    private static NotificationCompat.Builder mBuilder;
    private static final Random RANDOM = new Random();

    /**
     * 获取Builder
     */
    public static NotificationCompat.Builder getBuilder(Context context) {
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setNumber(NotificationNumber)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true);
              //.setDefaults(Notification.DEFAULT_VIBRATE);
        return mBuilder;
    }

    /**
     * 获取NotificationManager
     */
    public static NotificationManager getManager(Context context) {

        if (mManager == null) {
            synchronized (NotificationUtils.class) {
                if (mManager == null) {
                    mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                }
            }
        }
        return mManager;
    }


    /**
     * 显示普通的通知
     */
    public static void showOrdinaryNotification(Context context, String title, String text, String ticker,
                                                int icon, int channel) {
        mBuilder = getBuilder(context);
        mManager = getManager(context);
        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setContentIntent(getDefalutIntent(context, Notification.FLAG_AUTO_CANCEL))
                .setNumber(NotificationNumber)//显示数量
                .setTicker(ticker)//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 //DEFAULT_VIBRATE requires VIBRATE permission
                .setSmallIcon(SmallIcon)
        ;
        Notification mNotification = mBuilder.build();
        mNotification.icon = icon;
        mManager.notify(dealWithId(channel), mNotification);
    }

    /**
     * Intent 中可以包含很多参数、功能
     * 如：  页面启动、跳转、
     * 安装apk
     */
    public static void showIntentNotification(Context context, String title, String text, String ticker,
                                              Intent resultIntent, int icon, int channel, int defaults) {
        mBuilder = getBuilder(context);
        mManager = getManager(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, RequestCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setTicker(ticker)
                .setSmallIcon(SmallIcon)
                .setContentIntent(pendingIntent);
        if (defaults>0){
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }
        Notification mNotification = mBuilder.build();
        mNotification.icon = icon;
        mManager.notify(dealWithId(channel), mNotification);

    }

    /** 执行PendingIntent事件*/
    public static void send(PendingIntent pendingIntent) {
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static PendingIntent getDefalutIntent(Context context, int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent(), flags);
        return pendingIntent;
    }


    public static int dealWithId(int channel) {
        return channel >= 1 && channel <= 100 ? channel : RANDOM.nextInt(Integer.MAX_VALUE - 100) + 101;
    }

    public static int getSystemVersion() {
        int version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    /**
     * 清理 所有
     *
     * @param context
     */
    public static void clearAllNotifification(Context context) {
        mManager = getManager(context);
        mManager.cancelAll();
    }

    /**
     * 清理
     *
     * @param context
     * @param channel
     */
    public static void clearNotifificationById(Context context, int channel) {
        mManager = getManager(context);
        mManager.cancel(dealWithId(channel));
    }


}

