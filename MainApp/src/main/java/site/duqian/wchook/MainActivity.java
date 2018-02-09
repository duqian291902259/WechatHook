package site.duqian.wchook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import site.duqian.wchook.accessibility.AccessibilityHelper;
import site.duqian.wchook.accessibility.WechatService;
import site.duqian.wchook.android.MapActivity;
import site.duqian.wchook.android.TestFun;
import site.duqian.wchook.android.WxJumpActivity;
import site.duqian.wchook.base.BaseActivity;
import site.duqian.wchook.database.FriendsDbUtils;
import site.duqian.wchook.model.Config;
import site.duqian.wchook.model.Constant;
import site.duqian.wchook.utils.LogUtils;
import site.duqian.wchook.utils.SystemUtil;
import site.duqian.wchook.utils.ThreadManager;
import site.duqian.wchook.utils.ToastUtil;
import site.duqian.wchook.utils.UIUtil;
import site.duqian.wchook.wechat.WeChatHelper;
import site.duqian.wchook.wechat.WechatUI;
import site.duqian.wchook.xposed.SettingsHelper;

import static site.duqian.wchook.accessibility.NearbyAs.sendMessageContent;
import static site.duqian.wchook.model.Constant.MAP_DEFAULT_LATITUDE;
import static site.duqian.wchook.model.Constant.MAP_DEFAULT_LONGITUDE;
import static site.duqian.wchook.model.Constant.SP_ADDRESS;
import static site.duqian.wchook.model.Constant.SP_DURATION;
import static site.duqian.wchook.model.Constant.SP_GREETING_CONTENT;
import static site.duqian.wchook.model.Constant.SP_IS_AUTO_REPLY;
import static site.duqian.wchook.model.Constant.SP_IS_REPLY_BY_AS;
import static site.duqian.wchook.model.Constant.SP_IS_REPLY_BY_XP;
import static site.duqian.wchook.model.Constant.SP_LATITUDE;
import static site.duqian.wchook.model.Constant.SP_LONGITUDE;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 16:11.
 * E-mail: duqian2010@gmail.com
 * Description:MainActivity 主UI
 * remarks:
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.et_config_time)
    EditText et_config_time;
    @BindView(R.id.et_send_greeting)
    EditText et_send_greeting;
    @BindView(R.id.et_longitude)
    EditText etLongitude;
    @BindView(R.id.et_latitude)
    EditText etLatitude;
    @BindView(R.id.tv_result)
    TextView tv_result;
    @BindView(R.id.btn_open_as)
    Button btnOpenAs;
    @BindView(R.id.btn_mock_click)
    Button btnMockClick;
    @BindView(R.id.rb_reply_xposed)
    RadioButton rb_reply_xposed;
    @BindView(R.id.rb_reply_as)
    RadioButton rb_reply_as;
    @BindView(R.id.rg_reply)
    RadioGroup rg_reply;
    @BindView(R.id.checkbox_reply)
    CheckBox checkbox_reply;
    @BindView(R.id.ll_open_reply)
    LinearLayout ll_open_reply;
    private SettingsHelper mSettings;
    private Context context;
    private MainActivity activity;
    private boolean running;
    private String wechatVersionName;
    private String btnTextClose = "关闭辅助功能";
    private String btnTextOpen = "开启辅助功能";
    private MessageBroadcast receiver;
    private TestFun testFun;
    private Config config;
    private String hello_content;
    private FriendsDbUtils dbUtils;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_result.setText(handleInfo);
        }
    };
    private String handleInfo;


    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViewAndData() {
        mSettings = new SettingsHelper(this);
        context = this;
        activity = this;
        initWechat();
        registerMessageReceiver();
        testFun = TestFun.init(context, mSettings);
        testFun.requestShellPermissions();
        rg_reply.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                configReply();
            }
        });
        et_config_time = (EditText) findViewById(R.id.et_config_time);
        getLastConfig();
    }


    @OnClick(R.id.btn_wx_jump)
    public void go2WxJump() {
        this.startActivity(new Intent(context, WxJumpActivity.class));
    }

    @OnCheckedChanged(R.id.checkbox_reply)
    public void openReply() {
        boolean checked = checkbox_reply.isChecked();
        ll_open_reply.setVisibility(checked ? View.VISIBLE : View.GONE);
        mSettings.setBoolean(SP_IS_AUTO_REPLY, checked);
        if (!checked) {//都关闭
            mSettings.setBoolean(SP_IS_REPLY_BY_XP, false);
            mSettings.setBoolean(SP_IS_REPLY_BY_AS, false);
        } else {
            rb_reply_xposed.setChecked(true);
            mSettings.setBoolean(SP_IS_REPLY_BY_XP, true);
        }
    }

    public void configReply() {
        int checkedRadioButtonId = rg_reply.getCheckedRadioButtonId();
        if (R.id.rb_reply_xposed == checkedRadioButtonId) {
            mSettings.setBoolean(SP_IS_REPLY_BY_XP, true);
            mSettings.setBoolean(SP_IS_REPLY_BY_AS, false);
        }
        if (R.id.rb_reply_as == checkedRadioButtonId) {
            mSettings.setBoolean(SP_IS_REPLY_BY_XP, false);
            mSettings.setBoolean(SP_IS_REPLY_BY_AS, true);
        }
    }

    private void getLastConfig() {
        hello_content = context.getResources().getString(R.string.text_send_greeting);
        String latitude = mSettings.getString(SP_LATITUDE, MAP_DEFAULT_LATITUDE);
        String longitude = mSettings.getString(SP_LONGITUDE, MAP_DEFAULT_LONGITUDE);
        String address = mSettings.getString(SP_ADDRESS, "");
        int duration = mSettings.getInt(SP_DURATION, 1000);
        LogUtils.debug(TAG, "duration=" + duration);

        et_config_time.setText(duration + "");
        String greetingContent = mSettings.getString(SP_GREETING_CONTENT, hello_content);
        if (TextUtils.isEmpty(greetingContent)) {
            greetingContent = hello_content;
        }
        et_send_greeting.setText(greetingContent);
        boolean isAutoReply = mSettings.getBoolean(SP_IS_AUTO_REPLY, false);
        boolean is_reply_by_xp = mSettings.getBoolean(SP_IS_REPLY_BY_XP, false);
        boolean is_reply_by_as = mSettings.getBoolean(SP_IS_REPLY_BY_AS, false);
        config = new Config(latitude, longitude, address, greetingContent, isAutoReply, is_reply_by_xp, is_reply_by_as);
        checkbox_reply.setChecked(isAutoReply);
        if (isAutoReply) {
            if (is_reply_by_xp) {
                rb_reply_xposed.setChecked(true);
            } else {
                rb_reply_as.setChecked(true);
            }
        }

        //LogUtils.debug(TAG, "as=" + is_reply_by_as + ",xp=" + is_reply_by_xp);
        setMapInfo(latitude, longitude, address);
    }


    private void registerMessageReceiver() {
        receiver = new MessageBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_MEASSAG_RECEIVER);
        context.registerReceiver(receiver, filter);
    }

    class MessageBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sendMessageContent = intent.getStringExtra("message");
            //LogUtils.debug(TAG,"message ="+sendMessageContent);
            UIUtil.copyText(context, sendMessageContent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void initWechat() {
        wechatVersionName = WeChatHelper.getWechatVersionName(context);
        WechatUI.initWechatUI(wechatVersionName);
        LogUtils.debug(TAG, "wechat version=" + wechatVersionName);
        WechatService.ACTION = Constant.ACTION_DEFAULT;
        WechatService.pasted = false;

        final String versionName = SystemUtil.getVersionName(context);
        setTitle(context.getString(R.string.app_name) + ":" + versionName);
        Constant.packageName = context.getPackageName();//这个包名改动了，一定要记得修改
    }

    @OnClick(R.id.btn_mock_click)
    public void mockSayHello() {
        if (checkService()) {
            if (WechatService.ACTION == Constant.ACTION_SAY_HELLO) {//cancle
                WechatService.ACTION = Constant.ACTION_DEFAULT;
                btnMockClick.setTextColor(getResources().getColor(R.color.colorBlack));
                btnMockClick.setText("模拟添加附近好友");
            } else {//start
                WechatService.ACTION = Constant.ACTION_SAY_HELLO;
                btnMockClick.setTextColor(getResources().getColor(R.color.colorGreen));
                btnMockClick.setText("取消模拟加好友");
                getDbCount();
                WechatService.COUNT_PAGE = 0;
                WechatService.startTime = System.currentTimeMillis();
                openWX();
            }
        }
    }

    //获取数据库记录
    private void getDbCount() {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                if (dbUtils == null) {
                    dbUtils = FriendsDbUtils.init(context);
                }
                List<String> friends = dbUtils.getAllFriends();
                if (friends != null) {
                    //WechatService.userList = friends;
                    handleInfo = "当前数据库总记录条数:" + friends.size();
                    LogUtils.debug(TAG, "getDbCount=" + handleInfo);
                    handler.sendEmptyMessage(0);
                }
            }

        });

    }

    private boolean checkService() {
        if (TextUtils.isEmpty(wechatVersionName)) {
            ToastUtil.toast(context, "无法打开，请确认已经安装微信");
            return false;
        }
        if (running) {
            return true;
        } else {
            ToastUtil.toast(context, context.getString(R.string.as_not_open));
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = WechatService.isRunning();
        if (running) {
            btnOpenAs.setText(btnTextClose);
            btnOpenAs.setTextColor(getResources().getColor(R.color.colorGreen));
        } else {
            btnOpenAs.setText(btnTextOpen);
            btnOpenAs.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        UIUtil.copyText(context, sendMessageContent);
    }


    @OnClick(R.id.tv_result)
    public void test() {
        testFun.requestShellPermissions();
    }


    @OnClick(R.id.btn_open_wechat)
    public void openWechat() {
        openWX();
    }

    private void openWX() {
        boolean isInstalled = true;
        try {
            WeChatHelper.openWechat(activity);
        } catch (Exception e) {
            LogUtils.debug("openWechat error " + e.toString());
            isInstalled = false;
            ToastUtil.toast(context, "无法打开，请确认已经安装微信");
        }
        if (!isInstalled) {
            return;
        }
        if (!TextUtils.isEmpty(wechatVersionName)) {
            if (wechatVersionName.contains("6.3.32") || wechatVersionName.contains("6.5.7")) {
                LogUtils.debug(TAG, "WechatHook与当前微信版本兼容");
            } else {
                ToastUtil.toast(context, "WechatHook与当前微信版本部分功能不兼容");
            }
        }
    }

    @OnClick(R.id.btn_open_as)
    public void openAccessibilityService() {
        boolean isOpen = btnOpenAs.getText().toString().contains(btnTextOpen);
        openService(isOpen);
    }

    private void openService(boolean isOpen) {
        final String service_name = context.getString(R.string.service_name);
        if (isOpen) {//开启辅助功能
            if (!running) {
                AccessibilityHelper.openAccessibilityServiceSettings(activity);
                ToastUtil.toast(context, "点击打开辅助服务-" + service_name);
            } else {
                ToastUtil.toast(context, service_name + "辅助服务-已是开启状态");
            }
        } else {//关闭辅助功能
            if (running) {
                AccessibilityHelper.openAccessibilityServiceSettings(activity);
                ToastUtil.toast(context, "请点击关闭辅助服务：" + service_name);
            } else {
                ToastUtil.toast(context, service_name + "辅助服务-已是关闭状态");
            }
        }
    }

    @OnClick(R.id.btn_save)
    public void saveConfigs() {
        String longitude = etLongitude.getText().toString().trim();//经度
        String latitude = etLatitude.getText().toString().trim();//纬度
        if (TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude)) {
            ToastUtil.toast(context, "position info can not be null!");
            return;
        }
        String greetingContent = et_send_greeting.getText().toString().trim();
        mSettings.setString(Constant.SP_GREETING_CONTENT, greetingContent);

        String configTime = et_config_time.getText().toString().trim();
        mSettings.setInt(Constant.SP_DURATION, Integer.parseInt(configTime));

        save2sp(longitude, latitude);
    }

    private void save2sp(String longitude, String latitude) {
        mSettings.setString(SP_LATITUDE, latitude);
        mSettings.setString(SP_LONGITUDE, longitude);
        ToastUtil.toast(context, "配置已保存！");
    }

    private long mPressedTime = 0;

    @OnClick(R.id.btn_clear_config)
    public void getConfig() {
        //getLastConfig();
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - mPressedTime) >= 2000) {//比较两次按键时间差
            mPressedTime = mNowTime;
            getDbCount();
            ToastUtil.toastShort(context, "双击清除历史记录数据");
            return;
        }
        //清除记录
        deleteHistory();
    }

    private void deleteHistory() {
        ThreadManager.getBackgroundPool().execute(new Runnable() {
            @Override
            public void run() {
                FriendsDbUtils dbUtils = FriendsDbUtils.init(context);
                boolean deleteAllUsers = dbUtils.deleteAllUsers();
                if (deleteAllUsers) {
                    ToastUtil.toast(context, "已经清除打招呼的历史记录!");
                } else {
                    LogUtils.debug(TAG, "清除记录");
                }
            }
        });
    }

    @OnClick(R.id.btn_get_map)
    public void getFromMap() {
        startActivityForResult(new Intent(context, MapActivity.class), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (100 == requestCode) {
            LogUtils.debug(TAG, "got it");
            try {
                String latitude = data.getStringExtra(SP_LATITUDE);
                String longitude = data.getStringExtra(SP_LONGITUDE);
                String address = data.getStringExtra(SP_ADDRESS);
                mSettings.setString(SP_ADDRESS, address);
                setMapInfo(latitude, longitude, address);
                save2sp(longitude, latitude);
            } catch (Exception e) {
                LogUtils.debug(TAG, e.toString());
            }
        }
    }

    private void setMapInfo(String latitude, String longitude, String address) {
        String postion = "当前配置:经度=" + longitude + ",纬度=" + latitude + "\n位置=" + address + "\n\n";
        LogUtils.debug(TAG, postion);
        etLongitude.setText(longitude);
        etLatitude.setText(latitude);
        String text = postion + context.getResources().getString(R.string.feature_list);
        tv_result.setText(text);
    }

    @Override
    protected boolean isShowBack() {
        return false;
    }
}
