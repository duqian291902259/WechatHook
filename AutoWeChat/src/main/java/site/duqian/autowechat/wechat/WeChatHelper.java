package site.duqian.autowechat.wechat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.UIUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

/**
 * wechat 相关操作
 * Created by duqian on 2017/1/13.
 */

public class WeChatHelper {

    private static final String TAG = WeChatHelper.class.getSimpleName();

    private Handler mHandler = null;
    private WeChatHelper() {
        mHandler = UIUtils.getHandler();
        //mHandler = new Handler(Looper.getMainLooper());
    }
    private static final WeChatHelper instance = new WeChatHelper();
    public static WeChatHelper init() {
        return instance;
    }


    public static boolean isChatRoom(String talker) {
        return talker.endsWith("@chatroom");
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

    /*public static int getRandom(int baseNum,int range){
        Random random=new Random();
        return baseNum + random.nextInt(range);
    }*/

    public static int getRandomDefault(){
        Random random=new Random();
        return 100 + random.nextInt(500);
    }

    /**
     * open wechat client
     * @param context
     */
    public static void openWechat(Context context) {
        try {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName(WechatUI.WECHAT_PACKAGE_NAME,WechatUI.UI_LUANCHER);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        }catch (Exception e){
            Log.d("main error ",e.toString());
        }

    }

    public static String initWechat(Context context) {
        final String wechatVersionName = getWechatVersionName(context);
        if (!TextUtils.isEmpty(wechatVersionName)) {
            SPUtils.putString(context, Constant.SP_WECHAT_VERSION_NAME, wechatVersionName);
        }
        //LogUtils.debug(TAG,"wechatVersionName="+wechatVersionName);
        return wechatVersionName;
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
            mWechatPackageInfo = context.getPackageManager().getPackageInfo(Constant.WECHAT_PACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mWechatPackageInfo;
    }

}
