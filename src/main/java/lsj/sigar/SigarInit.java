package lsj.sigar;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Sigar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SigarInit {

    private static Logger logger = Logger.getLogger(SigarInit.class);

    private static final String SIGAR_FILE = "sigar";

    private static final String WINDOWS_AMD_DLL = "sigar-amd64-winnt.dll";
    private static final String WINDOWS_X86_DLL = "sigar-x86-winnt.dll";

    private static final String LINUX_AMD_SO = "libsigar-amd64-linux.so";
    private static final String LINUX_X86_SO = "libsigar-x86-linux.so";

    private static Sigar sigar;

    public synchronized static Sigar getSigar(){
        if(sigar == null){
            int maxRetryTime = 3;
            int time = 0;
            // 如果创建失败，重试3次
            do {
                time++;
                sigar = init();
            } while (null == sigar && time < maxRetryTime);
        }
        return sigar;
    }

    private static Sigar init(){
        //获取系统临时文件路径
        String nativeTempDir = System.getProperty("java.io.tmpdir");
        //获取加载库时搜索的路径列表
        String path = System.getProperty("java.library.path");
        //定义sigar动态库保存路径
        String configPath = nativeTempDir+File.separator+SIGAR_FILE;
        File file = new File(configPath);
        try {
            //若此sigar目录不存在，则创建
            if (!file.exists()) {
                boolean flag = file.mkdir();
                if(!flag) {
                    logger.error("-----sigar目录创建失败-----"+configPath);
                    return null;
                }
            }else {
                //目前存在则删除下面所有的临时文件
                deleteFile(configPath);
            }
            String os = System.getProperty("os.name").toLowerCase();
            //如果是windows系统
            if(os.contains("win")){
                //sigar-amd64-winnt.dll 本地文件路径
                String amdDll = nativeTempDir + File.separator + SIGAR_FILE + File.separator + WINDOWS_AMD_DLL;
                //读取jar包中resources下的文件
                InputStream admIn = SigarInit.class.getResourceAsStream("/sigar/" + WINDOWS_AMD_DLL);

                //sigar-x86-winnt.dll 本地文件路径
                String x86Dll = nativeTempDir + File.separator + SIGAR_FILE + File.separator + WINDOWS_X86_DLL;
                //读取jar包中resources下的文件
                InputStream x86In = SigarInit.class.getResourceAsStream("/sigar/" + WINDOWS_X86_DLL);

                //创建sigar-x86-winnt.dll和sigar-amd64-winnt.dll临时文件
                File amdFile = createFile(admIn,amdDll);
                File x86File = createFile(x86In,x86Dll);
                // 如果都创建成功
                if(amdFile != null && x86File != null){
                    //获取文件所在文件夹的绝对路径，追加到java.library.path中
                    path += ";" + amdFile.getParentFile().getCanonicalPath();
                    System.setProperty("java.library.path", path);
                }else {
                    return null;
                }
            } else {   //linux系统
                //sigar-amd64-winnt.dll 本地文件路径
                String amdSo = nativeTempDir + File.separator + SIGAR_FILE + File.separator + LINUX_AMD_SO;
                //读取jar包中resources下的文件
                InputStream admIn = SigarInit.class.getResourceAsStream("/sigar/" + LINUX_AMD_SO);

                //sigar-x86-winnt.dll 本地文件路径
                String x86So = nativeTempDir + File.separator + SIGAR_FILE + File.separator + LINUX_X86_SO;
                //读取jar包中resources下的文件
                InputStream x86In = SigarInit.class.getResourceAsStream("/sigar/" + LINUX_X86_SO);

                //创建sigar-x86-winnt.dll和sigar-amd64-winnt.dll临时文件
                File amdFile = createFile(admIn,amdSo);
                File x86File = createFile(x86In,x86So);
                // 如果文件都创建成功
                if(amdFile != null && x86File != null){
                    //获取文件所在文件夹的绝对路径，追加到java.library.path中
                    path += ":" + amdFile.getParentFile().getCanonicalPath();
                    System.setProperty("java.library.path", path);
                }else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return new Sigar();
    }

    private static File createFile(InputStream jarInputStream,String tempFilePath){
        File file = new File(tempFilePath);
        BufferedInputStream reader;
        FileOutputStream writer;
        try {
            if (!file.exists()) {
                boolean flag = file.createNewFile();
                if(flag){
                    reader = new BufferedInputStream(jarInputStream);
                    writer = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    while (reader.read(buffer) > 0) {
                        writer.write(buffer);
                        buffer = new byte[1024];
                    }
                    jarInputStream.close();
                    writer.close();
                    reader.close();
                }else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return file;
    }

    //删除文件夹下的所有文件
    private static void deleteFile(String path){
        boolean flag = false;
        int maxRetryTime = 3;
        int time = 0;
        do {
            time++;
            File dir = new File(path);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        flag = file.delete();
                    }
                }
            }
        }while (!flag && time < maxRetryTime);
    }


    // 如果创建失败，重试3次
}
