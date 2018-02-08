package site.duqian.wchook.model;

/**
 * Created by duqian on 2017/5/12.
 */

public class Config {
    private String latitude;
    private String longitude;
    private String address;
    private String replyContent;
    private boolean isAutoReply;
    private boolean is_reply_by_xp;
    private boolean is_reply_by_as;

    public Config(String latitude, String longitude, String address, String replyContent, boolean isAutoReply, boolean is_reply_by_xp, boolean is_reply_by_as) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.replyContent = replyContent;
        this.isAutoReply = isAutoReply;
        this.is_reply_by_xp = is_reply_by_xp;
        this.is_reply_by_as = is_reply_by_as;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public boolean isAutoReply() {
        return isAutoReply;
    }

    public void setAutoReply(boolean autoReply) {
        isAutoReply = autoReply;
    }

    public boolean is_reply_by_xp() {
        return is_reply_by_xp;
    }

    public void setIs_reply_by_xp(boolean is_reply_by_xp) {
        this.is_reply_by_xp = is_reply_by_xp;
    }

    public boolean is_reply_by_as() {
        return is_reply_by_as;
    }

    public void setIs_reply_by_as(boolean is_reply_by_as) {
        this.is_reply_by_as = is_reply_by_as;
    }
}
