package site.duqian.autowechat.android.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import site.duqian.autowechat.utils.LogUtils;

/**
 * Created by duqian on 2017/1/13.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private static final String TAG = NotificationService.class.getSimpleName();

    private static NotificationService service;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.debug(TAG, "onCreate");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            onListenerConnected();
        }
    }


    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        LogUtils.debug(TAG, "onNotificationPosted");
       /* WechatService.handeNotificationPosted(new IStatusBarNotification() {
            @Override
            public String getPackageName() {
                return sbn.getPackageName();
            }

            @Override
            public Notification getNotification() {
                return sbn.getNotification();
            }
        });*/
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onNotificationRemoved(sbn);
        }
        LogUtils.debug(TAG, "onNotificationRemoved");
    }

    @Override
    public void onListenerConnected() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onListenerConnected();
        }

        LogUtils.debug(TAG, "onListenerConnected");
        service = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.debug(TAG, "onDestroy");
        service = null;
    }


    public static boolean isRunning() {
        if(service == null) {
            return false;
        }
        return true;
    }

    public static boolean isNotificationServiceRunning() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false;
        }
        //有可能没有NotificationService服务
        try {
            return isRunning();
        } catch (Throwable t) {}
        return false;
    }

}

