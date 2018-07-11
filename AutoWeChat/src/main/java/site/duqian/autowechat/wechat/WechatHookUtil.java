package site.duqian.autowechat.wechat;

import android.content.Context;
import android.text.TextUtils;

import com.easy.wtool.sdk.MessageEvent;
import com.easy.wtool.sdk.OnMessageListener;
import com.easy.wtool.sdk.WToolSDK;
import site.duqian.autowechat.android.service.ServiceUtil;
import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.model.robot.ReplyBean;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.ToastUtils;
import site.duqian.autowechat.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * duqian3201@163.com
 * 本工具类依赖于xposed第三方module：微控工具模块。兼容微信多个版本，但是需要授权才能使用更多功能，这里仅供测试
 * Created by duqian on 2017/2/20.
 */

public class WechatHookUtil {

    private static final String TAG = WechatHookUtil.class.getSimpleName();

    //初始化
    private static  WToolSDK wToolSDK ;//= new WToolSDK()
    private static int delayMillis;

    public static WToolSDK initWtoolSdk() {
        if (wToolSDK==null) {
            synchronized (WechatHookUtil.class){
                if (wToolSDK==null){
                    wToolSDK = new WToolSDK();
                }
            }
        }
        //init();
        return wToolSDK;
    }

    private static void init(WToolSDK wToolSDK) {
        //wToolSDK.getVersion();
        wToolSDK.encodeValue("1");
        String authcode = Config.STRING_AUTHCODE;
        if (!TextUtils.isEmpty(authcode)) {
            parseResult(false,wToolSDK.init(authcode));
        }
    }

    public static boolean isInitWToolSDK=false;
    public static boolean isListenerOK=false;

    public static boolean checkWToolSdk(){
        WToolSDK wToolSDK = getToolSDK();
        WechatHookUtil.autoRepley(wToolSDK);
        return isInitWToolSDK;
    }


    public static WToolSDK getToolSDK(){
        if (!WechatHookUtil.isInitWToolSDK){
            wToolSDK = WechatHookUtil.initWtoolSdk();
            init(wToolSDK);
        }
        return wToolSDK;
    }



    public static void parseResult(boolean isToast,String json) {
        String text = "";
        int result = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            result = jsonObject.getInt("result");
            if (result == 0) {
                text = "操作成功";
            } else {
                text = jsonObject.getString("errmsg");
            }
        } catch (Exception e) {
            text = "解析结果失败";
        }


        if (text.contains("失败")||result == 2000||text.contains("SDK")) {//连接微控xposed模块失败
            isInitWToolSDK = false;
            //开启微信
        }else{
            isInitWToolSDK = true;
        }
        LogUtils.debug(TAG,"parseResult "+text+",isInitWToolSDK="+isInitWToolSDK);

    }

    //解析结果
    public static void parseResult(String json) {
        parseResult(true,json);
    }

    public static boolean startMessageListener(WToolSDK wToolSDK){
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(1);
            jsonArray.put(2);
            jsonObject.put("talkertypes", jsonArray);
            jsonObject.put("froms", new JSONArray());
            jsonArray = new JSONArray();
            jsonArray.put(1);
            jsonArray.put(42);
            jsonObject.put("msgtypes", jsonArray);
            jsonObject.put("msgfilters", new JSONArray());
            String result = wToolSDK.startMessageListener(jsonObject.toString());
            jsonObject = new JSONObject(result);
            if (jsonObject.getInt("result") == 0) {
                String text = jsonObject.getString("errmsg");
                LogUtils.debug(TAG, "start listener..."+ text);
                ToastUtils.showToast(UIUtils.getContext(),"start listener...");
                return true;
            }
        } catch (Exception e) {
            LogUtils.debug(TAG, "err="+ e);
            return false;
        }
        return false;
    }

    public static void stopMessageListener(WToolSDK wToolSDK){
        wToolSDK.stopMessageListener();
    }

    /*
    * WechatHookUtil content: {
      "content" : "NQ==\n",
      "imgpath" : "",
      "msgid" : "173",
      "msgsvrid" : "3005161224655192956"
     }*/
    public static void autoRepley(WToolSDK wToolSDK){
        boolean started = startMessageListener(wToolSDK);
        if (!started){
            LogUtils.debug(TAG,"监听信息失败");
        }
        isListenerOK = started;

        wToolSDK.setOnMessageListener(new OnMessageListener() {
            @Override
            public void messageEvent(MessageEvent event) {
                final String talker = event.getTalker();
                String content = event.getContent();
                //LogUtils.debug(TAG,"content: " + content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    content = wToolSDK.decodeValue(jsonObject.getString("content"));
                } catch (Exception e) {
                    LogUtils.debug(TAG,"get content error " + e);
                }

                final String message = "From "+talker+" : " + content;
                Context context = UIUtils.getContext();

                ToastUtils.showToast(context,message);
                LogUtils.debug(TAG,message);

                boolean isXposedOpen = SPUtils.getBoolean(context, Constant.SP_XPOSED_OPENED, true);
                boolean isOpenAutoRepley = SPUtils.getBoolean(context,"KEY_ROBOT_REPLEY", false);
                if(!isOpenAutoRepley){//没有开启自动回复
                    LogUtils.debug(TAG,"isAutoRepley: false");
                    return;
                }
                if(!isXposedOpen){//没有开启xposed拦截功能
                    LogUtils.debug(TAG,"isXposedOpen:false ");
                    return;
                }
                //自动回复
                final String keywords = Config.getNoReplyKeywords();
                if (keywords.contains(content)){
                    LogUtils.debug(TAG,"包含过滤信息，不回复: " + message);
                    return;
                }

                delayMillis = Config.getDelayTime();
                ReplyBean replyBean = new ReplyBean(talker,content);
                LogUtils.debug(TAG,"startRobotService = "+delayMillis+"，"+replyBean.toString());

                ServiceUtil.startRobotService(context,replyBean,delayMillis);
                //BroadcastUtil.sendReplyBroadcast(context,replyBean, delayMillis+5000);
            }
        });
    }

}
