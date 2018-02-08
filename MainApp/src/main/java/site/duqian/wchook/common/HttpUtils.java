package site.duqian.wchook.common;

import android.text.TextUtils;

import site.duqian.wchook.utils.LogUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by duqian on 2017/5/11.
 */

public class HttpUtils implements IHttpRequester {

    private static final String TAG = HttpUtils.class.getSimpleName();


    @Override
    public String get(String url) throws Exception {
        return request("GET", url, "");
        //return doGet(url);
    }

    @Override
    public String post(String url, String json) throws Exception {
        return request("POST", url, json);
    }

    /**
     * 向后台发送请求
     *
     * @param requestMethod
     * @param url
     * @param json
     * @return
     */
    public static String request(String requestMethod, String url, String json) throws Exception {
        OutputStreamWriter out = null;
        InputStream inputStream = null;
        BufferedReader in = null;
        String result = "";
        boolean isPost = false;
        if ("GET".equals(requestMethod) || TextUtils.isEmpty(json)) {
            isPost = false;
        } else {
            isPost = true;
        }
        URL realUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
        conn.setDoInput(true);
        if (isPost) {
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
        }
        conn.setUseCaches(false);
        conn.setRequestMethod(requestMethod);
        conn.setConnectTimeout(50000);
        conn.setReadTimeout(50000);

        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Connection", "close");
        //conn.setRequestProperty("Authorization", "token");
        //conn.connect();
        if (isPost) {
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(json);
            out.flush();
            out.close();
        }
        int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
        if (200 == responseCode) {
            inputStream = conn.getInputStream();
            result = getStringFromInputStream(inputStream);
            //result = readFromStream(inputStream);
        } else {
            LogUtils.debug(TAG, "访问失败" + responseCode);
        }
        return result;
    }

    private static String readFromStream(InputStream inputStream) throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line = "";
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 根据流返回一个字符串信息         *
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        // 如果while((is.read(buffer))!=-1)则无法将数据写入buffer中
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        String state =os.toString(); //new String(os.toByteArray(), "UTF-8");// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        is.close();
        return state;
    }

    public static String doGet(String url) throws Exception{
        HttpURLConnection conn = null;
        String result = "";
        // 利用string url构建URL对象
        URL mURL = new URL(url);
        conn = (HttpURLConnection) mURL.openConnection();

        conn.setRequestMethod("GET");
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(10000);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            InputStream is = conn.getInputStream();
            result = getStringFromInputStream(is);
        } else {
            LogUtils.debug(TAG, "访问失败" + responseCode);
        }
        return result;
    }

}
