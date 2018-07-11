package site.duqian.autowechat.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import site.duqian.autowechat.model.robot.ReplyBean;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by duqian on 2017/3/8.
 */

public class ServiceUtil {

    private static final String TAG = ServiceUtil.class.getSimpleName();

    //启动robot服务
    public static void startRobotService(Context context) {
        Intent intent =new Intent(RobotService.ACTION);
        //Intent intent =new Intent(context,RobotService.class);
        //intent.setAction(RobotService.ACTION);
        intent.setPackage("site.duqian.autowechat");//app的包名
        context.startService(intent);//启动app中的服务

        //LogUtils.debug(TAG,"  startRobotService ....");
    }

    public static void startRobotService(Context context, ReplyBean replyBean, int delayMillis) {
        Intent intent =new Intent(RobotService.ACTION);//context, RobotService.class
        intent.setPackage("site.duqian.autowechat");//app的包名
        intent.putExtra("reply", replyBean);
        //context.startService(intent);//启动app中的服务
        //设定一个XX秒后的时间
        PendingIntent sender= PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm=(AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, sender);
    }


    //开启服务：
    public static void startService(Context context, Class clazz) {
       /* Intent intent = new Intent();
        ComponentName component = new ComponentName("site.duqian.autowechat", "site.duqian.autowechat.android.service.RobotService");
        intent.setComponent(component);//设置一个组件名称*/

        Intent service = new Intent(context, clazz);
        service.setPackage(context.getPackageName());//5.0新要求
        context.startService(service);//多次调用onStartCommand
    }

    //关闭服务
    public static void StopService(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setPackage(context.getPackageName());//5.0新要求
        context.stopService(intent);
    }

}
