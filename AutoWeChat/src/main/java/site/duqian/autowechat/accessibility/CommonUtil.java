package site.duqian.autowechat.accessibility;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import site.duqian.autowechat.R;
import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.ToastUtils;
import site.duqian.autowechat.utils.UIUtils;
import site.duqian.autowechat.wechat.WeChatHelper;
import site.duqian.autowechat.wechat.WechatUI;

import static site.duqian.autowechat.accessibility.WechatService.pasted;

/**
 * Created by duqian on 2017/2/7.
 */

public class CommonUtil {

    private static final String TAG = CommonUtil.class.getSimpleName();

    private Handler mHandler = null;
    private CommonUtil() {
        mHandler = UIUtils.getHandler();
        //mHandler = new Handler(Looper.getMainLooper());
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
            LogUtils.debug(TAG, "rootWindow is null");
            return null;
        }
        return nodeInfo;
    }

    //返回聊天界面
    public void backWeChatHomePage(WechatService service) {
        AccessibilityNodeInfo nodeInfo = checkRootWindow(service);
        if (nodeInfo == null) return;
        //返回
        AccessibilityNodeInfo node_back = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_BACK);
        if(node_back != null) {
            service.setDefalut();
            LuckyMoneyUtil.isOpend = false;
            AccessibilityHelper.performClick(node_back);
            final AccessibilityNodeInfo parent = node_back.getParent();
            AccessibilityHelper.performClick(parent);
            LogUtils.debug(TAG,"performClick back "+parent);
        }else{
            //AccessibilityHelper.performBack(service);//后退
        }
    }

    //点击联系人
    public void clickMessage(WechatService service) {
        AccessibilityNodeInfo nodeInfo = checkRootWindow(service);
        if (nodeInfo == null) return;
        AccessibilityNodeInfo node_contact = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_MSG_WRAP);
        if (node_contact != null) {
            AccessibilityHelper.performClick(node_contact);
        }
    }
    //发送信息给第一个联系人
    public void testSendMsg(WechatService service) {
        pasteText(service);
        sendText(service);
        service.setDefalut();
    }

    //粘贴信息
    public void pasteText(WechatService service) {
        final int delayMax = Config.getDelayMax();
        final int delayMin = Config.getDelayMin();
        //final int delayMillis = WeChatHelper.getRandom(delayMin,delayMax);
        final int delayMillis = WeChatHelper.getRandom(2000,3000);
        UIUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                pasteText(service, WechatUI.ID_CHAT_EDITVIEW);
            }
        }, delayMillis);
    }
    //发送
    public void sendText(WechatService service) {
        UIUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                send(service,WechatUI.ID_MSG_SEND);
            }
        }, 2000);

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
        if (node_editview != null&& !pasted) {//
            AccessibilityHelper.performClick(node_editview);
            AccessibilityHelper.performLongClick(node_editview);
            //SystemClock.sleep(WeChatHelper.getRandom(200,300));
            AccessibilityHelper.performPaste(node_editview);
            pasted = true;
        }else{
            LogUtils.debug(TAG, "pasteText node is null"+resId);
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
        if(node_send != null) {
            final int delayMillis = WeChatHelper.getRandom(800, 500);
            AccessibilityNodeInfo finalNode_send = node_send;
            UIUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AccessibilityHelper.performClick(finalNode_send);
                    LogUtils.debug(TAG,"node_send"+delayMillis);
                    CommonUtil.init().backWeChatHomePage(service);
                    service.setDefalut();
                }
            }, delayMillis);
        }else{
            LogUtils.debug(TAG, "sendText node is null "+resId);
            final Context context = UIUtils.getContext();
            ToastUtils.showToast(context,context.getString(R.string.as_error));
        }
    }



}
