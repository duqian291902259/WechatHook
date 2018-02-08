package site.duqian.wchook.wechat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
/**
 * Created by Dusan (duqian) on 2017/5/8 - 16:30.
 * E-mail: duqian2010@gmail.com
 * Description:wechat 相关操作
 * remarks:
 */
public class WeChatHelper {

    private static final String TAG = WeChatHelper.class.getSimpleName();

    private Handler mHandler = null;
    private WeChatHelper() {
        mHandler = new Handler(Looper.getMainLooper());
    }
    private static final WeChatHelper instance = new WeChatHelper();
    public static WeChatHelper init() {
        return instance;
    }

    public static String getFromXml(String xmlmsg, String node) throws XmlPullParserException, IOException {
        String xl = xmlmsg.substring(xmlmsg.indexOf("<msg>"));
        //nativeurl
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser pz = factory.newPullParser();
        pz.setInput(new StringReader(xl));
        int v = pz.getEventType();
        String result = "";
        while (v != XmlPullParser.END_DOCUMENT) {
            if (v == XmlPullParser.START_TAG) {
                if (pz.getName().equals(node)) {
                    pz.nextToken();
                    result = pz.getText();
                    break;
                }
            }
            v = pz.next();
        }
        return result;
    }

    public static int getRandom(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    public static void openWechat(Context context) throws Exception {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(WechatUI.WECHAT_PACKAGE_NAME,WechatUI.UI_LUANCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);
    }

    /** 获取微信的版本*/
    public static int getWechatVersion(Context context) {
        PackageInfo mWechatPackageInfo = getPackageInfo(context);
        if(mWechatPackageInfo == null) {
            return 0;
        }
        return mWechatPackageInfo.versionCode;
    }

    /** 获取微信的版本名称*/
    public static String getWechatVersionName(Context context) {
        PackageInfo mWechatPackageInfo = getPackageInfo(context);
        if(mWechatPackageInfo == null) {
            return "";
        }
        return mWechatPackageInfo.versionName;
    }

    /** 更新微信包信息*/
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo mWechatPackageInfo = null;
        try {
            mWechatPackageInfo = context.getPackageManager().getPackageInfo(WechatUI.WECHAT_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mWechatPackageInfo;
    }

}
