package lsj.shell;

import ch.ethz.ssh2.Connection;

public class Test {

    public static void main(String[] args){
        String ip = "192.168.43.134";
        String username = "root";
        String password = "123456";
        Connection connection = ShellRemote.getConnection(ip,username,password);
        String str = ShellRemote.remoteShell(connection, "netstat -nlp");
        ShellRemote.closeConnection(connection);
        System.out.println(str);
    }
}
