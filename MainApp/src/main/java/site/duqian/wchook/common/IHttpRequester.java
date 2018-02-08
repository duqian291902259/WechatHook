package site.duqian.wchook.common;
/**
 * Created by Dusan (duqian) on 2017/5/11 - 16:39.
 * E-mail: duqian2010@gmail.com
 * Description:网络请求的接口定义
 * remarks:
 */

public interface IHttpRequester {
    public String get(String url) throws Exception;
    public String post(String url,String json) throws Exception;
}
