package site.duqian.wchook.common;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by duqian on 2017/5/11.
 */

public class OkhttpUtils implements IHttpRequester {

    private static final OkHttpClient client = new OkHttpClient();

    private String runGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String runPost(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public String get(String url) throws Exception {
        return runGet(url);
    }

    @Override
    public String post(String url, String json) throws Exception{
        return runPost(url,json);
    }
}
