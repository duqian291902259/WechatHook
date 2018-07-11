package site.duqian.autowechat.android.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.model.robot.ReplyBean;
import site.duqian.autowechat.utils.LogUtils;

/**
 * Created by duqian on 2017/3/8.
 */

public class BroadcastUtil {

    private static final String TAG = BroadcastUtil.class.getSimpleName();


    public static void sendReplyBroadcast(Context context, ReplyBean replyBean, int delayMillis) {
        //发送延时广播
        Intent intent = new Intent(AlarmReceiver.ACTION);
        intent.putExtra("reply",replyBean);
        //context.sendBroadcast(intent);
        LogUtils.debug(TAG,"sendReplyBroadcast delayMillis= "+delayMillis);

        //设定一个XX秒后的时间
        PendingIntent sender= PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarm=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, sender);
    }

    public static void sendReplyBroadcast(Context context, ReplyBean replyBean) {
        //发送延时广播
        Intent intent = new Intent(AlarmReceiver.ACTION);
        intent.putExtra("reply",replyBean);
        context.sendBroadcast(intent);
    }

    //向wechat里面发送广播
    public static void sendXposedBroadcast(Context context, ReplyBean replyBean) {
        //Intent intent =new Intent(context, XposedReceiver.class);
        // intent.setAction(XposedReceiver.ACTION);
        try {
            Intent intent = new Intent(XposedReceiver.ACTION);
            intent.putExtra("reply",replyBean);
            context.sendBroadcast(intent);
        } catch (Throwable e) {
            LogUtils.debug(TAG, "sendXposedBroadcast error " + e.toString());
        }
    }

    public static void sendMessage2Receiver(Context context, ReplyBean replyBean) {
        try {
            Intent intent = new Intent(Constant.ACTION_MEASSAG_RECEIVER);
            intent.putExtra("reply",replyBean);
            context.sendBroadcast(intent);
        } catch (Throwable e) {
            LogUtils.debug(TAG, "send2RobotReceiver error " + e.toString());
        }
    }

}
