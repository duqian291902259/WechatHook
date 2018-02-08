package site.duqian.wchook.model;

/**
 * Created by duqian on 2017/5/11.
 */

public class ResponseBean {
    public String response;

    public ResponseBean(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "response='" + response + '\'' +
                '}';
    }
}
