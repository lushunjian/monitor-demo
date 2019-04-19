package lsj.snmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {

    private static String snmpPort;

    public static void main(String[] args){
        //需要监控服务器的Ip地址
        String ip = "192.168.43.134";
        // 加载oid信息
        OidBean oidBean = OidBean.getOidBean();
        snmpPort = oidBean.getPort();

        System.out.println("------系统信息--------");
        getSysData(ip,oidBean);

        System.out.println();
        System.out.println("------cpu信息--------");
        getCpuData(ip,oidBean);

        System.out.println();
        System.out.println("------磁盘信息--------");
        getDiskData(ip,oidBean);

        System.out.println();
        System.out.println("------内存信息--------");
        getMemData(ip,oidBean);

        System.out.println();
        System.out.println("------网卡信息--------");
        getNetData(ip,oidBean);

    }

    //系统基本信息
    private static void getSysData(String ip,OidBean oidBean){
        String sysBaseData =  SnmpUtil.getInfoByOidGet(ip,snmpPort,oidBean.getSysBaseData());
        System.out.println("系统基本信息:"+sysBaseData);

        String machineName =  SnmpUtil.getInfoByOidGet(ip,snmpPort,oidBean.getSysMachineName());
        System.out.println("系统用户名称:"+machineName);
    }


    //CPU信息
    private static void getCpuData(String ip,OidBean oidBean){
        // cpu空闲百分比
        String cpuFreePercent =  SnmpUtil.getInfoByOidGet(ip,snmpPort,oidBean.getCpuFreePercent());
        System.out.println("cpu空闲占比:"+cpuFreePercent+"%");
        // cpu核信息
        Map<String, List<String>> map = SnmpUtil.getInfoByOidWalk(ip,oidBean.getPort(),oidBean.getCpuCoreLoad());
        List<String> loadList = new ArrayList<String>();
        if(map != null){
            for(String str : map.keySet()){
                loadList.addAll(map.get(str));
            }
            //cpu各个核使用占比
            System.out.println("cpu各个核负载占比");
            for(int i=0;i<loadList.size();i++){
                System.out.println("Core"+i+":"+loadList.get(i)+"%");
            }
        }
    }

    //磁盘信息
    private static void getDiskData(String ip,OidBean oidBean){
        // 磁盘挂载路径
        Map<String, List<String>> map1 = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getDiskMountPath());
        String path = getValues(map1);
        System.out.println("磁盘挂载路径:"+path);

        //磁盘总大小 Size
        Map<String, List<String>> map2 = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getDiskTotalSize());
        String size = getValues(map2);
        System.out.println("磁盘总大小:"+size+" kb");

        //磁盘可用大小  Avail
        Map<String, List<String>> map3 = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getDiskAvailSize());
        String avail = getValues(map3);
        System.out.println("磁盘可用大小:"+avail+" kb");

        //磁盘已用大小  Used
        Map<String, List<String>> map4 = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getDiskUsedSize());
        String used = getValues(map4);
        System.out.println("磁盘已用大小:"+used+" kb");

        //磁盘使用百分比
        Map<String, List<String>> map5 = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getDiskUsedPercent());
        String usedPercent = getValues(map5);
        System.out.println("磁盘使用百分比:"+usedPercent+"%");

    }

    //内存信息
    private static void getMemData(String ip,OidBean oidBean){
        List<String> list = new ArrayList<String>();
        //物理内存总大小  Mem total
        list.add(oidBean.getMemoryPhysicsTotal());
        //物理内存已使用大小  Mem used
        list.add(oidBean.getMemoryPhysicsUsed());
        //物理内存剩余大小  Mem free
        list.add(oidBean.getMemoryPhysicsFree());
        // 物理内存 Mem buffers
        list.add(oidBean.getMemoryPhysicsBuffers());
        // 物理内存 Mem cached
        list.add(oidBean.getMemoryPhysicsCached());
        //虚拟内存总大小  Swap total
        list.add(oidBean.getSwapVirtualTotal());
        //虚拟内存可用大小 Swap free
        list.add(oidBean.getSwapVirtualFree());

        Map<String,String>  map =  SnmpUtil.snmpGetList(ip,snmpPort,list);
        System.out.println("物理内存总大小:"+map.get(oidBean.getMemoryPhysicsTotal())+" kb");
        System.out.println("物理内存已使用大小:"+map.get(oidBean.getMemoryPhysicsUsed())+" kb");
        System.out.println("物理内存剩余大小:"+map.get(oidBean.getMemoryPhysicsFree())+" kb");
        System.out.println("物理内存Buffers:"+map.get(oidBean.getMemoryPhysicsBuffers())+" kb");
        System.out.println("物理内存Cached:"+map.get(oidBean.getMemoryPhysicsCached())+" kb");
        System.out.println("虚拟内存总大小:"+map.get(oidBean.getSwapVirtualTotal())+" kb");
        System.out.println("虚拟内存可用大小:"+map.get(oidBean.getSwapVirtualFree())+" kb");
    }

    //网卡信息
    private static void getNetData(String ip,OidBean oidBean){
        // 网络接口信息描述 -- 网卡名称
        Map<String, List<String>> netNameMap = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getNetworkName());
        // 网卡收到的字节数
        Map<String, List<String>> netFlowIn = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getNetworkReceiveByte());
        // 网卡发送的字节数
        Map<String, List<String>> netFlowOut = SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getNetworkSendByte());
        // 网卡mac地址
        Map<String, List<String>> netMacAddress =  SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getNetMacAddress());
        // 网卡当前带宽
        Map<String, List<String>> netBps =  SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getNetBps());
        // 网卡类型
        Map<String, List<String>> netTpe =  SnmpUtil.getInfoByOidWalk(ip,snmpPort,oidBean.getNetType());
        if(netNameMap != null && netFlowIn != null && netFlowOut != null && netMacAddress != null && netBps != null) {
            for (String index : netNameMap.keySet()) {
                String netName = netNameMap.get(index).get(0);
                String mac = netMacAddress.get(index).get(0);
                String type = netTpe.get(index).get(0);
                String bps = netBps.get(index).get(0)+" bps";
                String sendByte = netFlowOut.get(index).get(0)+" byte";
                String receiveByte = netFlowIn.get(index).get(0)+" byte";
                System.out.println("网卡名称:"+netName);
                System.out.println("网卡类型:"+type);
                System.out.println("网卡mac地址:"+mac);
                System.out.println("网卡当前带宽:"+bps);
                System.out.println("发送数据:"+sendByte);
                System.out.println("接收数据:"+receiveByte);
                System.out.println();
            }
        }
    }

    private static String getValues(Map<String, List<String>> map){
        if(map != null && map.size()>0){
            for (String str : map.keySet()) {
                List<String> list = map.get(str);
                if (list != null && list.size() > 0)
                    return list.get(0);
                else
                    return "";
            }
        }
        return "";
    }
}
