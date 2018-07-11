package site.duqian.autowechat.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.Log;

import site.duqian.autowechat.R;
import site.duqian.autowechat.accessibility.AccessibilityHelper;
import site.duqian.autowechat.accessibility.PyqUtil;
import site.duqian.autowechat.accessibility.WechatService;
import site.duqian.autowechat.android.MainActivity;
import site.duqian.autowechat.android.SubActivity;
import site.duqian.autowechat.android.base.BaseSettingsFragment;
import site.duqian.autowechat.android.view.CustomSwitchPreference;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.SystemUtil;
import site.duqian.autowechat.utils.ToastUtils;
import site.duqian.autowechat.wechat.WeChatHelper;


/**
 * 主界面
 * create an instance of this fragment.
 */
public class MainFragment extends BaseSettingsFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = MainFragment.class.getSimpleName();
    private MainActivity activity;
    private Context context;
    private boolean running;
    private static final String key_wechat_enable = "KEY_WECHAT_ENABLE";
    private static final String key_open_wechat = "KEY_OPEN_WECHAT";
    private static final String key_enter_pyq = "KEY_ENTER_PYQ";
    private static final String key_send_pyq = "KEY_SEND_PYQ";
    private static final String key_robot_repley = "KEY_ROBOT_REPLEY";
    private static final String key_wechat_message = "KEY_WECHAT_MESSAGE";
    private static final String key_lucky_money = "KEY_LUCKY_MONEY";
    private static final String key_no_reply = "KEY_NO_REPLY";
    private static final String key_send_message = "KEY_SEND_MESSAGE";
    private static final String key_auto_reply = "KEY_AUTO_REPLY";
    private static final String key_accessibility_reply = "KEY_ACCESSIBILITY_REPLY";

    @Override
    protected void initData() {
        addPreferencesFromResource(R.xml.main);
        activity = (MainActivity) getActivity();
        context = activity.getApplicationContext();

        findPreference(key_open_wechat).setOnPreferenceClickListener(this);
        findPreference(key_enter_pyq).setOnPreferenceClickListener(this);
        findPreference(key_send_pyq).setOnPreferenceClickListener(this);
        findPreference(key_lucky_money).setOnPreferenceClickListener(this);

        //findPreference(key_no_reply).setOnPreferenceClickListener(this);
        findPreference(key_send_message).setOnPreferenceClickListener(this);
        findPreference(key_auto_reply).setOnPreferenceClickListener(this);

        findPreference(key_wechat_enable).setOnPreferenceChangeListener(this);
        findPreference(key_robot_repley).setOnPreferenceChangeListener(this);
        findPreference(key_wechat_message).setOnPreferenceChangeListener(this);
        findPreference(key_accessibility_reply).setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        running = WechatService.isRunning();
        CustomSwitchPreference wechatPref = (CustomSwitchPreference) findPreference(key_wechat_enable);
        if (running) {
            wechatPref.setChecked(true);
        } else {
            wechatPref.setChecked(false);
        }

        //LogUtils.debug(TAG,"isOpenRobotReply="+SPUtils.getBoolean(context,key_robot_repley,false));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        boolean isOpen = (boolean) newValue;
        switch (key) {
            case key_wechat_enable:
                openService(isOpen);
                break;
            case key_robot_repley:
                openRobotRepley(isOpen);
                break;
            case key_accessibility_reply:
                openAccessibilityRepley(isOpen);
                break;
            case key_wechat_message:
                openMessageListener(isOpen);
            case key_lucky_money:
                openLuckyMoneyService(isOpen);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();
        switch (key) {
            case key_open_wechat:
                openWeChat();
                break;
            case key_enter_pyq:
                enterPYQ();
                break;
            case key_send_pyq:
                sendPYQ();
                break;
            case key_auto_reply:
                replyMessage();
                break;
            case key_send_message:
                sendMessage();
                break;
            default:
                break;
        }
        return true;
    }


    private void openMessageListener(boolean isOpen) {
        SPUtils.putBoolean(context, Constant.isListenerMessage, isOpen);
        ToastUtils.showToast(context, isOpen + "");
        LogUtils.debug(TAG, "isListenerMessage=" + isOpen);
    }


    private void openRobotRepley(boolean isOpen) {
        if (isOpen) {
            ToastUtils.showToast(context, context.getString(R.string.action_robot_reply_open));
        } else {
            ToastUtils.showToast(context, context.getString(R.string.action_robot_reply_close));
        }
        SPUtils.putBoolean(context, Constant.SP_ROBOT_REPLEY, isOpen);
    }

    private void openAccessibilityRepley(boolean isOpen) {
        if (checkService()) {
            if (isOpen) {
                ToastUtils.showToast(context, context.getString(R.string.action_accessibility_reply_open));
            } else {
                ToastUtils.showToast(context, context.getString(R.string.action_accessibility_reply_close));
            }
            SPUtils.putBoolean(context, Constant.SP_ACCESSIBILITY_REPLEY, isOpen);
        }else{
            CustomSwitchPreference preference = (CustomSwitchPreference) (findPreference(key_accessibility_reply));
            preference.setChecked(false);
        }
    }

    private void enterPYQ() {
        if (checkService()) {
            WechatService.ACTION = Constant.ACTION_ENTER_PYQ;
            WeChatHelper.openWechat(activity);
        }
    }

    private void sendPYQ() {
        if (checkService()) {
            PyqUtil.isClickRightBtn = false;
            PyqUtil.isClickComplete = false;
            SystemUtil.copyText(context, "本人正忙，有事请微信。。。" + WeChatHelper.getRandomDefault());
            WechatService.ACTION = Constant.ACTION_SEND_PYQ;
            WeChatHelper.openWechat(activity);
        }
    }

    private boolean checkService() {
        if (running) {
            return true;
        } else {
            ToastUtils.showCenterToast(context, context.getString(R.string.as_not_open));
        }
        return false;
    }

    private void openWeChat() {
        try {
            WechatService.ACTION = Constant.ACTION_OPEN_WECHAT;
            WeChatHelper.openWechat(activity);
        } catch (Exception e) {
            Log.d("main error ", e.toString());
        }
    }

    private void openService(boolean isOpen) {
        final String service_name = context.getString(R.string.service_name);
        if (isOpen) {
            if (!running) {
                AccessibilityHelper.openAccessibilityServiceSettings(activity);
                ToastUtils.showCenterToast(context, "点击打开辅助服务-" + service_name);
            } else {
                ToastUtils.showCenterToast(context, service_name + "辅助服务-已是开启状态");
            }
        } else {
            if (running) {
                AccessibilityHelper.openAccessibilityServiceSettings(activity);
                ToastUtils.showCenterToast(context, "请点击关闭辅助服务：" + service_name);
            } else {
                ToastUtils.showCenterToast(context, service_name + "辅助服务-已是关闭状态");
            }
        }
    }

    private void replyMessage() {
        final Intent intent = new Intent(activity, SubActivity.class);
        intent.putExtra(Constant.PAGE_TYPE, Constant.PAGE_AUTO_REPLEY);
        activity.startActivity(intent);
    }

    private void sendMessage() {
        if (checkService()) {
            SystemUtil.copyText(context, "本人正忙，稍后回复。。。祝身体健康，万事如意");
            WechatService.ACTION = Constant.ACTION_SEND_MESSAGE;
            WeChatHelper.openWechat(activity);
        }
    }

    private void openLuckyMoneyService(boolean isOpen) {
        //抢红包功能开关
        if (isOpen) {
            ToastUtils.showToast(context, context.getString(R.string.action_lucky_money_open));
        } else {
            ToastUtils.showToast(context, context.getString(R.string.action_lucky_money_close));
        }
        SPUtils.putBoolean(context, Constant.SP_LUCKY_MONEY, isOpen);
    }

}
