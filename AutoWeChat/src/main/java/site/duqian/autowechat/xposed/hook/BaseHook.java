package site.duqian.autowechat.xposed.hook;

/**
 * Created by DHB on 2017/2/8.
 */

public class BaseHook {
    protected ClassLoader classLoader;

    BaseHook(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
