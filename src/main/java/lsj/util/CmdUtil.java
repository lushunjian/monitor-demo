package lsj.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 执行cmd命令工具类
 *
 */

public class CmdUtil {
    /**
     * 执行一个cmd命令
     * @param cmd cmd命令
     * @return 命令执行结果字符串，如出现异常返回null
     */
    private static String excuteCMDCommand(String cmd){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line ;
            while((line=bufferedReader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            process.waitFor();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String excuteCMDCommand(String[] cmds){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(cmds);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line ;
            while((line=bufferedReader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            process.waitFor();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 执行bat文件，
     * @param file bat文件路径
     * @param isCloseWindow 执行完毕后是否关闭cmd窗口
     * @return bat文件输出log
     */
    public static String excuteBatFile(String file, boolean isCloseWindow){
        String cmdCommand = null;
        if(isCloseWindow)
        {
            cmdCommand = "cmd.exe /c "+file;
        }else
        {
            cmdCommand = "cmd.exe /k "+file;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while((line=bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行bat文件,新开窗口
     * @param file bat文件路径
     * @param isCloseWindow 执行完毕后是否关闭cmd窗口
     * @return bat文件输出log
     */
    public static String excuteBatFileWithNewWindow(String file, boolean isCloseWindow)
    {
        String cmdCommand = null;
        if(isCloseWindow)
        {
            cmdCommand = "cmd.exe /c start"+file;
        }else
        {
            cmdCommand = "cmd.exe /k start"+file;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while((line=bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // windows查端口和pid对应信息命令
        //String cmd = "netstat -aon";

        // linux查端口和pid对应信息命令
        //netstat -nlp|grep -w 80| sed -r 's#.* (.*)/.*#\1#'
        String[] cmd = {"/bin/sh", "-c","netstat -nlpt|grep -w 80| sed -r 's#.* (.*)/.*#\\1#'"};
        String result = CmdUtil.excuteCMDCommand(cmd);
        System.out.println(result);
    }
}
