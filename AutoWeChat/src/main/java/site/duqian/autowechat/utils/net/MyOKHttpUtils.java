package site.duqian.autowechat.utils.net;

import site.duqian.autowechat.utils.UIUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * MyOkHttpUtils
 * Created by duqian on 16/3/9.
 */
public class MyOKHttpUtils {
    private static OkHttpClient okHttpClient ;
    private static MyOKHttpUtils myOkHttpUtils;
    public static MyOKHttpUtils getInstance() {
        if (myOkHttpUtils == null) {
            synchronized (MyOKHttpUtils.class) {
                if (myOkHttpUtils == null) {
                    myOkHttpUtils = new MyOKHttpUtils();
                }
            }
        }
        return myOkHttpUtils;
    }

    private int time = 5;
    public MyOKHttpUtils() {
        if (okHttpClient == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            okHttpClientBuilder.connectTimeout(time, TimeUnit.SECONDS);
            okHttpClientBuilder.readTimeout(time, TimeUnit.SECONDS);
            okHttpClientBuilder.writeTimeout(time, TimeUnit.SECONDS);
            okHttpClient = okHttpClientBuilder.build();
        }
    }

    //OKHTTP GET
    public String get(String url){
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       return "";
    }
    public void get(String url, Callback responseCallback){
        Request request = new Request.Builder().url(url).build();
        enqueue(request,responseCallback);
    }

    public void get(String url, MyCallback myCallback){
        Request request = new Request.Builder().url(url).build();
        enqueue(request,myCallback);
    }

    //使用Request的post方法来提交请求体RequestBody
    public String post(String url, RequestBody requestBody) throws IOException {
        //RequestBody requestBody = RequestBody.create(MediaType.parse("UTF-8"),"param=value");
       /*RequestBody requestBody = new FormBody.Builder()
                .add("device_mark", "869085024277783")
                .add("uid", "1")
                .build();*/
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
    //带回调的post请求
    public void  post(String url, RequestBody requestBody, MyCallback myCallback){
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        enqueue(request,myCallback);
    }
    public void  post(String url, RequestBody requestBody, Callback responseCallback){
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        enqueue(request,responseCallback);
    }


    //POST提交Json数据
    public  final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public void postJson(String url, String json, MyCallback myCallback) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        enqueue(request,myCallback);
        /*Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }*/
    }

    /**
     * 开启异步线程访问网络
     * @param request
     * @param responseCallback
     */
    public  void enqueue(Request request, Callback responseCallback){
        okHttpClient.newCall(request).enqueue(responseCallback);
    }
    public interface MyCallback {
        public void onResponse(String result);
        public void onFailure(Exception e) ;
    }
    public void enqueue(Request request, final MyCallback myCallback){
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        myCallback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                final String result = response.body().string();
                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        myCallback.onResponse(result);
                    }
                });
            }
        });
    }


}
