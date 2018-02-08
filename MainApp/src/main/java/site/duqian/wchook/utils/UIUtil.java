package site.duqian.wchook.utils;

import android.content.Context;
import android.graphics.Point;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import site.duqian.wchook.base.MyApplication;


/**
 * Created by duqian on 2017/5/8.
 */

public class UIUtil {

    private static final String TAG = UIUtil.class.getSimpleName();

    public static void copyText(Context context, String content) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!TextUtils.isEmpty(content)) {
            cmb.setText(content); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
            CharSequence text = cmb.getText();
        }
    }

    public static Context getContext() {
        return MyApplication.mContext;
    }

    //获取的是分辨率
    public static Point getDisplayPoint(Context context) {
        Point point = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(point);
        Log.d(TAG, "the screen size is " + point.toString());//1080*1920
        return point;
    }

    public static int[] getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[]{width, height};
    }


}
