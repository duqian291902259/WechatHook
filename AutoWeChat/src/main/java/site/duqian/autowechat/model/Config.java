package site.duqian.autowechat.model;

import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.UIUtils;
import site.duqian.autowechat.wechat.WeChatHelper;

/**
 * Created by duqian on 2017/1/18.
 */

public class Config {
    public static String  getReplyContent() {
        return SPUtils.getString(UIUtils.getContext(), Constant.SP_REPLY_CONTENT, "快乐每一天！ge正忙...");
    }

    public static String  getKeywords() {
        return SPUtils.getString(UIUtils.getContext(), Constant.SP_MSG_KEYWORDS, "年，祝，新年");
    }

    public static int SEND_PIC_NUMBER = 3;

    //临时授权码:0279C8C340306804E57499CD112EB094CB13037A
    public static final String STRING_AUTHCODE = "0279C8C340306804E57499CD112EB094CB13037A";
    //设备识别码：2536E486CE067DE4C85EB8F667D272E5
    //public static final String STRING_AUTHCODE = "080ACC20E503EA2F045930ABDACEC7B11446070D";

    public static String  getNoReplyKeywords() {
        return SPUtils.getString(UIUtils.getContext(), "filter_keywords", "stop，停，4");
    }

    public static int getDelayMin() {
        return SPUtils.getInt(UIUtils.getContext(),"min_value", 5);
    }

    public static int getDelayMax() {
        return SPUtils.getInt(UIUtils.getContext(),"max_value", 20);
    }

    public static int getDelayTime() {
        return WeChatHelper.getRandom(getDelayMin(), getDelayMax())*1000;
    }
}
