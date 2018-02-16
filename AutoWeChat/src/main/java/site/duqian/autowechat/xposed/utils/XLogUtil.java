package site.duqian.autowechat.xposed.utils;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by DHB on 2017/2/8.
 */

public class XLogUtil {
    private enum Mode {
        RELEASE, DEBUG
    }

    private static Mode mMode = Mode.DEBUG;

    public static void setMode(Mode mode) {
        mMode = mode;
    }

    private static void log(String tag, String log) {
        XposedBridge.log("[" + tag + "]" + log);
    }

    public static void e(String tag, String log) {
        log(tag, log);
    }

    public static void d(String tag, String log) {
        if (mMode.equals(Mode.DEBUG)) {
            log("xposed-dq"+tag, log);
        }
    }
}
