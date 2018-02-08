package site.duqian.wchook.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
/**
 * Created by Dusan (duqian) on 2017/5/9 - 15:47.
 * E-mail: duqian2010@gmail.com
 * Description:AdbUtil 执行adb命令
 * remarks:执行命令之前，一定不能加adb shell ，否则总报错：device not found
 */
public class AdbUtil {

    private static final String TAG = AdbUtil.class.getSimpleName();
    private static Process process;
    //input keyevent "KEYCODE_BACK" //4
    //input swipe 100 100 100 100 1000 //在 100 100 位置长按 1000毫秒

    //向下滚动的高度height
    public static  int scrollDown(int right,int bottom,int height) {
        height = Math.abs(bottom-height);//通知栏距离
        if (height<50){
            LogUtils.debug(TAG, "scrollDown ,DisY is short "+height);
        }
        String cmd = "input swipe "+right+" "+bottom+" "+right+" "+height;//滑动100毫秒
        CommandResult commandResult =  ShellUtils.execCommand(cmd, true);
        int result = commandResult.getResult();
        if (0 != result){
            AdbUtil.execShell(cmd);
        }
        LogUtils.debug(TAG, "scrollDown = "+cmd+",result ="+commandResult.getResult());
        return result;
    }

    public static void testShellCmd() {
        String cmd2 ="input swipe 50 350 200 50";
        String cmd ="input keyevent \"KEYCODE_BACK\"";
        excuteCommand(cmd2);
        execShell(cmd);
    }
    public static void excuteCommand(String cmd) {
        initProcess();
        write2process(cmd);
        close();
    }

    private static void write2process(String cmd) {
        OutputStream out = process.getOutputStream();
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.debug(TAG,"excuteCommand="+e.toString());
        }
    }

    private static void initProcess() {
        if (process == null) {
            try {
                process = Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.debug(TAG, "initProcess=" + e.toString());
            }
        }
    }

    public static void killProcess(String packageName) {
        String cmd = "am force-stop " + packageName + " \n";
        excuteCommand(cmd);
    }

    private static void close() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static String  executShell(String cmd){
        BufferedReader reader = null;
        String content = "";
        try {
            //("ps -P|grep bg")执行失败，PC端adb shell ps -P|grep bg执行成功
            //Process process = Runtime.getRuntime().exec("ps -P|grep tv");
            //-P 显示程序调度状态，通常是bg或fg，获取失败返回un和er
            // Process process = Runtime.getRuntime().exec("ps -P");
            //打印进程信息，不过滤任何条件
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer output = new StringBuffer();
            int read;
            char[] buffer = new char[4096];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            content = output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    public static void execShell(String cmd){
        try{
            //权限设置
            Process p = Runtime.getRuntime().exec("sh");  //su为root用户,sh普通用户
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }


}
