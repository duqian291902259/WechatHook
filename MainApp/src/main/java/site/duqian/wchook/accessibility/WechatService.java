package site.duqian.wchook.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import site.duqian.wchook.database.FriendsDbUtils;
import site.duqian.wchook.model.Constant;
import site.duqian.wchook.model.NearbyFriend;
import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.NotificationUtils;
import site.duqian.wchook.utils.ToastUtil;
import site.duqian.wchook.utils.UIUtil;
import site.duqian.wchook.wechat.WechatUI;
import site.duqian.wchook.xposed.SettingsHelper;

import java.util.ArrayList;
import java.util.List;

import static site.duqian.wchook.accessibility.NearbyAs.enterNearby;
import static site.duqian.wchook.accessibility.NearbyAs.lastUsername;
import static site.duqian.wchook.accessibility.NearbyAs.username;

/**
 * Created by Dusan (duqian) on 2017/5/8 - 16:29.
 * E-mail: duqian2010@gmail.com
 * Description:模拟点击功能
 * remarks:
 */
public class WechatService extends AccessibilityService {

    private static String TAG = WechatService.class.getSimpleName();
    public static int ACTION = 0;
    public static int COUNT_TOTAL = 0;
    public static volatile int COUNT_PAGE = 0;
    private Context context;
    public static boolean pasted = false;
    private static WechatService service;
    public static int winWidth = 0;
    public static int winHeight = 0;
    public static int page_state = 0;
    public static SettingsHelper mSettings;
    public static long startTime = System.currentTimeMillis();

    public static FriendsDbUtils dbUtils;
    public static List<String> userList = new ArrayList<>();
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        service = this;
        context = getApplicationContext();
        mSettings = new SettingsHelper(context);
        int[] displaySize = UIUtil.getDisplaySize(context);
        winWidth = displaySize[0];
        winHeight = displaySize[1];
        dbUtils = FriendsDbUtils.init(context);
        //userList = dbUtils.getAllFriends();
        int size = userList.size();
        LogUtils.debug(TAG,"onServiceConnected userList.size()="+ size);
        if (size>1000){
            userList.clear();
            ToastUtil.toast(context,"数据库记录超过1000条，注意清理");
        }
        page_state=0;
    }


    public synchronized void setDefalut(){
        pasted = false;
        COUNT_PAGE +=1;
        COUNT_TOTAL+=1;
        page_state = 3;
        LogUtils.debug(TAG,COUNT_PAGE+",COUNT_TOTAL ="+COUNT_TOTAL);
        lastUsername = username;
       /*if (!userList.contains(lastUsername)) {
            userList.add(lastUsername);
        }*/
        if (!dbUtils.isAdded(lastUsername)){
            dbUtils.insert(new NearbyFriend(lastUsername));
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //mockNearByFriends(event);
        if (Constant.ACTION_SAY_HELLO ==ACTION) {
            handleEvent(event);
        }
    }

    private void handleEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        String className = event.getClassName().toString();

        //通知栏事件
        if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            handleNotificationEvent(event);//64
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if(WechatUI.UI_LUANCHER.equals(className)) {
                enterNearby(service);
                startTime = System.currentTimeMillis();
            }
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            mockNearByFriends(event);
            backHome(className);
        } else if(eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
             //CommonUtil.init().sendText(service);
        }else if (AccessibilityEvent.TYPE_VIEW_SCROLLED==eventType){//4096
            timeOutAndQuit();
        }
    }

    private void backHome(String className) {
        /*if (!WechatUI.UI_LUANCHER.equals(className)
        &&!WechatUI.UI_NEARBY_FRIENDS.equals(className)
                &&!WechatUI.UI_CONTACT_INFO.equals(className)
                &&!WechatUI.UI_SAYHI_EDIT.equals(className)){
            CommonUtil.init().backPrePage(service);
        }*/
    }

    private void timeOutAndQuit() {
        if (Constant.ACTION_SAY_HELLO != ACTION) {return;}
        long configTime = mSettings.getInt(Constant.SP_DURATION,1000)*1000;
        long duration = System.currentTimeMillis() - startTime;
        AccessibilityNodeInfo node_listview = AccessibilityHelper.findNodeInfosById(CommonUtil.init().checkRootWindow(service), WechatUI.ID_NEARBY_LIST_VIEW);
        if (duration>=configTime && node_listview!=null){//&& node_listview!=null
            CommonUtil.init().backPrePage(service);
            //startTime = System.currentTimeMillis();
        }
        //LogUtils.debug(TAG,configTime+" ,duration = "+duration);
    }

    private synchronized void  mockNearByFriends(AccessibilityEvent event) {
        if (Constant.ACTION_SAY_HELLO != ACTION) {return;}
        try {
            timeOutAndQuit();
            NearbyAs.init().startNearby(event, service, context);
        }catch (Exception e){
            LogUtils.debug(TAG,"mockNearByFriends error "+e.toString());
        }
    }

    private void handleNotificationEvent(AccessibilityEvent event) {
        Parcelable data = event.getParcelableData();
        if(data == null || !(data instanceof Notification)) {
            return;
        }
        List<CharSequence> texts = event.getText();
        if(!texts.isEmpty()) {
            String text = String.valueOf(texts.get(0));//包括微信名  :
            handleNotification(text, (Notification) data);
        }
    }

    /**
     * 判断当前服务是否正在运行
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if (service == null) {
            return false;
        }
        return AccessibilityHelper.isServiceRunning(service);
    }

    //处理通知栏消息
    private void handleNotification(String ticker, Notification notification) {
        String[] split = ticker.split(":");
        String username = split[0];
        if (ticker.contains(Constant.NOTIFACTION_ADDED_FRIEND)||ticker.contains(Constant.NOTIFACTION_ADDED_FRIEND_EN)){
            String message = "微信又有新的好友了！--" + username;
            LogUtils.debug(TAG, message);
            ToastUtil.toast(context,message);
        }
        //clickNotification(notification);
    }

    private void clickNotification(Notification notification) {
        PendingIntent pendingIntent  = notification.contentIntent;
        if (pendingIntent!=null) {
            NotificationUtils.send(pendingIntent);
        }
    }

    @Override
    public void onInterrupt() {
        LogUtils.debug(TAG,"onInterrupt");
    }

}
