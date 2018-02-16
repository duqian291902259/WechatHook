package site.duqian.autowechat.android.fragment;


import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import site.duqian.autowechat.R;
import site.duqian.autowechat.android.SubActivity;
import site.duqian.autowechat.android.base.BaseFragment;
import site.duqian.autowechat.model.Config;
import site.duqian.autowechat.model.Constant;
import site.duqian.autowechat.utils.LogUtils;
import site.duqian.autowechat.utils.SPUtils;
import site.duqian.autowechat.utils.SystemUtil;
import site.duqian.autowechat.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutoReplyFragment extends BaseFragment {


    private static final String TAG = AutoReplyFragment.class.getSimpleName();
    @BindView(R.id.et_keyword)
    EditText et_keyword;
    @BindView(R.id.et_reply_content)
    EditText et_reply_content;
    @BindView(R.id.btn_save_config)
    Button btn_save_config;
    @BindView(R.id.tv_notify)
    TextView tv_notify;

    private SubActivity activity;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_auto_reply;
    }

    @Override
    protected void initViewAndData() {
        activity = (SubActivity) getActivity();
        final String keywords = Config.getKeywords();
        et_keyword.setText(keywords);
        final String replyContent = Config.getReplyContent();
        et_reply_content.setText(replyContent);

        //LogUtils.debug(TAG,"old keywords="+keywords);
        //LogUtils.debug(TAG,"old reply_content="+replyContent);
    }

    @OnClick(R.id.btn_save_config)
    public void saveConfig() {
        final String keywords = et_keyword.getText().toString().trim().replace("\n", "");
        if (TextUtils.isEmpty(keywords)) {
            ToastUtils.showToast(context, context.getString(R.string.config_empty_keyword));
            return;
        }
        final String reply_content = et_reply_content.getText().toString().trim();

        LogUtils.debug(TAG, "keywords=" + keywords);
        LogUtils.debug(TAG, "reply_content=" + reply_content);
        SPUtils.putString(context, Constant.SP_MSG_KEYWORDS, keywords);
        SPUtils.putString(context, Constant.SP_REPLY_CONTENT, reply_content);
        ToastUtils.showToast(context, context.getString(R.string.config_saved));
        SystemUtil.hideInputMethod(context, et_reply_content);
        //copy 2 Clipboard
        SystemUtil.copyText(context, reply_content);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (et_reply_content != null)
            SystemUtil.hideInputMethod(context, et_reply_content);
    }
}
