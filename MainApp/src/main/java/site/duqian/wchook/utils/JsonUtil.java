package site.duqian.wchook.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by duqian on 2017/5/12.
 */

public class JsonUtil {

    private static Gson gson;

    public static <T> T json2Bean(String json, Class<T> tClass) {
        if (gson==null) {
            gson = new Gson();
        }
        return gson.fromJson(json, tClass);
    }
    /**
     * JSONObject解析json,实例
     */
    public static int getResult(String json) {
        int result = -1;
        if (json!=null&&!"".equals(json)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                result = (Integer) jsonObject.getInt("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
