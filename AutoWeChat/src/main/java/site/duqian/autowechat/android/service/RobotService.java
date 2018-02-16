package site.duqian.autowechat.android.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.model.robot.ReplyBean;
import site.duqian.autowechat.model.robot.RobotUtil;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.wechat.WechatHookUtil;

import static site.duqian.autowechat.wechat.WechatHookUtil.getToolSDK;
import static site.duqian.autowechat.wechat.WechatHookUtil.isInitWToolSDK;
import static site.duqian.autowechat.wechat.WechatHookUtil.isListenerOK;

/**
 * 自动聊天机器人服务，发送消息依赖于第三方module：微控工具模块
 * 启动方式：1，直接启动
 * 2，定时器延时启动
 * 基于xposed框架hook后，仅用于app保活
 */
public class RobotService extends Service {
    private static final String TAG = RobotService.class.getSimpleName();
    public static final String ACTION = "site.duqian.autowechat.RobotService";
    public static final String ACTION_SENT2_ROBOT_RECEIVER = "action_send2_robot_receiver";//发送广播到robotservice

    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtils.debug(TAG,"onCreate");
        context = getApplicationContext();
        //registerBroadcast();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ReplyBean reply = intent.getParcelableExtra("reply");
        if (reply!=null){
            LogUtils.debug(TAG,"onStartCommand ----" +reply.toString());
            //replyByWTools(reply);
        }else {
            LogUtils.debug(TAG, "onStartCommand ----" );
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void replyByWTools(ReplyBean reply) {
        getToolSDK();
        if (!isListenerOK){
            WechatHookUtil.checkWToolSdk();
        }
        SPUtils.putBoolean(context, Constant.SP_IS_WTOOL_OK, isInitWToolSDK);
        SPUtils.putBoolean(context, Constant.SP_Listener_OK, isListenerOK);

        if (isInitWToolSDK) {
            RobotUtil.handleReply(context, reply);
        }
    }


    @Override
    public void onDestroy() {
        LogUtils.debug(TAG,"onDestroy");
        try {
            if (receiver!=null)
            unregisterReceiver(receiver);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void registerBroadcast() {
        IntentFilter intentFilter=new IntentFilter(ACTION_SENT2_ROBOT_RECEIVER);
        try {
            registerReceiver(receiver,intentFilter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ACTION_SENT2_ROBOT_RECEIVER.equals(intent.getAction())){
                return;
            }
            ReplyBean replyBean = intent.getParcelableExtra("reply");
            int delayTime = Config.getDelayTime();
            ServiceUtil.startRobotService(context,replyBean,delayTime);
            //LogUtils.debug(TAG,"receiver  startRobotService = "+delayTime);
        }
    };
}
