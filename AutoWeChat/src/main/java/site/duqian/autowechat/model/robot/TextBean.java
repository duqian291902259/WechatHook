package site.duqian.autowechat.model.robot;

/**
 * Created by duqian on 2017/2/9.
 */

public class TextBean {
    //{"key": "APIKEY","info": "今天天气怎么样"}

    //{"code":100000,"text":"赞哇"}
    public String code ;
    public String text ;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TextBean{" +
                "code='" + code + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
