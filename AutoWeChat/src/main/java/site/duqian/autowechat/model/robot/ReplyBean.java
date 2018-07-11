package site.duqian.autowechat.model.robot;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by duqian on 2017/3/8.
 */

public class ReplyBean implements Parcelable{
    private String talker;
    private String content;

    public ReplyBean(String talker, String content) {
        this.talker = talker;
        this.content = content;
    }

    public ReplyBean(Parcel source) {
        talker = source.readString();
        content =source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 序列化过程：必须按成员变量声明的顺序进行封装
        dest.writeString(talker);
        dest.writeString(content);
    }

    // 反序列过程：必须实现Parcelable.Creator接口，并且对象名必须为CREATOR
    // 读取Parcel里面数据时必须按照成员变量声明的顺序，Parcel数据来源上面writeToParcel方法，读出来的数据供逻辑层使用
    public static final Parcelable.Creator<ReplyBean> CREATOR = new Creator<ReplyBean>() {

        @Override
        public ReplyBean createFromParcel(Parcel source) {
            return new ReplyBean(source.readString(), source.readString());
        }

        @Override
        public ReplyBean[] newArray(int size) {
            return new ReplyBean[size];
        }
    };

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "{" +
                "talker='" + talker + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
