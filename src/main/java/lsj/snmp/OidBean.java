package lsj.snmp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OidBean {

    private static OidBean oidBean;
    private String port;
    private String sysBaseData;
    private String sysMachineName;
    private String cpuFreePercent;
    private String cpuSysLoadPercent;
    private String cpuUserLoadPercent;
    private String cpuCoreLoad;
    private String diskMountPath;
    private String diskTotalSize;
    private String diskAvailSize;
    private String diskUsedSize;
    private String diskUsedPercent;
    private String memoryPhysicsTotal;
    private String memoryPhysicsUsed;
    private String memoryPhysicsFree;
    private String memoryPhysicsBuffers;
    private String memoryPhysicsCached;
    private String swapVirtualTotal;
    private String swapVirtualFree;
    private String networkName;
    private String networkReceiveByte;
    private String networkSendByte;
    private String processList;
    private String netMacAddress;
    private String netBps;
    private String netType;

    private OidBean(){}

    public static OidBean getOidBean(){
        if(oidBean != null)
            return oidBean;
        oidBean = new OidBean();
        InputStream inputStream = OidBean.class.getClassLoader().getResourceAsStream("snmp/oid.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
            oidBean.setPort(p.getProperty("port"));
            oidBean.setSysBaseData(p.getProperty("sysBaseData"));
            oidBean.setSysMachineName(p.getProperty("sysMachineName"));
            oidBean.setCpuFreePercent(p.getProperty("cpuFreePercent"));
            oidBean.setCpuSysLoadPercent(p.getProperty("cpuSysLoadPercent"));
            oidBean.setCpuUserLoadPercent(p.getProperty("cpuUserLoadPercent"));
            oidBean.setCpuCoreLoad(p.getProperty("cpuCoreLoad"));
            oidBean.setDiskMountPath(p.getProperty("diskMountPath"));
            oidBean.setDiskTotalSize(p.getProperty("diskTotalSize"));
            oidBean.setDiskAvailSize(p.getProperty("diskAvailSize"));
            oidBean.setDiskUsedSize(p.getProperty("diskUsedSize"));
            oidBean.setDiskUsedPercent(p.getProperty("diskUsedPercent"));
            oidBean.setMemoryPhysicsTotal(p.getProperty("memoryPhysicsTotal"));
            oidBean.setMemoryPhysicsUsed(p.getProperty("memoryPhysicsUsed"));
            oidBean.setMemoryPhysicsFree(p.getProperty("memoryPhysicsFree"));
            oidBean.setMemoryPhysicsBuffers(p.getProperty("memoryPhysicsBuffers"));
            oidBean.setMemoryPhysicsCached(p.getProperty("memoryPhysicsCached"));
            oidBean.setSwapVirtualTotal(p.getProperty("swapVirtualTotal"));
            oidBean.setSwapVirtualFree(p.getProperty("swapVirtualFree"));
            oidBean.setNetworkName(p.getProperty("networkName"));
            oidBean.setNetworkReceiveByte(p.getProperty("networkReceiveByte"));
            oidBean.setNetworkSendByte(p.getProperty("networkSendByte"));
            oidBean.setProcessList(p.getProperty("processList"));
            oidBean.setNetMacAddress(p.getProperty("netMacAddress"));
            oidBean.setNetBps(p.getProperty("netBps"));
            oidBean.setNetType(p.getProperty("netType"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return oidBean;
    }

    public String getCpuFreePercent() {
        return cpuFreePercent;
    }

    public void setCpuFreePercent(String cpuFreePercent) {
        this.cpuFreePercent = cpuFreePercent;
    }

    public String getCpuCoreLoad() {
        return cpuCoreLoad;
    }

    public void setCpuCoreLoad(String cpuCoreLoad) {
        this.cpuCoreLoad = cpuCoreLoad;
    }

    public String getDiskMountPath() {
        return diskMountPath;
    }

    public void setDiskMountPath(String diskMountPath) {
        this.diskMountPath = diskMountPath;
    }

    public String getDiskTotalSize() {
        return diskTotalSize;
    }

    public void setDiskTotalSize(String diskTotalSize) {
        this.diskTotalSize = diskTotalSize;
    }

    public String getDiskAvailSize() {
        return diskAvailSize;
    }

    public void setDiskAvailSize(String diskAvailSize) {
        this.diskAvailSize = diskAvailSize;
    }

    public String getDiskUsedSize() {
        return diskUsedSize;
    }

    public void setDiskUsedSize(String diskUsedSize) {
        this.diskUsedSize = diskUsedSize;
    }

    public String getDiskUsedPercent() {
        return diskUsedPercent;
    }

    public void setDiskUsedPercent(String diskUsedPercent) {
        this.diskUsedPercent = diskUsedPercent;
    }

    public String getMemoryPhysicsTotal() {
        return memoryPhysicsTotal;
    }

    public void setMemoryPhysicsTotal(String memoryPhysicsTotal) {
        this.memoryPhysicsTotal = memoryPhysicsTotal;
    }

    public String getMemoryPhysicsUsed() {
        return memoryPhysicsUsed;
    }

    public void setMemoryPhysicsUsed(String memoryPhysicsUsed) {
        this.memoryPhysicsUsed = memoryPhysicsUsed;
    }

    public String getMemoryPhysicsFree() {
        return memoryPhysicsFree;
    }

    public void setMemoryPhysicsFree(String memoryPhysicsFree) {
        this.memoryPhysicsFree = memoryPhysicsFree;
    }

    public String getMemoryPhysicsBuffers() {
        return memoryPhysicsBuffers;
    }

    public void setMemoryPhysicsBuffers(String memoryPhysicsBuffers) {
        this.memoryPhysicsBuffers = memoryPhysicsBuffers;
    }

    public String getMemoryPhysicsCached() {
        return memoryPhysicsCached;
    }

    public void setMemoryPhysicsCached(String memoryPhysicsCached) {
        this.memoryPhysicsCached = memoryPhysicsCached;
    }

    public String getSwapVirtualTotal() {
        return swapVirtualTotal;
    }

    public void setSwapVirtualTotal(String swapVirtualTotal) {
        this.swapVirtualTotal = swapVirtualTotal;
    }

    public String getSwapVirtualFree() {
        return swapVirtualFree;
    }

    public void setSwapVirtualFree(String swapVirtualFree) {
        this.swapVirtualFree = swapVirtualFree;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkReceiveByte() {
        return networkReceiveByte;
    }

    public void setNetworkReceiveByte(String networkReceiveByte) {
        this.networkReceiveByte = networkReceiveByte;
    }

    public String getNetworkSendByte() {
        return networkSendByte;
    }

    public void setNetworkSendByte(String networkSendByte) {
        this.networkSendByte = networkSendByte;
    }

    public String getProcessList() {
        return processList;
    }

    public void setProcessList(String processList) {
        this.processList = processList;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSysBaseData() {
        return sysBaseData;
    }

    public void setSysBaseData(String sysBaseData) {
        this.sysBaseData = sysBaseData;
    }

    public String getSysMachineName() {
        return sysMachineName;
    }

    public void setSysMachineName(String sysMachineName) {
        this.sysMachineName = sysMachineName;
    }

    public String getCpuSysLoadPercent() {
        return cpuSysLoadPercent;
    }

    public void setCpuSysLoadPercent(String cpuSysLoadPercent) {
        this.cpuSysLoadPercent = cpuSysLoadPercent;
    }

    public String getCpuUserLoadPercent() {
        return cpuUserLoadPercent;
    }

    public void setCpuUserLoadPercent(String cpuUserLoadPercent) {
        this.cpuUserLoadPercent = cpuUserLoadPercent;
    }

    public String getNetMacAddress() {
        return netMacAddress;
    }

    public void setNetMacAddress(String netMacAddress) {
        this.netMacAddress = netMacAddress;
    }

    public String getNetBps() {
        return netBps;
    }

    public void setNetBps(String netBps) {
        this.netBps = netBps;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }
}
