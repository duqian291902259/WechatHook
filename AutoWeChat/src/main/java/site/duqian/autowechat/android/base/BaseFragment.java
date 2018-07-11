package site.duqian.autowechat.android.base;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * baseFragment
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    public abstract int getContentViewId();
    protected Context context;
    protected View mRootView;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final int contentViewId = getContentViewId();
        mRootView =inflater.inflate(contentViewId,container,false);
        ButterKnife.bind(this,mRootView);//绑定framgent
        this.context = getActivity();
        initView(mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initViewAndData();
        super.onActivityCreated(savedInstanceState);
    }

    protected abstract void initViewAndData();
    protected void initView(View mRootView){};

    public static class MyHandler extends Handler {
        WeakReference<BaseFragment> mReference;

        public MyHandler(BaseFragment fragment) {
            mReference= new WeakReference<BaseFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseFragment fragment = mReference.get();
            if (fragment != null) {
                fragment.handleMyMessage(msg);
            }
        }
    }
    public void handleMyMessage(Message msg){}

}
