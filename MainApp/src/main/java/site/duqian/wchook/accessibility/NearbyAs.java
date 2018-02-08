package site.duqian.wchook.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import site.duqian.wchook.model.Constant;
import site.duqian.wchook.utils.AdbUtil;
import site.duqian.wchook.utils.BroadcastUtil;
import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.SystemUtil;
import site.duqian.wchook.wechat.WechatUI;

import static site.duqian.wchook.accessibility.WechatService.userList;

/**
 * Created by Dusan (duqian) on 2017/5/8 - 20:32.
 * E-mail: duqian2010@gmail.com
 * Description:微信附近的人自动添加好友，模拟点击
 * remarks:
 */
public class NearbyAs {

    private final Handler mHandler;
    private static final String TAG = NearbyAs.class.getSimpleName();
    public static String username ="你好！";
    public static String lastUsername ="";
    public static String sendMessageContent ="Hello,I want to make friends with you!";
    private int countSend;
    private Rect rect = new Rect();
    private long clickedItemTime;
    private long clickedGreetingTime;
    private AccessibilityNodeInfo nodeInfo;
    private boolean needMove = false;
    private int userAvatarHeight =0;
    private int distance;//移动的最短距离

    private NearbyAs() {
        mHandler = new Handler(Looper.getMainLooper());
    }
    private static final NearbyAs instance = new NearbyAs();

    public static NearbyAs init() {
        return instance;
    }
    private Context context ;
    private long lastTime = 0;
    //模拟事件处理，确保同样的事件只进入一次，进入一个
    public synchronized void startNearby(AccessibilityEvent event, WechatService service, Context context) {
        if (this.context==null) {
            this.context = context;
        }
        int eventType = event.getEventType();
        String className = event.getClassName().toString();
        nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        ////判断什么时候向下滑动
        if (WechatUI.UI_NEARBY_FRIENDS.equals(className)
                ||WechatUI.CLASS_NAME_LIST_VIEW.equals(className)){
            if (handleMoveEvent()) return;
        }

        AccessibilityNodeInfo node_listview = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_NEARBY_LIST_VIEW);
        if (node_listview!=null||AccessibilityEvent.TYPE_VIEW_SCROLLED==eventType) {
            handle_nearby_friends(service);
            getThumbHeight();
        }

        AccessibilityNodeInfo node_username = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_USER_NAME);
        if (node_username!=null) {
            //if (isActionShort()) return;
            handle_contact_info(service);
        }

        AccessibilityNodeInfo node_editview = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_SEND_HELLO_EDIT_VIEW);
        if (node_editview!=null||AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED==eventType) {
            handle_sayhi_edit(service);
        }
    }

    private boolean isActionShort() {
        long acitonTime = System.currentTimeMillis() - lastTime;
        if (acitonTime<500){
            return true;
        }
        lastTime = System.currentTimeMillis();
        return false;
    }

    private boolean handleMoveEvent() {
        distance = userAvatarHeight==0 ? 160:userAvatarHeight+50;
        if (needMove){
            LogUtils.debug(TAG,"needMove handleMoveEvent");
            moveScreenDown(distance);
        }
        if ((rect!=null&&rect.bottom >=  WechatService.winHeight*4/5 )){//|| WechatService.winHeight*3/4 countSend>=8
            needMove = true;
            moveScreenDown(rect.bottom);
            return true;
        }else{
            needMove = false;
        }
        return false;
    }

    private void getThumbHeight() {
        if (userAvatarHeight==0) {
            AccessibilityNodeInfo node_thumb = AccessibilityHelper.findNodeInfosByIdAndPosition(nodeInfo, WechatUI.ID_NEARBY_USER_AVATAR, 4);
            if (node_thumb==null) {
                return ;
            }
            Rect rect = new Rect();
            node_thumb.getBoundsInScreen(rect);
            userAvatarHeight = rect.bottom - rect.top;
            LogUtils.debug(TAG,"node_thumb rect ="+rect+",userAvatarHeight="+ userAvatarHeight);
        }
    }

    //编辑打招呼用户
    private void handle_sayhi_edit(WechatService service) {
        //复制文本信息
        if (!TextUtils.isEmpty(username)){
            sendMessageContent = username +","+WechatService.mSettings.getString(Constant.SP_GREETING_CONTENT,"I want to make friends with you!");
        }
        if (!SystemUtil.hasLollipop()){
            BroadcastUtil.sendMessage2Receiver(context, sendMessageContent);
        }
        //粘贴文本信息
        CommonUtil.init().pasteText(service);
        //发送放到文本变化去处理,为了避免重复点击，在粘贴后进行
        //CommonUtil.init().sendText(service);
    }

    //详细资料
    private void handle_contact_info(WechatService service) {
        AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        //username 方便处理后退
        AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_USER_NAME);
        if (node != null) {
            username = node.getText().toString().trim();
            if (lastUsername.equals(username)){
                lastUsername = username;
                needMove = true;
                CommonUtil.init().backPrePage(service);
                return;
            }else {
                lastUsername = username;
            }
        }

        //打招呼
        AccessibilityNodeInfo node1 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SAY_HELLO);
        if (node1==null){
            node1 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SAY_HELLO_EN);
        }
        if (node1 != null) {
            if (System.currentTimeMillis()-clickedGreetingTime<100){
                LogUtils.debug(TAG,"clickedGreetingTime is short ");
                return;
            }
            AccessibilityHelper.performClick(node1);
            WechatService.page_state = 2;
            clickedGreetingTime = System.currentTimeMillis();
        }else {
            //如果遇到发消息则返回
            AccessibilityNodeInfo node2 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.ID_SEND_MESSAGE);
            if (node2==null){
                node2 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SEND_MESSAGE);
            }
            if (node2==null){
                node2 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SEND_MESSAGE_EN);
            }
            if (node2 != null) {
                //LogUtils.debug(TAG, "发消息  return");
                lastUsername = username;
                needMove =true;
                CommonUtil.init().backPrePage(service);
            }else{
                needMove = false;
            }
        }
    }

    //附近的人列表
    private void handle_nearby_friends(WechatService service) {
        //点击列表中某个item 用户名
        long duration = System.currentTimeMillis() - clickedItemTime;
        if (duration<100){
            LogUtils.debug(TAG,"list 快速点击了 ="+duration);
            return;
        }
        clickNearByItem(service);
    }


    private void clickNearByItem(WechatService service) {
        AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        countSend = WechatService.COUNT_PAGE;
        AccessibilityNodeInfo node1 = AccessibilityHelper.findNodeInfosByIdAndPosition(nodeInfo, WechatUI.ID_NEARBY_USERNAME, countSend);

        if (node1 != null) {
            username = node1.getText().toString().trim();
            //boolean contains = !userList.contains(username);
            boolean contains =  WechatService.dbUtils.isAdded(username);
            if (!contains &&!username.equals(lastUsername)){
                rect = new Rect();
                node1.getBoundsInScreen(rect);
                LogUtils.debug(TAG,username+",clickNearByItem node 位置="+ rect.toString());
                AccessibilityHelper.performClick(node1);
                userList.add(username);
                WechatService.page_state = 1;
                clickedItemTime = System.currentTimeMillis();
            }else{
                LogUtils.debug(TAG,"needMove 不符合条件移动一下2 ");
                moveScreenDown(distance);//distance
            }
        }else{
            LogUtils.debug(TAG,"needMove current node1 null = ");
            moveScreenDown(distance);
        }
    }

    private synchronized void moveScreenDown(int height) {
        if (rect==null){
            rect = new Rect(50,100,50,600);
        }
        if (rect.bottom<=600){
            rect.bottom = 600;
        }
        if (height >= WechatService.winHeight*4/5){
            height = rect.bottom - distance ;// 不全部上拉
        }
        if (height<160){height=160;}

        AdbUtil.scrollDown(rect.right, rect.bottom, height);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rect = null;
        WechatService.COUNT_PAGE = 0;
        countSend = 0;
        needMove = false;
    }

    /**
     * 进入附近的人
     * @param service
     */
    public synchronized static void enterNearby(WechatService service) {
        AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        //发现
        AccessibilityNodeInfo node1 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_DISCOVERY);
        if (node1==null){
            node1 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_DISCOVERY_EN);
        }
        if (node1 != null) {
            AccessibilityHelper.performClick(node1);
            //CharSequence text = node1.getText();
        }
        //朋友圈
        AccessibilityNodeInfo node2 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_NEARBY);
        if (node2==null){
            node2 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_NEARBY_EN);
        }
        if (node2 != null) {
            AccessibilityHelper.performClick(node2);
        }
    }

}
