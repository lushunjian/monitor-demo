package lsj.oshi;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

public class Test {

    public static void main(String[] args) {
        // Options: ERROR > WARN > INFO > DEBUG > TRACE

        System.out.println("Initializing System...");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        System.out.println("----------操作系统信息-----------");
        System.out.println("os:"+os);
        Oshi.printComputerSystem(hal.getComputerSystem());

        System.out.println();
        System.out.println("----------硬件处理器信息-----------");
        Oshi.printProcessor(hal.getProcessor());

        System.out.println();
        System.out.println("----------内存信息获取-----------");
        Oshi.printMemory(hal.getMemory());

        System.out.println();
        System.out.println("----------CPU信息获取-----------");
        Oshi.printCpu(hal.getProcessor());

        System.out.println();
        System.out.println("----------进程信息获取-----------");
        Oshi.printProcesses(os, hal.getMemory());

        System.out.println();
        System.out.println("----------CPU温度电压获取-----------");
        Oshi.printSensors(hal.getSensors());

        System.out.println();
        System.out.println("----------系统电量获取-----------");
        Oshi.printPowerSources(hal.getPowerSources());

        System.out.println();
        System.out.println("----------磁盘信息获取-----------");
        Oshi.printDisks(hal.getDiskStores());

        System.out.println();
        System.out.println("----------文件系统信息获取-----------");
        Oshi.printFileSystem(os.getFileSystem());

        System.out.println();
        System.out.println("----------网络接口信息获取-----------");
        Oshi.printNetworkInterfaces(hal.getNetworkIFs());

        System.out.println();
        System.out.println("----------系统参数信息获取-----------");
        Oshi.printNetworkParameters(os.getNetworkParams());

        // hardware: displays
        System.out.println();
        System.out.println("Checking Displays...");
        Oshi.printDisplays(hal.getDisplays());

        // hardware: USB devices
        System.out.println();
        System.out.println("----------USB接口信息获取-----------");
        Oshi.printUsbDevices(hal.getUsbDevices(true));

        System.out.println();
        System.out.println("----------本应用JVM信息获取-----------");
        Oshi.printJVM();
    }
}
