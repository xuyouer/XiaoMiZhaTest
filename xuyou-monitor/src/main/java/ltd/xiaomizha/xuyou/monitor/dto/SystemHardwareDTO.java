package ltd.xiaomizha.xuyou.monitor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 系统硬件信息汇总 DTO
 * <p>
 * 封装返回给前端的格式化硬件信息数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemHardwareDTO {

    /**
     * 操作系统信息
     */
    private OsDTO os;

    /**
     * CPU 信息
     */
    private CpuDTO cpu;

    /**
     * 内存信息
     */
    private MemoryDTO memory;

    /**
     * 磁盘信息（多个挂载点）
     */
    private List<DiskDTO> disks;

    /**
     * 网络信息（多个网卡）
     */
    private List<NetworkDTO> networks;

    /**
     * 操作系统 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OsDTO {
        /**
         * 系统名称（如 Windows 11、CentOS 8）
         */
        private String name;

        /**
         * 系统版本
         */
        private String version;

        /**
         * 系统运行时间（秒）
         */
        private Long uptime;
    }

    /**
     * CPU DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CpuDTO {
        /**
         * CPU 型号
         */
        private String model;

        /**
         * 物理核心数
         */
        private Integer physicalCores;

        /**
         * 逻辑核心数
         */
        private Integer logicalCores;

        /**
         * CPU 使用率（%）
         */
        private Double usage;
    }

    /**
     * 内存 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryDTO {
        /**
         * 总内存
         */
        private String total;

        /**
         * 可用内存
         */
        private String available;

        /**
         * 已用内存
         */
        private String used;

        /**
         * 内存使用率（%）
         */
        private Double usage;
    }

    /**
     * 磁盘 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskDTO {
        /**
         * 挂载点（如 C:\、/root）
         */
        private String mountPoint;

        /**
         * 总容量
         */
        private String total;

        /**
         * 已用容量
         */
        private String used;

        /**
         * 可用容量
         */
        private String usable;

        /**
         * 磁盘使用率（%）
         */
        private Double usage;
    }

    /**
     * 网络 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetworkDTO {
        /**
         * 网卡名称（如 eth0、WLAN）
         */
        private String name;

        /**
         * MAC 地址
         */
        private String mac;

        /**
         * 接收字节数
         */
        private String recvBytes;

        /**
         * 发送字节数
         */
        private String sendBytes;
    }
}
