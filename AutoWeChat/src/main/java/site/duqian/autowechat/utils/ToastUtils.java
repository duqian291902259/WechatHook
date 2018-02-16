package site.duqian.autowechat.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {

	public static void showToast(final Activity context, final String msg) {
		if ("main".equals(Thread.currentThread().getName())) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		} else {
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
    public static void showToast(final Context context, final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public static void showCenterToast(final Context context, final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            createCenterToast(context, msg);
        } else {
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    createCenterToast(context, msg);
                }
            });
        }
    }

    private static void createCenterToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showCenterToast(final Activity context, final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            createCenterToast(context, msg);
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createCenterToast(context, msg);
                }
            });
        }
    }
}
