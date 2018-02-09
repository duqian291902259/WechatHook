package site.duqian.wchook.common;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.util.List;

import site.duqian.wchook.model.Constant;
import site.duqian.wchook.model.GoogleAddress;
import site.duqian.wchook.model.MyCallBack;
import site.duqian.wchook.utils.JsonUtil;
import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.ThreadManager;
import site.duqian.wchook.xposed.CommonHook;

/**
 * Created by duqian on 2017/5/11.
 */

public class ApiUtil {

    private static final String TAG = ApiUtil.class.getSimpleName();

    private static final String base_api = "http://www.tuling123.com/";
    private static final String turing_api = base_api + "openapi/api";
    private static final String turing_app_key = "b20ed26f953f465d85117820d48c2c9e";

    private static volatile ApiUtil apiUtil;
    private final IHttpRequester requester;

    public static ApiUtil init() {
        if (apiUtil == null) {
            synchronized (CommonHook.class) {
                if (apiUtil == null) {
                    apiUtil = new ApiUtil();
                }
            }
        }
        return apiUtil;
    }

    public ApiUtil() {
        requester = new OkhttpUtils();
        //requester = new HttpUtils();
    }

    public String askTuringRobot(String field_content) {
        String result = "";
        try {
            //封装请求参数
            JSONObject json = new JSONObject();
            json.put("key", turing_app_key);
            json.put("info", field_content);
            result = requester.post(turing_api, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.debug(TAG, "error " + e);
        }
        return result;
    }

    public void askRobot(String field_content, MyCallBack myCallBack) {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String reply = askRobot(field_content);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallBack.onSuccess(reply);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    myCallBack.onFailure(e);
                }
            }
        });
    }


    /**
     * 任务
     */
    private class NonoTask implements Runnable {
        private MyCallBack myCallBack;
        private int taskType;

        public NonoTask(int taskType, MyCallBack myCallBack) {
            this.myCallBack = myCallBack;
        }

        @Override
        public void run() {
            String result = "";
        }
    }

    public String askRobot(String field_content) {
        //String url = "http://52.77.95.9:8299/get_response?user_input=" + field_content + "&nsukey=YNsGMm6t4t720UGrr%2FsyJTA9fBnm9yg2GOw00NdirnCUnSl1r3Pn7AKtFkSbCqXwl%2FuIB5UArRfUCEeUYchQiYbmerLDnejdHVft25dUeLY8PXuyRfxz2QSFJyBqfmkbHZeEecalGC%2FxEw84o6nfQiK15t%2BTwT6yLiqlEV25lRrxb2EnJkMETrcTv9YfsmkY";
        String url ="http://baike.baidu.com/api/openapi/BaikeLemmaCardApi?scope=103&format=json&appid=379020&bk_key=%E9%93%B6%E9%AD%82&bk_length=600";
        String reply = "";
        try {
            String result = requester.get(url);
            JSONObject object = new JSONObject(result);
            reply = object.getString("response");//reply = object.optString("response");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.debug(TAG, "askRobot error=" + e);
        }
        return reply;
    }

    public String getGooglePostion(com.google.android.gms.maps.model.LatLng latLng) throws Exception {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=" + Constant.GOOGLEMAP_APP_KEY;
        String formatted_address = "";
        String json = requester.get(url);
        //LogUtils.debug(TAG,"json="+json+"，url="+url);
        GoogleAddress googleAddress = JsonUtil.json2Bean(json, GoogleAddress.class);
        List<GoogleAddress.ResultsBean> results = googleAddress.getResults();
        formatted_address = results.get(0).getFormatted_address();
        return formatted_address;
    }

    public void getGooglePostion(com.google.android.gms.maps.model.LatLng latLng, MyCallBack myCallBack) {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String googlePostion = getGooglePostion(latLng);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myCallBack.onSuccess(googlePostion);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    myCallBack.onFailure(e);
                }
            }
        });
    }

}
