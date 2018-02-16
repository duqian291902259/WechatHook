package site.duqian.autowechat.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import site.duqian.autowechat.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class MyHttpUtils {
    private static final String TAG = MyHttpUtils.class.getSimpleName();

    /**
     * 路径编码为UTF-8的格式
     * @param url
     * @return
     */
    public static String encodeUrl2UTF8(String url) {
        try {
            url = URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20");//路径编码
            //url = URLEncoder.encode(url, "UTF-8");//路径编码
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String decodeString(String str) {
        try {
            str = URLDecoder.decode(str, "UTF-8");//路径编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * unicode解码（unicode编码转中文）
     * @param theString
     * @return
     */
    public static String unicodeDecode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }


    /**
     * 根据本机IP，获取当前serve访问地址
     * @param ip_address
     * @return
     */
    public static String formatIPAddress(String ip_address) {
        try{
            if (!TextUtils.isEmpty(ip_address)) {
                ip_address = ip_address.substring(0, ip_address.lastIndexOf("."));
                ip_address = "http://" + ip_address + ".1:8080/";//当前server地址
            }
        }catch (Exception e) {
            LogUtils.debug(TAG,e.toString());
        }
        return ip_address;
    }

    // 得到本机在局域网中的IP
    public static String getLocalIP(Context context) {
        if (context==null){
            return "";
        }
        WifiManager wifiService = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifiService.getConnectionInfo();
        int ip = wifiinfo.getIpAddress();
        if (ip==0){
            return "";
        }
        //获取到ip地址
        String ip_address = intToIp(ip);
        //LogUtils.debug(TAG,"getLocalIP="+ip_address);
        return ip_address;
    }

    //转换为IP地址格式
    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    /**
     * 判断是否连接了网络
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 获取当前的wifi名称
     * @param context
     * @return
     */
    public static String getCurrentWifiName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo != null ? wifiInfo.getSSID() : "";
        return ssid.replace("\"","").replace("<unknown ssid>","");//.replace("\"","");返回的字符串有双引号，要去掉
    }


}
