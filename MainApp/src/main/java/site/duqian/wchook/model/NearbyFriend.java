package site.duqian.wchook.model;

/**
 * Created by duqian on 2017/5/12.
 */

public class NearbyFriend {
    private String username ;//用户名
    private String userInfo;//附加信息
    private boolean isAdded ;//是否成为好友， 0（false）和 1（true）
    private long addTime;

    public NearbyFriend(String username, String userInfo, boolean isAdded) {
        this.username = username;
        this.userInfo = userInfo;
        this.isAdded = isAdded;
    }

    public NearbyFriend(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }


    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "NearbyFriend{" +
                "username='" + username + '\'' +
                ", userInfo='" + userInfo + '\'' +
                ", isAdded=" + isAdded +
                ", addTime=" + addTime +
                '}';
    }
}
