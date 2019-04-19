package lsj.other;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;

public class Other {

    private static String osName = System.getProperty("os.name");

    public static void main(String[] args){
        //System.out.println("-----内存使用率---"+cpuUsage());
/*        Properties props=System.getProperties(); //获得系统属性集
        String osName = props.getProperty("os.name");
        System.out.println("----操作系统名称-----"+osName);*/
        getDiskInfo();
/*        double cpu = getCpuUsedPercent();
        System.out.println("----CPU---"+cpu);*/
    }

    public static void getDiskInfo()
    {
        File[] disks = File.listRoots();
        for(File file : disks){
            System.out.print(file.getPath() + "    ");
            System.out.print("可用= " + file.getFreeSpace() / 1024 / 1024/1024 + "G" + "    ");// 空闲空间
            ///System.out.print("可用容量 = " + file.getUsableSpace() / 1024 / 1024/1024 + "G" + "    ");// 可用空间
            System.out.print("总容量 = " + file.getTotalSpace() / 1024 / 1024/1024 + "G" + "    ");// 总空间
            System.out.println();
        }
    }

    public static float cpuUsage() {
        try {
            Map<?, ?> map1 = Other.cpuinfo();
            Thread.sleep(3 * 1000);
            Map<?, ?> map2 = Other.cpuinfo();

            long user1 = Long.parseLong(map1.get("user").toString());
            long nice1 = Long.parseLong(map1.get("nice").toString());
            long system1 = Long.parseLong(map1.get("system").toString());
            long idle1 = Long.parseLong(map1.get("idle").toString());

            long user2 = Long.parseLong(map2.get("user").toString());
            long nice2 = Long.parseLong(map2.get("nice").toString());
            long system2 = Long.parseLong(map2.get("system").toString());
            long idle2 = Long.parseLong(map2.get("idle").toString());

            long total1 = user1 + system1 + nice1;
            long total2 = user2 + system2 + nice2;
            float total = total2 - total1;

            long totalIdle1 = user1 + nice1 + system1 + idle1;
            long totalIdle2 = user2 + nice2 + system2 + idle2;
            float totalidle = totalIdle2 - totalIdle1;

            float cpusage = (total / totalidle) * 100;
            return cpusage;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 功能：CPU使用信息
     * */
    public static Map<?, ?> cpuinfo() {
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("cpu")) {
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    List<String> temp = new ArrayList<String>();
                    while (tokenizer.hasMoreElements()) {
                        String value = tokenizer.nextToken();
                        temp.add(value);
                    }
                    map.put("user", temp.get(1));
                    map.put("nice", temp.get(2));
                    map.put("system", temp.get(3));
                    map.put("idle", temp.get(4));
                    map.put("iowait", temp.get(5));
                    map.put("irq", temp.get(6));
                    map.put("softirq", temp.get(7));
                    map.put("stealstolen", temp.get(8));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return map;
    }

    public static double getCpuUsedPercent(){
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        double cpu = osMxBean.getSystemLoadAverage();

        return cpu;
    }
}
