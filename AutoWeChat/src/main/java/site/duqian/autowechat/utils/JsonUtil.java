package site.duqian.autowechat.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json 2 Bean
 * @author duqian
 */
public class JsonUtil {
    private static final String TAG = JsonUtil.class.getSimpleName();

    public static <T> T json2Bean(String json, Class<T> clazz){
        Gson gson = new Gson();
        return gson.fromJson(json,clazz);
	}

	/**
	 * 解析json:UpdateInfo
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
