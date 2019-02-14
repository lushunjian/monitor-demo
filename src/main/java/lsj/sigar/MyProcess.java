package lsj.sigar;

import org.hyperic.sigar.*;

public class MyProcess {

    private static final String unknown = "???";
    private String pid;
    private String port;
    private String memUsed;
    private String memSize;
    private String memUsedPercent;
    private String cpuUsedPercent;
    private String name;

    @Override
    public String toString() {
        return "{" +
                "pid='" + pid + '\'' +
                ", port='" + port + '\'' +
                ", memUsed='" + memUsed + '\'' +
                ", memSize='" + memSize + '\'' +
                ", memUsedPercent='" + memUsedPercent + '\'' +
                ", cpuUsedPercent='" + cpuUsedPercent + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public MyProcess(){}

    public MyProcess(Sigar sigar, long pid){
        this.pid=pid+"";
        this.init(sigar,pid);
    }

    private void init(Sigar sigar, long pid){
        String memSize;
        String memUsed;
        try {
            ProcMem mem = sigar.getProcMem(pid);
            // mem.getSize()返回单位是字节--包括物理内存和虚拟内存的总和
            memSize = Sigar.formatSize(mem.getSize());
            // 进程使用的物理内存
            memUsed = Sigar.formatSize(mem.getResident());
            //查找网络端口，返回进程号PID

            //sigar.getProcPort(NetFlags.CONN_UDP, port);
        }catch (Exception e){
            memSize="1";
            memUsed="0";
        }
        //this.memUsedPercent = (Long.parseLong(memUsed) / Long.parseLong(memSize)) + "";
        this.memUsed = memUsed;
        this.memSize = memSize;

        try {
            //ProcCpu pCpu = new ProcCpu();

            ProcCpu pCpu  = sigar.getProcCpu(pid);
            pCpu.gather(sigar, pid);
            Thread.sleep(3000);
            pCpu.gather(sigar, pid);
            this.cpuUsedPercent=pCpu.getPercent()+"";
        }catch (SigarPermissionDeniedException e){
            e.printStackTrace();
            this.cpuUsedPercent = unknown;
        } catch (SigarException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ProcState ps =  sigar.getProcState(pid);
            this.name=ps.getName();
        } catch (SigarException e) {
            e.printStackTrace();
        }

    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMemUsedPercent() {
        return memUsedPercent;
    }

    public void setMemUsedPercent(String memUsedPercent) {
        this.memUsedPercent = memUsedPercent;
    }

    public String getCpuUsedPercent() {
        return cpuUsedPercent;
    }

    public void setCpuUsedPercent(String cpuUsedPercent) {
        this.cpuUsedPercent = cpuUsedPercent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(String memUsed) {
        this.memUsed = memUsed;
    }

    public String getMemSize() {
        return memSize;
    }

    public void setMemSize(String memSize) {
        this.memSize = memSize;
    }
}
