package ltd.xiaomizha.xuyou.common.utils.monitor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

/**
 * 系统硬件信息采集工具类
 * <p>
 * 基于OSHI开源库实现跨平台硬件信息采集
 */
@Slf4j
@Component
public class SystemInfoUtil {

    /**
     * 单例模式, 避免频繁创建 SystemInfo 对象
     */
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

    /**
     * 操作系统信息对象
     */
    private static OperatingSystem operatingSystem;

    /**
     * CPU 信息对象
     */
    private static CentralProcessor processor;

    /**
     * 内存信息对象
     */
    private static GlobalMemory memory;

    /**
     * 磁盘存储列表
     */
    private static List<HWDiskStore> diskStores;

    /**
     * 网络接口列表
     */
    private static List<NetworkIF> networkIFs;

    /**
     * 项目启动时初始化核心对象
     */
    @PostConstruct
    public void init() {
        try {
            operatingSystem = SYSTEM_INFO.getOperatingSystem();
            processor = SYSTEM_INFO.getHardware().getProcessor();
            memory = SYSTEM_INFO.getHardware().getMemory();
            diskStores = SYSTEM_INFO.getHardware().getDiskStores();
            networkIFs = SYSTEM_INFO.getHardware().getNetworkIFs();
            log.info("系统硬件信息采集工具初始化成功");
            log.debug("操作系统: {}", operatingSystem);
            log.debug("CPU型号: {}", processor.getProcessorIdentifier().getName());
            log.debug("物理核心: {}, 逻辑核心: {}", processor.getPhysicalProcessorCount(), processor.getLogicalProcessorCount());
            log.debug("磁盘数量: {}", diskStores.size());
            log.debug("网络接口数量: {}", networkIFs.size());
        } catch (Exception e) {
            log.error("系统硬件信息采集工具初始化失败", e);
        }
    }

    /**
     * 服务关闭时释放资源
     */
    @PreDestroy
    public void destroy() {
        log.info("系统硬件信息采集工具资源已释放");
    }

    /**
     * 获取操作系统信息
     *
     * @return OperatingSystem 操作系统信息对象
     */
    public static OperatingSystem getOsInfo() {
        return operatingSystem;
    }

    /**
     * 获取 CPU 信息对象
     *
     * @return CentralProcessor CPU 信息对象
     */
    public static CentralProcessor getCpuInfo() {
        return processor;
    }

    /**
     * 获取内存信息对象
     *
     * @return GlobalMemory 内存信息对象
     */
    public static GlobalMemory getMemoryInfo() {
        return memory;
    }

    /**
     * 获取磁盘信息 (所有挂载点)
     *
     * @return 磁盘文件存储列表
     */
    public static List<OSFileStore> getDiskInfo() {
        return operatingSystem.getFileSystem().getFileStores();
    }

    /**
     * 获取磁盘存储列表
     *
     * @return 磁盘存储列表
     */
    public static List<HWDiskStore> getDiskStores() {
        return diskStores;
    }

    /**
     * 获取网络接口信息
     *
     * @return 网络接口列表
     */
    public static List<NetworkIF> getNetworkInfo() {
        // return SYSTEM_INFO.getHardware().getNetworkIFs();
        return networkIFs;
    }

    /**
     * 格式化字节数 (B → KB → MB → GB → TB)
     *
     * @param bytes 字节数
     * @return 格式化后的字符串 (如 "4.00 GiB")
     */
    public static String formatBytes(long bytes) {
        return FormatUtil.formatBytesDecimal(bytes);
    }

    /**
     * 获取系统运行时间 (秒)
     *
     * @return 运行时间 (秒)
     */
    public static long getSystemUptime() {
        return operatingSystem.getSystemUptime();
    }

    /**
     * 获取 CPU 使用率 (需要间隔采样)
     * <p>
     * 重要：CPU 使用率必须间隔采样才能获取准确值,
     * 否则会返回 0 或 -1
     *
     * @param waitMs 采样间隔时间 (毫秒), 建议 100ms
     * @return CPU 使用率百分比 (0-100)
     */
    public static double getCpuUsage(int waitMs) {
        try {
            // 首次采样
            long[] prevTicks = processor.getSystemCpuLoadTicks();

            // 等待指定时间
            Thread.sleep(waitMs);

            // 第二次采样并计算使用率
            double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

            // 保留2位小数
            return Math.round(cpuUsage * 100.0) / 100.0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("获取 CPU 使用率被中断");
            return 0.0;
        } catch (Exception e) {
            log.error("获取 CPU 使用率失败", e);
            return 0.0;
        }
    }

    /**
     * 获取物理处理器数量
     *
     * @return 物理处理器数量
     */
    public static int getPhysicalProcessorCount() {
        return processor.getPhysicalProcessorCount();
    }

    /**
     * 获取逻辑处理器数量
     *
     * @return 逻辑处理器数量 (包含超线程)
     */
    public static int getLogicalProcessorCount() {
        return processor.getLogicalProcessorCount();
    }

    /**
     * 获取 CPU 型号/名称
     *
     * @return CPU 型号字符串
     */
    public static String getCpuModel() {
        return processor.getProcessorIdentifier().getName().trim();
    }

    /**
     * 获取 CPU 序列号
     *
     * @return CPU 序列号字符串
     */
    public static String getCpuSerial() {
        try {
            String serial = processor.getProcessorIdentifier().getProcessorID();
            if (serial != null && !serial.isEmpty() && !serial.equals("unknown")) {
                return serial.trim();
            }
        } catch (Exception e) {
            log.debug("无法通过OSHI获取CPU序列号: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 获取总内存大小 (字节)
     *
     * @return 总内存字节数
     */
    public static long getTotalMemory() {
        return memory.getTotal();
    }

    /**
     * 获取可用内存大小 (字节)
     *
     * @return 可用内存字节数
     */
    public static long getAvailableMemory() {
        return memory.getAvailable();
    }

    /**
     * 获取已使用内存大小 (字节)
     *
     * @return 已使用内存字节数
     */
    public static long getUsedMemory() {
        return memory.getTotal() - memory.getAvailable();
    }

    /**
     * 获取主硬盘序列号 (用于硬件绑定)
     * <p>
     * 返回第一个非空序列号的硬盘, 通常为主硬盘
     *
     * @return 硬盘序列号字符串
     */
    public static String getDiskSerial() {
        try {
            for (HWDiskStore disk : diskStores) {
                String serial = disk.getSerial();
                if (serial != null && !serial.isEmpty() && !serial.equals("unknown")) {
                    return serial.trim();
                }
            }
        } catch (Exception e) {
            log.error("获取硬盘序列号失败", e);
        }
        return "unknown";
    }

    /**
     * 获取所有硬盘序列号 (拼接)
     *
     * @return 所有硬盘序列号拼接字符串
     */
    public static String getAllDiskSerials() {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < diskStores.size(); i++) {
                HWDiskStore disk = diskStores.get(i);
                String serial = disk.getSerial();
                if (i > 0) {
                    sb.append("|");
                }
                sb.append(serial != null ? serial : "unknown");
            }
        } catch (Exception e) {
            log.error("获取所有硬盘序列号失败", e);
        }
        return sb.toString();
    }

    /**
     * 获取主硬盘模型/名称
     *
     * @return 硬盘模型字符串
     */
    public static String getDiskModel() {
        try {
            if (!diskStores.isEmpty()) {
                return diskStores.getFirst().getModel();
            }
        } catch (Exception e) {
            log.error("获取硬盘模型失败", e);
        }
        return "unknown";
    }

    /**
     * 获取第一个可用网卡的MAC地址
     * <p>
     * 优先返回物理网卡, 排除虚拟网卡、回环网卡,
     * 如果所有网卡都不可用则返回主机名MD5
     *
     * @return MAC地址字符串, 格式: XX:XX:XX:XX:XX:XX 或无分隔符
     */
    public static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                // 跳过虚拟网卡、回环网卡、未启用的网卡
                if (ni.isLoopback() || ni.isVirtual() || ni.isPointToPoint() || !ni.isUp()) {
                    continue;
                }
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    // 转换为 XX:XX:XX:XX:XX:XX 格式
                    StringBuilder macStr = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        macStr.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                    }
                    return macStr.toString();
                }
            }
        } catch (Exception e) {
            log.warn("无法获取MAC地址: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 获取无分隔符的MAC地址
     *
     * @return MAC地址字符串 (格式: XXXXXXXXXXXX)
     */
    public static String getMacAddressPlain() {
        return getMacAddress().replaceAll(":", "").toUpperCase();
    }

    /**
     * 获取所有网卡的MAC地址 (拼接)
     *
     * @return 所有MAC地址拼接字符串
     */
    public static String getAllMacAddresses() {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean first = true;
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                    continue;
                }
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    if (!first) {
                        sb.append("|");
                    }
                    first = false;
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取所有MAC地址失败", e);
        }
        return !sb.isEmpty() ? sb.toString() : "unknown";
    }

    /**
     * 获取完整硬件指纹信息
     * <p>
     * 包含: MAC地址 + CPU序列号 + CPU型号 + 内存信息 + 硬盘序列号
     * <p>
     * 格式: MAC:xxx|CPU_SERIAL:xxx|CPU_INFO:xxx|MEMORY:xxx|DISK:xxx
     *
     * @return 硬件指纹字符串
     */
    public static String getHardwareFingerprint() {
        StringBuilder fingerprint = new StringBuilder();

        // MAC地址
        String macAddress = getMacAddressPlain();
        fingerprint.append("MAC:").append(macAddress).append("|");

        // CPU序列号
        String cpuSerial = getCpuSerial();
        fingerprint.append("CPU_SERIAL:").append(cpuSerial).append("|");

        // CPU型号
        String cpuModel = getCpuModel();
        if (cpuModel.length() > 50) {
            cpuModel = cpuModel.substring(0, 50);
        }
        fingerprint.append("CPU_INFO:").append(cpuModel).append("|");

        // 内存信息
        long totalMemory = getTotalMemory();
        fingerprint.append("MEMORY:").append(totalMemory).append("|");

        // 硬盘序列号
        String diskSerial = getDiskSerial();
        fingerprint.append("DISK:").append(diskSerial);

        String result = fingerprint.toString();
        log.debug("生成硬件指纹: {}", result);
        return result;
    }

    /**
     * 获取简化的硬件指纹
     * <p>
     * 包含: MAC + CPU序列号 + 硬盘序列号
     *
     * @return 简化硬件指纹字符串
     */
    public static String getSimpleHardwareFingerprint() {
        StringBuilder fp = new StringBuilder();
        fp.append(getMacAddressPlain()).append("|");
        fp.append(getCpuSerial()).append("|");
        fp.append(getDiskSerial());
        return fp.toString();
    }

    /**
     * 获取操作系统名称
     *
     * @return 操作系统名称
     */
    public static String getOsName() {
        return operatingSystem.toString();
    }

    /**
     * 获取操作系统版本
     *
     * @return 版本字符串
     */
    public static String getOsVersion() {
        return operatingSystem.getVersionInfo().getVersion();
    }

    /**
     * 获取主机名
     *
     * @return 主机名字符串
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-host";
        }
    }

}
