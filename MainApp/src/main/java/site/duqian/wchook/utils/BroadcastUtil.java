package site.duqian.wchook.utils;

import android.content.Context;
import android.content.Intent;

import site.duqian.wchook.model.Constant;

/**
 * Created by duqian on 2017/3/8.
 */

public class BroadcastUtil {

    private static final String TAG = BroadcastUtil.class.getSimpleName();


    public static void sendMessage2Receiver(Context context, String message) {
        try {
            Intent intent = new Intent(Constant.ACTION_MEASSAG_RECEIVER);
            intent.putExtra("message",message);
            context.sendBroadcast(intent);
        } catch (Throwable e) {
            LogUtils.debug(TAG, "sendMessage2Receiver error " + e.toString());
        }
    }

}
