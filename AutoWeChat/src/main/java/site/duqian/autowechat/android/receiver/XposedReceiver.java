package site.duqian.autowechat.android.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import site.duqian.autowechat.android.service.RobotService;
import site.duqian.autowechat.android.service.ServiceUtil;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.model.robot.ReplyBean;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.SystemUtil;
import site.duqian.autowechat.xposed.utils.XSharedPrefUtil;

/**
 * Created by duqian on 2017/3/8.
 */

public class XposedReceiver extends BroadcastReceiver {
    private static final String TAG = XposedReceiver.class.getSimpleName();
    public static final String ACTION = "site.duqian.autowechat.xposed.receiver";

    private Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!ACTION.equals(action)){
            return;
        }

        boolean processRunning = SystemUtil.isProcessRunning(context, Constant.processName);
        //boolean processRunning = SystemUtil.isProcessRunning(context, Constant.packageName);
        if (!processRunning){
            ServiceUtil.startRobotService(context);
            return;
        }
       
        ReplyBean replyBean = intent.getParcelableExtra("reply");
        if (replyBean!=null){
            String content = replyBean.getContent();
            if (content.contains("[微信红包]")){
                Toast.makeText(context, "recevied! 红包来了", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "recevied：" + replyBean.getTalker() + "："
                        + content, Toast.LENGTH_SHORT).show();
            }
            //广播任务10秒没有完成，将可能ANR异常 应该启动一个service来处理
            action2clinet(context,  replyBean);
        }
        
    }

    private void action2clinet(Context context,ReplyBean replyBean) {
        String robotServiceName = "site.duqian.autowechat.android.service.RobotService";
        boolean serviceRunning = SystemUtil.isServiceRunning(context, robotServiceName);
        if (serviceRunning){
            //BroadcastUtil.sendReplyBroadcast(context,replyBean);
            //将接受到的信息通过广播发送到app，界面显示,如果app没有打开，会有bug
            //BroadcastUtil.sendMessage2Receiver(context,replyBean);
        }else{
            LogUtils.debug(TAG, "recived startRobotService "+replyBean);
            startRobotService(context,replyBean);
        }
    }

    private void startRobotService(Context context, ReplyBean replyBean) {
        try {
            Intent intent_service =new Intent(RobotService.ACTION);//context, RobotService.class
            intent_service.setPackage("site.duqian.autowechat");//app的包名
            if (replyBean!=null){
                intent_service.putExtra("reply",replyBean);
            }
            context.startService(intent_service);//启动app中的服务
        }catch (Exception e){
            openAPP(context);
            LogUtils.debug(TAG,"recived startService error.."+e);
        }
    }

    private void openAPP(Context context) {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(Constant.packageName,Constant.UI_LUANCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);
        LogUtils.debug(TAG,"recived openAPP  ....");
    }

    private void sendMessage(final Context context, final Intent intent) {
        if (handler==null){
            handler=new Handler(Looper.getMainLooper());
        }

        int delayTime = XSharedPrefUtil.getDelayTime();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },delayTime);
    }

}
