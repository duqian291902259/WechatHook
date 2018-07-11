package site.duqian.autowechat.android;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import site.duqian.autowechat.R;
import site.duqian.autowechat.accessibility.AccessibilityHelper;
import site.duqian.autowechat.android.base.BaseActivity;
import site.duqian.autowechat.android.fragment.MainFragment;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.SystemUtil;
import site.duqian.autowechat.utils.ToastUtils;
import site.duqian.autowechat.wechat.WeChatHelper;
import site.duqian.autowechat.wechat.WechatUI;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MainActivity activity;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViewAndData() {
        activity =this;
        if (savedInstanceState == null) {
            showMainMenu();
        }
        final String versionName = SystemUtil.getVersionName(context);
        setTitle(context.getString(R.string.app_name)+":"+versionName);
        Constant.packageName = context.getPackageName();//这个包名改动了，一定要记得修改
        final String wechatVersionName = WeChatHelper.initWechat(context);
        WechatUI.initWechatUI(wechatVersionName);

        String filter_keywords = SPUtils.getString(context,"filter_keywords","");
        if (TextUtils.isEmpty(filter_keywords)){
            filter_keywords = context.getString(R.string.action_filter_text);
            SPUtils.putString(context,"filter_keywords",filter_keywords);
        }

        int delay_max = SPUtils.getInt(context,"delay_max",10);
        int delay_min = SPUtils.getInt(context,"delay_min",0);
        if (delay_max<delay_min+5){
            delay_max = delay_min+5;
            SPUtils.putInt(context,"delay_max",delay_max);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        final boolean serviceRunning = SystemUtil.isServiceRunning(context, RobotService.class);
        if(!serviceRunning){
            startService(new Intent(context, RobotService.class));
        }*/
    }

    public void showMainMenu() {
        getFragmentManager().beginTransaction().add(R.id.fl_content,
                new MainFragment()).commitAllowingStateLoss();
    }

    @Override
    protected boolean isShowBack() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //分组，id，排序
        MenuItem item = menu.add(0, 1, 1, R.string.action_accessibility);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        MenuItem notifyitem = menu.add(0, 2, 2, R.string.action_about);
        notifyitem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);

        MenuItem xposeditem = menu.add(0, 3, 3, R.string.action_xposed);
        xposeditem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent intent = new Intent(context, SubActivity.class);
        switch (item.getItemId()) {
            case 1:
                AccessibilityHelper.openAccessibilityServiceSettings(activity);
                final String service_name = context.getString(R.string.service_name);
                ToastUtils.showCenterToast(context, "点击打开辅助服务-"+ service_name);
                return true;
            case 2:
                intent.putExtra(Constant.PAGE_TYPE,Constant.PAGE_ABOUT);
                context.startActivity(intent);
                break;
            case 3:
                intent.putExtra(Constant.PAGE_TYPE,Constant.PAGE_HOOK);
                context.startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
