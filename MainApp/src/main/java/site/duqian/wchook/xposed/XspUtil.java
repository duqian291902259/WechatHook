package site.duqian.wchook.xposed;

import site.duqian.wchook.model.Constant;

/**
 * Created by duqian on 2017/5/11.
 */

class XspUtil {

    public boolean isOpenRobotReply() {
        boolean isAutoReply = mSettings.getBoolean(Constant.SP_IS_AUTO_REPLY, true);
        boolean is_reply_by_xp = mSettings.getBoolean("is_reply_by_xp", true);
        return is_reply_by_xp;
    }

    public String getFilterKeywords() {
        return mSettings.getString("Keywords", "");
    }

    private static XspUtil xspUtil = null;
    private static SettingsHelper mSettings = null;

    public static XspUtil init(SettingsHelper settings) {
        if (xspUtil == null) {
            synchronized (SettingsHelper.class) {
                if (xspUtil == null) {
                    xspUtil = new XspUtil();
                }
            }
        }
        mSettings = settings;
        mSettings.reload();
        return xspUtil;
    }

}
