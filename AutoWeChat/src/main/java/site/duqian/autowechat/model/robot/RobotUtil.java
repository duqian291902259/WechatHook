package site.duqian.autowechat.model.robot;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.easy.wtool.sdk.WToolSDK;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.JsonUtil;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.SystemUtil;
import site.duqian.autowechat.utils.net.MyOKHttpUtils;
import site.duqian.autowechat.wechat.WechatHookUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图灵机器人
 * Created by duqian on 2017/2/9.
 */

public class RobotUtil {
    private static final String TAG = RobotUtil.class.getSimpleName();

    private Context context;
    private  static  final String base_api = "http://www.tuling123.com/";
    private  static  final String turing_api = base_api+"openapi/api";
    private  static  final String turing_app_key = "b20ed26f953f465d85117820d48c2c9e";
    private  static  final String turing_secret = "56a2ae6afc25c108";

    private RobotUtil() {
    }

    public RobotUtil(Context context) {
        this.context = context;
    }

    private static volatile RobotUtil robotUtil;

    public static RobotUtil init() {
        if (robotUtil==null) {
            synchronized (RobotUtil.class){
                if (robotUtil==null){
                    robotUtil = new RobotUtil();
                }
            }
        }
        return robotUtil;
    }


    //子线程请求网络后回调方法
    public static void handleReply(Context context, ReplyBean reply) {
        String talker = reply.getTalker();
        String content = reply.getContent();
        RobotUtil.askTuringRobot(content, new RobotUtil.OnRobotListener() {
            @Override
            public void onSuccess(String repley_content) {
                if (!TextUtils.isEmpty(repley_content)) {
                    final WToolSDK wToolSDK = WechatHookUtil.getToolSDK();
                    if (talker.contains("@chatroom")){
                        wToolSDK.sendText(talker, repley_content);
                        //wToolSDK.transferMessage(content, 1,talker);
                    }else {
                        wToolSDK.sendText(talker, repley_content);
                    }
                    SPUtils.putString(context, Constant.SP_LAST_REPLY,repley_content);

                    LogUtils.debug(TAG,content+" << response2 >> : "+talker+",reply=" + repley_content);
                    Toast.makeText(context, "response2" + talker + ":" + repley_content,Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailed(Exception e) {
                LogUtils.debug(TAG, "handleReply error " + e.toString());
            }
        });
    }


    //辅助功能里面使用的请求网络
    public void requestTuringRobot(String info) {
        try {
            //封装请求参数
            JSONObject json = new JSONObject();
            json.put("key", turing_app_key);
            json.put("info", info);
            MyOKHttpUtils.getInstance().postJson(turing_api, json.toString(), new MyOKHttpUtils.MyCallback() {
                @Override
                public void onResponse(String result) {
                    LogUtils.debug(TAG, "post2Turing result =" + result);
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            final TextBean textBean = JsonUtil.json2Bean(result, TextBean.class);
                            final String content = textBean.text;
                            SystemUtil.copyText(context, content.replace("图灵机器人", ""));
                        }
                    } catch (Exception e) {
                        LogUtils.debug(TAG, "response json error " + e.toString());
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    LogUtils.debug(TAG, "post2Turing error  " + e.toString());
                }
            });
        } catch (Exception e) {
            LogUtils.debug(TAG, "json error " + e.toString());
        }
    }

    public interface OnRobotListener{
        abstract void onSuccess(String repley_content);
        abstract void onFailed(Exception e);
    }

    public static void askTuringRobot(String info,OnRobotListener onRobotListener) {
        try {
            //封装请求参数
            JSONObject json = new JSONObject();
            json.put("key", turing_app_key);
            json.put("info", info);
            MyOKHttpUtils.getInstance().postJson(turing_api, json.toString(), new MyOKHttpUtils.MyCallback() {
                @Override
                public void onResponse(String result) {
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            final TextBean textBean = JsonUtil.json2Bean(result, TextBean.class);
                            final String repley_content = textBean.text;
                            onRobotListener.onSuccess(repley_content);
                        }
                    } catch (Exception e) {
                        onRobotListener.onFailed(e);
                    }
                    //你好
                    //result ={"code":100000,"text":"你好呀，~有什么新鲜事儿要对我讲？"}
                }
                @Override
                public void onFailure(Exception e) {
                    onRobotListener.onFailed(e);
                }
            });
        } catch (Exception e) {
            onRobotListener.onFailed(e);
        }
    }

    //子线程访问图灵机器人
    public static String askTuringRobot(String param) {
        String url = turing_api;
        //封装请求参数
        JSONObject json = new JSONObject();
        String content = "";
        try {
            json.put("key",turing_app_key);
            json.put("info", param);
            final String result = post(json.toString(), url);
            if (!TextUtils.isEmpty(result)) {
                final TextBean textBean = JsonUtil.json2Bean(result, TextBean.class);
                content = textBean.text;
            }

            LogUtils.debug(TAG,"askTuringRobot result="+result);
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.debug(TAG,"askTuringRobot error "+e);
        }
        return content==null? "...":content;
    }

    /**
     * 向后台发送post请求
     *
     * @param param
     * @return
     */
    public static String post(String param, String url) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl
                    .openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "token");
            conn.setRequestProperty("tag", "htc_new");
            conn.connect();

            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(param);

            out.flush();
            out.close();
            //read
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.debug(TAG,"post error="+e.toString());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                LogUtils.debug(TAG,"post error2="+ex.toString());
            }
        }
        return result;
    }

}
