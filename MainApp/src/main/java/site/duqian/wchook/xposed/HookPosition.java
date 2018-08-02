package site.duqian.wchook.xposed;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import site.duqian.wchook.utils.LogUtils;

import static site.duqian.wchook.xposed.CommonHook.hook_method;
import static site.duqian.wchook.xposed.CommonHook.hook_methods;

/**
 * Created by Dusan (duqian) on 2017/5/6 - 17:36.
 * E-mail: duqian2010@gmail.com
 * Description:HookPosition
 * remarks:修改地理位置
 */
public class HookPosition {
    private static final String TAG = HookPosition.class.getSimpleName();

    public static void hookPostion(ClassLoader classLoader, SettingsHelper mSettings){
        hook_method("android.net.wifi.WifiManager", classLoader, "getScanResults", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //LogUtils.debug(TAG,"getScanResults  "+param.getResult());
                param.setResult(null);

            }
        });

        hook_method("android.telephony.TelephonyManager", classLoader, "getCellLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //LogUtils.debug(TAG,"getCellLocation  "+param.getResult());
                param.setResult(null);
            }
        });

        hook_method("android.telephony.TelephonyManager", classLoader, "getNeighboringCellInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //LogUtils.debug(TAG,"getNeighboringCellInfo "+param.getResult());
                param.setResult(null);
            }
        });

        hook_methods("android.location.LocationManager", "requestLocationUpdates", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args.length == 4 && (param.args[0] instanceof String)) {
                    LocationListener ll = (LocationListener) param.args[3];
                    Class<?> clazz = LocationListener.class;
                    Method m = null;
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.getName().equals("onLocationChanged")) {
                            m = method;
                            break;
                        }
                    }

                    try {
                        if (m != null) {
                            mSettings.reload();
                            Object[] args = new Object[1];
                            Location l = new Location(LocationManager.GPS_PROVIDER);
                            //车陂
                            String latitude = mSettings.getString("latitude", "23.13080840");
                            String longitude = mSettings.getString("longitude", "113.4014138");
                            LogUtils.debug(TAG, "xposed postion: latitude=" + latitude + ",longitude=" + longitude);
                            double la = Double.parseDouble(latitude);//-10001
                            double lo = Double.parseDouble(longitude);//-10001
                            l.setLatitude(la);
                            l.setLongitude(lo);
                            args[0] = l;
                            //onLocationChanged
                            m.invoke(ll, args);
                            //CommonHook.log(TAG,"fake location: " + la + ", " + lo);
                        }
                    } catch (Exception e) {
                        CommonHook.log(TAG,e.toString());
                    }
                }
            }
        });

        hook_methods("android.location.LocationManager", "getGpsStatus", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                GpsStatus gss = (GpsStatus) param.getResult();
                if (gss == null)
                    return;
                Class<?> clazz = GpsStatus.class;
                Method m = null;
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals("setStatus")) {
                        if (method.getParameterTypes().length > 1) {
                            m = method;
                            break;
                        }
                    }
                }
                // 私有方法 GpsStatus.setStatus
                if (m!=null)
                m.setAccessible(true);
                //make the apps belive GPS works fine now
                int svCount = 5;
                int[] prns = {1, 2, 3, 4, 5};
                float[] snrs = {0, 0, 0, 0, 0};
                float[] elevations = {0, 0, 0, 0, 0};
                float[] azimuths = {0, 0, 0, 0, 0};
                int ephemerisMask = 0x1f;
                int almanacMask = 0x1f;
                //5 satellites are fixed
                int usedInFixMask = 0x1f;
                try {
                    if (m != null) {
                        m.invoke(gss, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                        param.setResult(gss);
                    }
                } catch (Exception e) {
                    CommonHook.log(TAG,e.toString());
                }
            }
        });
        
    }
}
