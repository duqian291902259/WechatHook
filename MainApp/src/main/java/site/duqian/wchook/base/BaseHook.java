package site.duqian.wchook.base;

import android.content.Context;

/**
 * Created by Dusan (duqian) on 2017/5/11 - 12:24.
 * E-mail: duqian2010@gmail.com
 * Description:BaseHook
 * remarks:
 */
public class BaseHook {
    protected ClassLoader classLoader;
    protected Context context;

    public BaseHook(ClassLoader classLoader,Context context) {
        this.classLoader = classLoader;
        this.context = context;
    }
}
