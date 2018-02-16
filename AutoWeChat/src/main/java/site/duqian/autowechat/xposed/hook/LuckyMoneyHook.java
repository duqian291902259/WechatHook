package site.duqian.autowechat.xposed.hook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.wechat.WeChatHelper;
import site.duqian.autowechat.xposed.VersionParam;
import site.duqian.autowechat.xposed.utils.XSharedPrefUtil;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findFirstFieldByExactType;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static site.duqian.autowechat.xposed.VersionParam.hasTimingIdentifier;

/**
 * hongbao 功能
 */

public class LuckyMoneyHook extends BaseHook {

    private static final String TAG = LuckyMoneyHook.class.getSimpleName();
    private Context mContext;

    private static final String LUCKY_MONEY_RECEIVE_UI_CLASS_NAME = VersionParam.PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    private static final String RECEIVE_LUCKY_MONEY_REQUEST = VersionParam.PACKAGE_NAME + ".plugin.luckymoney.c.ae";

    private static Object requestCaller;
    private static int msgType;
    private static int channelId;
    private static String sendId;
    private static String nativeUrlString;
    private static String talker;
    private static boolean shouldPick;

    public LuckyMoneyHook(ClassLoader classLoader, Context context) {
        super(classLoader);
        mContext = context;
    }

    public void hookLuckyMoney() {
        //findAndHookMethod(VersionParam.conversationClass, classLoader, VersionParam.con_GetCursorMethod, Cursor.class, new XC_MethodHook() {
        findAndHookMethod(VersionParam.getMessageClass, classLoader, "b", Cursor.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                final boolean open = XSharedPrefUtil.isOpenLuckyMoney();
                if (!open) {
                    return;
                }
                int type = (int) getObjectField(param.thisObject, "field_type");
                if (type == 436207665 || type == 469762097) {
                    int status = (int) getObjectField(param.thisObject, "field_status");
                    if (status == 4) {
                        return;
                    }
                    talker = getObjectField(param.thisObject, "field_talker").toString();

                    String content = getObjectField(param.thisObject, "field_content").toString();

                    nativeUrlString = WeChatHelper.getFromXml(content, "nativeurl");
                    Uri nativeUrl = Uri.parse(nativeUrlString);
                    msgType = Integer.parseInt(nativeUrl.getQueryParameter("msgtype"));
                    channelId = Integer.parseInt(nativeUrl.getQueryParameter("channelid"));
                    sendId = nativeUrl.getQueryParameter("sendid");
                    //LogUtils.debug(TAG,"nativeUrlString:" + nativeUrlString);

                    requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, classLoader), VersionParam.getNetworkByModelMethod);
                    if (hasTimingIdentifier) {
                        LogUtils.debug(TAG,"hasTimingIdentifier:" + hasTimingIdentifier);
                        shouldPick = true;
                        callMethod(requestCaller, "a", newInstance(findClass(RECEIVE_LUCKY_MONEY_REQUEST, classLoader), channelId, sendId, nativeUrlString, 0, "v1.0"), 0);
                        return;
                    }
                    Object luckyMoneyRequest = newInstance(findClass("com.tencent.mm.plugin.luckymoney.c.ab", classLoader),
                            msgType, channelId, sendId, nativeUrlString, "", "", talker, "v1.0");

                    final int delayTime = XSharedPrefUtil.getDelayTime()/10;
                    //final int delayTime = XSharedPrefUtil.getLuckyMoneyDelayTime();
                    callMethod(requestCaller, "a", luckyMoneyRequest, delayTime);
                    LogUtils.debug(TAG,"requestCaller:" + requestCaller+"，luckyMoneyRequest:" + luckyMoneyRequest);
                }
            }
        });


        findAndHookMethod(RECEIVE_LUCKY_MONEY_REQUEST, classLoader, "a", int.class, String.class, JSONObject.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws JSONException {
                        if (!shouldPick) {
                            return;
                        }
                        String timingIdentifier = ((JSONObject) (param.args[2])).getString("timingIdentifier");
                        if (TextUtils.isEmpty(timingIdentifier)) {
                            return;
                        }
                        Object luckyMoneyRequest = newInstance(findClass("com.tencent.mm.plugin.luckymoney.c.ab", classLoader),
                                msgType, channelId, sendId, nativeUrlString, "", "", talker, "v1.0", timingIdentifier);
                        callMethod(requestCaller, "a", luckyMoneyRequest, XSharedPrefUtil.getDelayTime());
                        shouldPick = false;
                        LogUtils.debug(TAG,"timingIdentifier2:" + timingIdentifier);
                        LogUtils.debug(TAG,"requestCaller2:" + requestCaller);
                        LogUtils.debug(TAG,"luckyMoneyRequest2:" + luckyMoneyRequest);
                    }
                }
        );

        findAndHookMethod(LUCKY_MONEY_RECEIVE_UI_CLASS_NAME, classLoader, VersionParam.receiveUIFunctionName, int.class, int.class, String.class, VersionParam.receiveUIParamName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (XSharedPrefUtil.quickOpen()) {
                    Button button = (Button) findFirstFieldByExactType(param.thisObject.getClass(), Button.class).get(param.thisObject);
                    if (button.isShown() && button.isClickable()) {
                        button.performClick();
                    }
                    LogUtils.debug(TAG,"afterHookedMethod:" +button.toString());
                }
            }
        });

    }


}