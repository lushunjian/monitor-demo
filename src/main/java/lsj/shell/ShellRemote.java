package lsj.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.ConnectionMonitor;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.*;

public class ShellRemote {

    private static final Logger log = LoggerFactory.getLogger(ShellRemote.class);

    /**
     * 登录主机
     * @return
     *      登录成功返回Connection，否则返回null
     */
    public static Connection login(String ip,
                                   String userName,
                                   String userPwd){
        long start = System.currentTimeMillis();
        Connection conn = new Connection(ip);
        conn.addConnectionMonitor(new ConnectionMonitor() {
            @Override
            public void connectionLost(Throwable throwable) {
                // 在socket连接丢失时，会进入此触发器
            }
        });
        try {
            conn.connect();//连接
            boolean flg=conn.authenticateWithPassword(userName, userPwd);//认证
            System.out.println("ip----"+ip+"---获取linux连接时间----"+(System.currentTimeMillis()-start));
        } catch (IOException e) {
            log.error("=========登录失败========="+e.getMessage());
            //如报错Authentication method password not supported by the server at this stage
            //需在服务器中 vi /etc/ssh/sshd_config，将PasswordAuthentication配置项改成yes，重启
            e.printStackTrace();
            return null;
        }
        return conn;
    }

    public static Connection getConnection(String ip,String username,String password) {
        if(ip!=null && !"".equals(ip)) {
            if (username!=null && !"".equals(username) && password!=null && !"".equals(password)) {
                Connection linuxConnect = login(ip,username,password);
                if(linuxConnect == null){
                    //服务器连接失败。请将服务器中 /etc/ssh/sshd_config 文件中的PasswordAuthentication配置项改成yes，并重启服务器
                    log.error("服务器未开通远程连接权限");
                    return null;
                }
                if(!linuxConnect.isAuthenticationComplete()){
                    log.error("服务器账号或密码错误");
                    return null;
                }
                return linuxConnect;
            } else {
                log.error("账号或密码为空");
                return null;
            }
        }else {
            log.error("IP地址为空");
            return null;
        }
    }

    /**
     * 远程执行shll脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     */
    private static String execute(Connection conn,String cmd) throws IOException {
        String result="";
        if(conn !=null){
            Session session= conn.openSession();//打开一个会话
            session.execCommand(cmd);//执行命令
            String DEFAULT_CODE = "UTF-8";
            result=processStdout(session.getStdout(), DEFAULT_CODE);
            //如果为得到标准输出为空，说明脚本执行出错了
            log.info("得到标准输出为空,链接conn:"+conn+",执行的命令："+cmd);
            if("".equals(result)){
                result=processStdout(session.getStderr(), DEFAULT_CODE);
            }
            session.close();
        }
        return result;
    }

    public static void closeConnection(Connection conn){
        if(conn != null){
            conn.close();
        }
    }

    /**
     * 解析脚本执行返回的结果集
     * @param in 输入流对象
     * @param charset 编码
     * @return
     *       以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset){
        InputStream stdout = new StreamGobbler(in);
        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout,charset));
            String line;
            while((line=br.readLine()) != null){
                buffer.append(line).append("\n");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("解析脚本出错："+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String remoteShell(String ip, String username, String password,String cmd) throws IOException {
        Connection connection = login(ip,username,password);
        if(connection != null){
            return execute(connection,cmd);
        }else {
            return "";
        }
    }

    public static String remoteShell(Connection connection,String cmd){
        try {
            return execute(connection,cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String shell(Connection connection,String cmd) throws IOException{
        return execute(connection,cmd);
    }
}
