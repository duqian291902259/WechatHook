package site.duqian.autowechat.android.fragment;


import android.support.v4.app.Fragment;
import android.widget.TextView;

import site.duqian.autowechat.R;
import site.duqian.autowechat.android.SubActivity;
import site.duqian.autowechat.android.base.BaseFragment;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonFragment extends BaseFragment {

    @BindView(R.id.tv_about)
    TextView tv_about;
    private SubActivity activity;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_common;
    }

    @Override
    protected void initViewAndData() {
        activity = (SubActivity) getActivity();
        activity.setTitle(R.string.about_tips);
    }

    @OnClick(R.id.tv_about)
    public void openLuckyMoney(){
        if ((System.currentTimeMillis() - pressTime) > 1000) {
            pressTime = System.currentTimeMillis();
        } else {
            boolean  isOpenLuckyMoney = SPUtils.getBoolean(context, Constant.SP_LUCKY_MONEY, false);
            if (isOpenLuckyMoney) {
                ToastUtils.showToast(context,context.getString(R.string.action_lucky_money_close));
            }else {
                ToastUtils.showToast(context,context.getString(R.string.action_lucky_money_open));
            }

            SPUtils.putBoolean(context, Constant.SP_LUCKY_MONEY, !isOpenLuckyMoney);
        }

    }

    private long pressTime;

}
