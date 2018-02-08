package site.duqian.wchook.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by duqian on 2017/4/25.
 */

public class ToastUtil {

    private static Handler handler;
    private static boolean isLong = false;

    public static void toastLong(final Context context, final String... args){
        isLong = true;
        toast(context,args);
    }

    public static void toastShort(final Context context, final String... args){
        isLong = false;
        toast(context,args);
    }

    public static void toast(final Context context, final String... args){
        //if (handler==null) {
            handler = new Handler(Looper.getMainLooper());
        //}
        handler.post(new Runnable() {
            @Override
            public void run() {
                makeText(context,args);
            }
        });
    }

    private static void makeText(Context context, String... args){
        StringBuilder sb = new StringBuilder();
        String temp = "";
        for (Object obj : args){
            if(obj!=null){
                temp = obj.toString();
            }else{
                temp = " *null* ";
            }
            sb.append(temp);
        }
        if (isLong) {
            Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
