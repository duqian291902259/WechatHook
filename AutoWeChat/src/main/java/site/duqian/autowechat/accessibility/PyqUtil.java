package site.duqian.autowechat.accessibility;

import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.UIUtils;
import site.duqian.autowechat.wechat.WechatUI;

import java.util.List;

/**
 * Created by duqian on 2017/2/7.
 */

public class PyqUtil {

    private static final String TAG = PyqUtil.class.getSimpleName();

    public static int SEND_TYPE = 0;
    public static final int SEND_TEXT= 1;
    public static final int SEND_PHOTOS= 2;
    public static final int SEND_VIDEOS= 3;
    private Handler mHandler = null;
    private String className;

    private PyqUtil() {
        mHandler = UIUtils.getHandler();
        //mHandler = new Handler(Looper.getMainLooper());
        SEND_TYPE = SEND_PHOTOS;
    }
    private static final PyqUtil instance = new PyqUtil();
    public static PyqUtil init() {
        return instance;
    }

    public void sendPYQ(AccessibilityEvent event, WechatService service) {
        className = event.getClassName().toString();
        if(WechatUI.UI_LUANCHER.equals(className)) {
            enterCircleOfFriends(event, service);
            //LogUtils.debug(TAG,"sendPYQ start");
        }
        if (SEND_TYPE == SEND_TEXT) {
            sendText(service);
        } else if (SEND_TYPE == SEND_PHOTOS) {
            sendPhotos(service);
        }
    }

    private void sendText(WechatService service) {

    }

    public static boolean isClickRightBtn = false;
    public static boolean isClickComplete = false;
    private void sendPhotos( WechatService service) {
        AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null){
            return;
        }
        //1，点击相机图标
        if (WechatUI.UI_SNS_TIME_LINE.equals(className)&&!isClickRightBtn) {
            AccessibilityNodeInfo node1 = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_SEND_PYQ);
            if (node1 != null) {
                AccessibilityHelper.performClick(node1);
                isClickRightBtn = true;
            } else {
                LogUtils.debug(TAG, "点击相机图标 node  null");
                service.setDefalut();
            }
        }
        //有可能出现对话框：我知道了
        clickIKnow(service);
        //从相册中选择
        if (WechatUI.UI_PHOTO_VIDEO.equals(className)) {
            AccessibilityNodeInfo node3 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SELECT_FROM_PHOTO);
            if (node3 != null) {
                AccessibilityHelper.performClick(node3);
            } else {
                LogUtils.debug(TAG, "点击 相册 node  null");//从相册中选择 相册
            }
        }

        //选择相片
        if (WechatUI.UI_AlbumPreview.equals(className)||className.contains(WechatUI.UI_BASE)) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(WechatUI.ID_PHOTO_CHECKBOX_WRAP);//ID_PHOTO_CHECKBOX
            for (int i = 0; i < list.size(); i++) {
                final AccessibilityNodeInfo node = list.get(i);
                if (i< Config.SEND_PIC_NUMBER) {
                    if (node != null) {
                        AccessibilityHelper.performClick(node);
                    } else {
                        LogUtils.debug(TAG, "相册中选择 node  null");
                    }
                }
            }
        }
        //点完成
        if (WechatUI.UI_AlbumPreview.equals(className)&&!isClickComplete) {
            AccessibilityNodeInfo node_complete = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_SELECT_COMPLETE);
            if (node_complete != null) {
                AccessibilityHelper.performClick(node_complete);
                isClickComplete = true;
                LogUtils.debug(TAG, "点击完成 node "+node_complete);
            } else {
                LogUtils.debug(TAG, "点击完成 node  null");
            }
        }

        if (WechatUI.UI_SNS_UPLOAD.equals(className)) {
            //复制／粘贴文本信息
            CommonUtil.init().pasteText(service, WechatUI.ID_SEND_EDIT_VIEW);
            //发送
            CommonUtil.init().send(service, WechatUI.ID_SEND_PYQ_BUTTON);
        }
    }

    //长按，发送文本
    private void clickIKnow(WechatService service) {
        AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        //点击我知道了
        AccessibilityNodeInfo node2 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_I_KNOWN);
        if (node2==null)
            node2 = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_SEND_TEXT_TIPS);
        if (node2==null)
            node2 = AccessibilityHelper.findNodeInfosById(nodeInfo, WechatUI.ID_I_KNOW);

        if(node2 != null) {
            AccessibilityHelper.performLongClick(node2);
        }else{
            //LogUtils.debug(TAG,"点击 我知道了 node  null");
        }
    }

    //进入朋友圈
    public void enterCircleOfFriends(AccessibilityEvent event, WechatService service) {
        AccessibilityNodeInfo nodeInfo = CommonUtil.init().checkRootWindow(service);
        if (nodeInfo == null) return;
        //发现
        AccessibilityNodeInfo node1 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_DISCOVERY);
        if (node1 != null) {
            AccessibilityHelper.performClick(node1);
        }
        //朋友圈
        AccessibilityNodeInfo node2 = AccessibilityHelper.findNodeInfosByText(nodeInfo, WechatUI.TEXT_FRIENDS);
        if (node2 != null) {
            AccessibilityHelper.performClick(node2);
        }
    }

    public void enterPYQ(AccessibilityEvent event, WechatService service) {
        PyqUtil.init().enterCircleOfFriends(event, service);
        String className = event.getClassName().toString();
        if (WechatUI.UI_SNS_TIME_LINE.equals(className) || WechatUI.UI_WxViewPager.equals(className)) {
            //UIUtils.swipeUp();
            service.setDefalut();
        }else{
            //LogUtils.debug(TAG, "not in pyq");
        }
    }

}
