package site.duqian.wchook.xposed;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import de.robv.android.xposed.XSharedPreferences;
import site.duqian.wchook.model.Constant;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 16:10.
 * E-mail: duqian2010@gmail.com
 * Description:SettingsHelper
 * remarks:
 */
public class SettingsHelper {
    private SharedPreferences mPreferences = null;
    private XSharedPreferences mXPreferences = null;//只能读sp配置文件不可写入
    private static SettingsHelper mSettingsHelper = null;

    //for xposed sp
    public static SettingsHelper getInstance() {
        if (mSettingsHelper == null) {
            synchronized (SettingsHelper.class) {
                if (mSettingsHelper == null) {
                    mSettingsHelper = new SettingsHelper();
                }
            }
        }
        return mSettingsHelper;
    }

    public SettingsHelper() {
        mXPreferences = new XSharedPreferences("site.duqian.wchook","config");
        mXPreferences.makeWorldReadable();
        reload();
    }

    public SettingsHelper(Context context) {
        this.mPreferences = context.getSharedPreferences(Constant.PREFERENCE_NAME, Context.MODE_PRIVATE);//1
    }

    public String getString(String key, String defaultValue) {
        if (mPreferences != null) {
            return mPreferences.getString(key, defaultValue);
        } else if (mXPreferences != null) {
            return mXPreferences.getString(key, defaultValue);
        }

        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        if (mPreferences != null) {
            return mPreferences.getInt(key, defaultValue);
        } else if (mXPreferences != null) {
            return mXPreferences.getInt(key, defaultValue);
        }

        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (mPreferences != null) {
            return mPreferences.getBoolean(key, defaultValue);
        } else if (mXPreferences != null) {
            return mXPreferences.getBoolean(key, defaultValue);
        }

        return defaultValue;
    }

    public void setString(String key, String value) {
        Editor editor = null;
        if (mPreferences != null) {
            editor = mPreferences.edit();
        } /*else if (mXPreferences != null) {
            editor = mXPreferences.edit();
        }*/

        if (editor != null) {
            editor.putString(key, value);
            editor.apply();
        }
    }

    public void setBoolean(String key, boolean value) {
        Editor editor = null;
        if (mPreferences != null) {
            editor = mPreferences.edit();
        } /*else if (mXPreferences != null) {
            editor = mXPreferences.edit();
        }*/

        if (editor != null) {
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public void setInt(String key, int value) {
        Editor editor = null;
        if (mPreferences != null) {
            editor = mPreferences.edit();
        }/* else if (mXPreferences != null) {
            editor = mXPreferences.edit();
        }*/

        if (editor != null) {
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public void reload() {
        if (mXPreferences != null) {
            mXPreferences.reload();
        }
    }
}