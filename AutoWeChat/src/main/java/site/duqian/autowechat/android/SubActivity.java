package site.duqian.autowechat.android;

import android.app.ActionBar;

import site.duqian.autowechat.R;
import site.duqian.autowechat.android.base.BaseActivity;
import site.duqian.autowechat.android.base.BaseFragment;
import site.duqian.autowechat.android.fragment.AutoReplyFragment;
import site.duqian.autowechat.android.fragment.CommonFragment;
import site.duqian.autowechat.android.fragment.HookFragment;
import site.duqian.autowechat.model.Constant;

public class SubActivity extends BaseActivity {


    private static final String TAG = SubActivity.class.getSimpleName();
    private SubActivity activity;
    private ActionBar actionBar;
    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViewAndData() {
        actionBar = getActionBar();
        activity = this;
        final int pageType = getIntent().getIntExtra(Constant.PAGE_TYPE, 0);
        BaseFragment fragment = null;
        switch (pageType){
            case Constant.PAGE_HOOK:
                fragment = new HookFragment();
                break;
            case Constant.PAGE_AUTO_REPLEY:
                fragment = new AutoReplyFragment();
                break;
            case Constant.PAGE_ABOUT:
                fragment = new CommonFragment();
                break;
            default:
                fragment = new CommonFragment();
                break;
        }
        changeFragement(fragment, R.id.fl_content);
    }
}
