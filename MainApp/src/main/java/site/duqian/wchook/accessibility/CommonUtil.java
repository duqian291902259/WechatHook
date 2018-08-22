package site.duqian.wchook.accessibility;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.SystemUtil;
import site.duqian.wchook.wechat.WeChatHelper;
import site.duqian.wchook.wechat.WechatUI;

import static site.duqian.wchook.accessibility.NearbyAs.sendMessageContent;
import static site.duqian.wchook.accessibility.WechatService.pasted;

/**
 * Created by duqian on 2017/2/7.
 */

public class CommonUtil {

    private static final String TAG = CommonUtil.class.getSimpleName();

    private Handler mHandler = null;
    private CommonUtil() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    private static final CommonUtil instance = new CommonUtil();
    public static CommonUtil init() {
        return instance;
    }

    private Handler getHandler() {
        if(mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }


    @Nullable
    public AccessibilityNodeInfo checkRootWindow(WechatService service) {
        AccessibilityNodeInfo nodeInfo = service.getRootInActiveWindow();
        if(nodeInfo == null) {
            //LogUtils.debug(TAG, "rootWindow is null");
            return null;
        }
        return nodeInfo;
    }

    //返回上一个界面
    public void backPrePage(WechatService service) {
        AccessibilityNodeInfo nodeInfo = checkRootWindow(service);
        if (nodeInfo == null) return;
        //返回
        AccessibilityNodeInfo node_back = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_BACK);
        if(node_back != null) {
            AccessibilityHelper.performClick(node_back);
            final AccessibilityNodeInfo parent = node_back.getParent();
            AccessibilityHelper.performClick(parent);
        }
    }

    private long pasteTime = 0;
    private long sendTime = 0;
    //粘贴信息
    public void pasteText(WechatService service) {
        if (System.currentTimeMillis()-pasteTime<500){
            LogUtils.debug(TAG,"pasteText repeate");
            return;
        }
        pasteTime = System.currentTimeMillis();
        final int delayMillis = WeChatHelper.getRandom(1200,1500);
        //LogUtils.debug(TAG,"pasteText "+delayMillis);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pasteText(service, WechatUI.ID_SEND_HELLO_EDIT_VIEW);
                //send(service,WechatUI.ID_SEND_HELLO_BUTTON);
                sendText(service);
            }
        }, delayMillis);
    }
    //发送
    public void sendText(WechatService service) {
        if (System.currentTimeMillis()-sendTime<500){
            LogUtils.debug(TAG,"pasteText repeate");
            return;
        }
        sendTime = System.currentTimeMillis();
        final int delayMillis = WeChatHelper.getRandom(1000, 1500);
        //LogUtils.debug(TAG,"node_send "+delayMillis);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                send(service,WechatUI.ID_SEND_HELLO_BUTTON);
            }
        },delayMillis);
    }

    //粘贴信息
    public void pasteText(WechatService service,String resId) {
        AccessibilityNodeInfo node_editview;
        final AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        //文本框
        node_editview = AccessibilityHelper.findNodeInfosById(nodeInfo, resId);
        if (node_editview==null){
            node_editview = AccessibilityHelper.findNodeInfosByClassName(nodeInfo, WechatUI.CLASS_NAME_EDITVIEW);
        }

        if (SystemUtil.hasLollipop()) {
            AccessibilityHelper.performSetText(node_editview, sendMessageContent);
            pasted = true;
        }else{
            pasteText2(resId, node_editview);
        }
    }

    public void pasteText2(String resId, AccessibilityNodeInfo node_editview) {
        if (node_editview != null&& !pasted) {//这种pasteText在部分手机上不兼容
            AccessibilityHelper.performClick(node_editview);
            AccessibilityHelper.performLongClick(node_editview);
            AccessibilityHelper.performPaste(node_editview);
           pasted = true;
        }else{
            //LogUtils.debug(TAG, "pasteText node is null"+resId);
        }
    }

    //发送
    public void send(WechatService service,String resId) {
        final AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        AccessibilityNodeInfo node_send = AccessibilityHelper.findNodeInfosById(nodeInfo, resId);
        if (node_send==null){
            node_send = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SEND);
        }
        if (node_send==null){
            node_send = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SEND_EN);
        }
        if(node_send != null) {
            AccessibilityHelper.performClick(node_send);
            CommonUtil.init().backPrePage(service);//后退
            service.setDefault();
        }else{
            //LogUtils.debug(TAG, "sendText node is null "+resId);
        }
    }

}
