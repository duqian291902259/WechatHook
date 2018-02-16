package site.duqian.autowechat.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import site.duqian.autowechat.android.service.ServiceUtil;
import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.model.robot.ReplyBean;
import site.duqian.autowechat.utils.LogUtils;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final String ACTION = "site.duqian.autowechat.AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(ACTION.equals(action)){
            ReplyBean replyBean = intent.getParcelableExtra("reply");
            if (replyBean==null){
                LogUtils.debug(TAG,"reply==null =");
                return;
            }
            //10秒没有完成，将可能ANR异常 应该启动一个service来处理
            int delayTime = Config.getDelayTime();
            LogUtils.debug(TAG,"AlarmReceiver  startRobotService = "+delayTime+replyBean);
            ServiceUtil.startRobotService(context,replyBean,delayTime);
        }else{
            LogUtils.debug(TAG," not alarm "+action);
        }
    }

}
