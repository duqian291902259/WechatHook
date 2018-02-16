package site.duqian.autowechat.xposed.utils;

import android.app.Activity;

import site.duqian.autowechat.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static site.duqian.autowechat.utils.SystemUtil.getTime;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by DHB on 2017/2/8.
 */

public class XposedUtil {

    private static final String TAG = XposedUtil.class.getSimpleName();

    public static void markedAllMethod(String className, ClassLoader classLoader) {
        Class aClass = XposedHelpers.findClass(className, classLoader);
        markedAllMethod(aClass);
    }

    public static void markedAllMethod(Class aClass) {
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
            Object[] parameterTypes = method.getParameterTypes();
            Object[] parameterTypesAndCallback = new Object[parameterTypes.length + 1];
            System.arraycopy(parameterTypes, 0, parameterTypesAndCallback, 0, parameterTypes.length);
            parameterTypesAndCallback[parameterTypesAndCallback.length - 1] = new MarkedAllMethodCallback(method);
            XposedHelpers.findAndHookMethod(aClass, method.getName(), parameterTypesAndCallback);
        }
    }

    private static class MarkedAllMethodCallback extends XC_MethodHook {
        private String methodName;

        MarkedAllMethodCallback(Method method) {
            methodName = method.toGenericString();
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            XLogUtil.d(getTime(), methodName);
        }
    }


    public static void showAllField(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object o = field.get(object);
                if (o != null) {
                    XLogUtil.d(field.toGenericString(), o.toString());
                } else {
                    XLogUtil.d(field.toGenericString(), "null");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    public static void setFieldsAccessible(Object object, String fieldName, Type type) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == type && field.getName().equals(fieldName)) {
                field.setAccessible(true);
            }
        }
    }


    public static void setField(Object object, String fieldName, Object value, Type type) {
        Field field = getField(object, fieldName, type);
        if (field == null) return;
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Field getField(Object object, String fieldName, Type type) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == type && field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    public static String currentActivityName = "";
    public static void markAllActivity() {
        findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object thisObject = param.thisObject;
                String name = thisObject.getClass().getName();
                //Toast.makeText((Activity) thisObject, name, Toast.LENGTH_SHORT).show();
                LogUtils.debug(TAG, "ActivityName = " + name);
                currentActivityName = name;
            }
        });
    }
}
