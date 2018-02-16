package site.duqian.autowechat.android.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    public Context context;
    public Bundle savedInstanceState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        this.context = this;
        setContentView(getContentViewId());
        ButterKnife.bind(this);

        //后退
        if(isShowBack()) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        initViewAndData();
    }

    protected boolean isShowBack() {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public abstract int getContentViewId();
    public abstract void initViewAndData();

    public void changeFragement(Fragment fragment, int containerViewId) {
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment)
                //.addToBackStack(null)
                .commit();
    }

    public class MyHandler extends Handler {
        WeakReference<BaseActivity> mActivityReference;

        public MyHandler(BaseActivity activity) {
            mActivityReference= new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseActivity activity = mActivityReference.get();
            if (activity != null) {
                activity.handleMyMessage(msg);
            }
        }

    }
    public void handleMyMessage(Message msg){}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
