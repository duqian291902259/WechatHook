package site.duqian.wchook.xposed;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import site.duqian.wchook.base.BaseHook;
import site.duqian.wchook.utils.LogUtils;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static site.duqian.wchook.accessibility.NearbyAs.username;

/**
 * Description:附近的人hook
 * Created by duqian on 2017/5/15 - 19:09.
 * E-mail: duqian2010@gmail.com
 */
public class NearbyHook extends BaseHook {
    private static final String TAG = NearbyHook.class.getSimpleName();
    private static Handler handler;
    private static Object requestCaller;

    public NearbyHook(ClassLoader classLoader, Context context) {
        super(classLoader, context);
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        LogUtils.debug(TAG,"NearbyHook init");
    }

    //发消息打招呼

    /**
     * List linkedList = new LinkedList();//Contact_User
     * linkedList.add(stringExtra);
     * List linkedList2 = new LinkedList();//Contact_Scene  18
     * linkedList2.add(Integer.valueOf(intExtra));
     * final k mVar = new m(2, linkedList, linkedList2, SayHiEditUI.a(this.oqT), SQLiteDatabase.KeyEmpty);
     * ak.vw().a(mVar, 0);
     *
     * @param username
     * @param greetingMessage
     * @param delayTime
     */
    public void hookSendGreeting(String username, String greetingMessage, int delayTime) {
        LogUtils.debug(TAG,"hookSendGreeting ");
        List linkedList = new LinkedList();
        linkedList.add(username);
        List linkedList2 = new LinkedList();
        linkedList2.add(18);
        if (requestCaller == null) {
            requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, classLoader), VersionParam.requestMethod);
        }
        Object sayHiModel = newInstance(findClass(VersionParam.SayHiModel, classLoader), 2, linkedList, linkedList2, greetingMessage, "");//type
        callMethod(requestCaller, VersionParam.con_NetworkMethod, sayHiModel, delayTime);
        LogUtils.debug(TAG, "sendGreeting :" + requestCaller + "，sayHiModel:" + sayHiModel);
    }

    public void hookSayHiModel() {
        LogUtils.debug(TAG,"hookSayHiModel .");
        findAndHookConstructor(VersionParam.SayHiModel, classLoader, int.class, List.class, List.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                //super.beforeHookedMethod(param);
                LogUtils.debug(TAG, "hookSayHiModel args1 = " + param.args[1]);
                /*
                for (int i = 0; i < param.args.length; i++) {
                    LogUtils.debug(TAG, "hookSayHiModel args " +i+"="+ param.args[i]);
                }*/
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                LogUtils.debug(TAG,"hookSayHiModel after.");
            }
        });
    }

    private Object friendList;

    // public final void a(int i, int i2, String str, k kVar) {
    // private List<afk> fBO = new LinkedList();
    public void hookNearbyNetCallBack() {
        LogUtils.debug(TAG, "hookNearbyNetCallBack ...");

        //Class<?> kClass = findClass(VersionParam.BaseRequestBean, classLoader);
        //findAndHookMethod(VersionParam.NearbyFriendsUI, classLoader, VersionParam.NEARBY_CALLBACK_METHOD, int.class, int.class, String.class,
        findAndHookMethod(VersionParam.SayHiEditUI, classLoader, VersionParam.NEARBY_CALLBACK_METHOD, int.class, int.class, String.class,
                findClass(VersionParam.BaseRequestBean, classLoader), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        //super.beforeHookedMethod(param);
                        for (int i = 0; i < param.args.length; i++) {
                            LogUtils.debug(TAG, "hookNearbyNetCallBack args : " + param.args[i]);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //super.afterHookedMethod(param);
                        Object currentObject = param.thisObject;
                        for (Field field : currentObject.getClass().getDeclaredFields()) { //遍历类成员
                            field.setAccessible(true);
                            Object value = field.get(currentObject);
                            if (field.getName().equals("fBO")) {
                                friendList = value;
                                handleData(friendList);
                                LogUtils.debug(TAG, "hookNearbyNetCallBack Field fBO found." + value);
                            }
                        }

                        LogUtils.debug(TAG, "hookNearbyNetCallBack Field after" );

                    }
                });
    }

    private void handleData(Object friendList) {
        if (friendList == null) {
            return;
        }
        Class<?> nearbyEntity = findClass(VersionParam.NEARBY_PROTOCAL, classLoader);
        if (friendList instanceof List) {
            List list = (List) friendList;
            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                LogUtils.debug(TAG, "get bean " + obj);
            }
        } else {
            LogUtils.debug(TAG, "type is wrong");
        }
    }

    /*
    *  this.huc.hub = new a(this.huc, f2, f, (int) d2);
                com.tencent.mm.modelstat.e.JM().a(2001, i != 0, this.huc.bXd == null ? false : this.huc.bXd.cHJ, f, f2, (int) d2);
                this.huc.htN = new com.tencent.mm.plugin.nearby.a.d(this.huc.htR, this.huc.hub.cHA, this.huc.hub.cHz, this.huc.hub.accuracy, i, "", "");
                ak.vw().a(this.huc.htN, 0);
                ak.vw().a(new com.tencent.mm.plugin.nearby.a.c(this.huc.hub.cHA, this.huc.hub.cHz, this.huc.hub.accuracy, i, "", ""), 0);
                v.i("MicroMsg.NearbyFriend", "do NetSceneLBSLifeGetNearbyRecommendPoi");

    d----v.d("MicroMsg.NetSceneLbsP", "Req: opcode:" + i + " lon:" + f + " lat:" + f2 + " pre:" + i2 + " gpsSource:" + i3 + " mac" + str + " cell:" + str2);
          int i, float f, float f2, int i2, int i3, String str, String str2
    c-----float f, float f2, int i, int i2, String str, String str2
    *
    * */

    public void hookGetFriendsByPosition(double longtitude, double latitude) {
        LogUtils.debug(TAG, "hookGetFriendsByPosition ...");
        List linkedList = new LinkedList();
        linkedList.add(username);
        List linkedList2 = new LinkedList();
        linkedList2.add(18);
        if (requestCaller == null) {
            requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, classLoader), VersionParam.requestMethod);
        }
        Object positionModel = newInstance(findClass(VersionParam.NEARBY_PROTOCAL_POSITION, classLoader), 1, longtitude, latitude, 0, 0, "", "");//发送位置
        callMethod(requestCaller, VersionParam.con_NetworkMethod, positionModel, 0);
        LogUtils.debug(TAG, "sendPosition :" + requestCaller + "，positionModel:" + positionModel);
    }
}
