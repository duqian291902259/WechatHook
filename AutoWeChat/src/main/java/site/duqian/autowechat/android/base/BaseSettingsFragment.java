package site.duqian.autowechat.android.base;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import site.duqian.autowechat.model.Constant;

/**
 * <p>Created 16/2/5 下午9:06.</p>
 * <p><a href="mailto:codeboy2013@gmail.com">Email:codeboy2013@gmail.com</a></p>
 * <p><a href="http://www.happycodeboy.com">LeonLee Blog</a></p>
 *
 * @author LeonLee
 */
public abstract class BaseSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        getPreferenceManager().setSharedPreferencesName(Constant.PREFERENCE_NAME);
        initData();

    }

    protected abstract void initData();

}
