package ltd.xiaomizha.xuyou.monitor.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.utils.monitor.SystemInfoUtil;
import ltd.xiaomizha.xuyou.monitor.dto.SystemHardwareDTO;
import ltd.xiaomizha.xuyou.monitor.service.SystemHardwareService;
import org.springframework.stereotype.Service;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统硬件信息服务实现类
 */
@Slf4j
@Service
public class SystemHardwareServiceImpl implements SystemHardwareService {

    /**
     * 获取完整的系统硬件信息
     *
     * @return 系统硬件信息汇总 DTO
     */
    @Override
    public SystemHardwareDTO getSystemHardwareInfo() {
        SystemHardwareDTO dto = new SystemHardwareDTO();

        // 1. 操作系统信息
        dto.setOs(buildOsDTO());

        // 2. CPU 信息（注意：CPU 使用率需要间隔采样）
        dto.setCpu(buildCpuDTO());

        // 3. 内存信息
        dto.setMemory(buildMemoryDTO());

        // 4. 磁盘信息
        dto.setDisks(buildDiskDTOList());

        // 5. 网络信息
        dto.setNetworks(buildNetworkDTOList());

        log.debug("系统硬件信息采集完成");
        return dto;
    }

    /**
     * 构建操作系统 DTO
     *
     * @return 操作系统信息 DTO
     */
    @Override
    public SystemHardwareDTO.OsDTO buildOsDTO() {
        try {
            var os = SystemInfoUtil.getOsInfo();
            SystemHardwareDTO.OsDTO osDTO = new SystemHardwareDTO.OsDTO();

            osDTO.setName(os.toString());
            osDTO.setVersion(os.getVersionInfo().getVersion());
            osDTO.setUptime(SystemInfoUtil.getSystemUptime()); // 单位：秒

            return osDTO;
        } catch (Exception e) {
            log.error("构建操作系统信息失败", e);
            return new SystemHardwareDTO.OsDTO("Unknown", "0", 0L);
        }
    }

    /**
     * 构建 CPU DTO
     * <p>
     * 注意：CPU 使用率计算需要首次采样，间隔 100ms 后再次采样，
     * 否则返回 0 或 -1
     *
     * @return CPU 信息 DTO
     */
    @Override
    public SystemHardwareDTO.CpuDTO buildCpuDTO() {
        try {
            var cpu = SystemInfoUtil.getCpuInfo();
            SystemHardwareDTO.CpuDTO cpuDTO = new SystemHardwareDTO.CpuDTO();

            // CPU 型号
            cpuDTO.setModel(SystemInfoUtil.getCpuModel());

            // 核心数
            cpuDTO.setPhysicalCores(SystemInfoUtil.getPhysicalProcessorCount());
            cpuDTO.setLogicalCores(SystemInfoUtil.getLogicalProcessorCount());

            // CPU 使用率（间隔 100ms 采样）
            double usage = SystemInfoUtil.getCpuUsage(100);
            cpuDTO.setUsage(usage);

            log.debug("CPU 信息采集完成: model={}, physicalCores={}, logicalCores={}, usage={}%",
                    cpuDTO.getModel(), cpuDTO.getPhysicalCores(), cpuDTO.getLogicalCores(), cpuDTO.getUsage());

            return cpuDTO;
        } catch (Exception e) {
            log.error("构建 CPU 信息失败", e);
            return new SystemHardwareDTO.CpuDTO("Unknown", 0, 0, 0.0);
        }
    }

    /**
     * 构建内存 DTO
     *
     * @return 内存信息 DTO
     */
    @Override
    public SystemHardwareDTO.MemoryDTO buildMemoryDTO() {
        try {
            GlobalMemory memory = SystemInfoUtil.getMemoryInfo();
            SystemHardwareDTO.MemoryDTO memoryDTO = new SystemHardwareDTO.MemoryDTO();

            long total = memory.getTotal();
            long available = memory.getAvailable();
            long used = total - available;

            memoryDTO.setTotal(SystemInfoUtil.formatBytes(total));
            memoryDTO.setAvailable(SystemInfoUtil.formatBytes(available));
            memoryDTO.setUsed(SystemInfoUtil.formatBytes(used));

            // 内存使用率（百分比）
            double usage = (double) used / total * 100;
            memoryDTO.setUsage(Math.round(usage * 100) / 100.0); // 保留2位小数

            log.debug("内存信息采集完成: total={}, available={}, used={}, usage={}%",
                    memoryDTO.getTotal(), memoryDTO.getAvailable(), memoryDTO.getUsed(), memoryDTO.getUsage());

            return memoryDTO;
        } catch (Exception e) {
            log.error("构建内存信息失败", e);
            return new SystemHardwareDTO.MemoryDTO("0 B", "0 B", "0 B", 0.0);
        }
    }

    /**
     * 构建磁盘 DTO 列表（所有挂载点）
     * <p>
     * 过滤掉容量为 0 的虚拟磁盘，避免无用数据
     *
     * @return 磁盘信息列表
     */
    @Override
    public List<SystemHardwareDTO.DiskDTO> buildDiskDTOList() {
        try {
            List<OSFileStore> diskStores = SystemInfoUtil.getDiskInfo();

            return diskStores.stream().map(disk -> {
                SystemHardwareDTO.DiskDTO diskDTO = new SystemHardwareDTO.DiskDTO();

                diskDTO.setMountPoint(disk.getMount());

                // 过滤掉容量为 0 的虚拟磁盘（避免无用数据）
                long total = disk.getTotalSpace();
                if (total == 0) {
                    return null; // 过滤掉
                }

                long usableSpace = disk.getUsableSpace();
                long used = total - usableSpace;

                diskDTO.setTotal(SystemInfoUtil.formatBytes(total));
                diskDTO.setUsed(SystemInfoUtil.formatBytes(used));
                diskDTO.setUsable(SystemInfoUtil.formatBytes(usableSpace));

                // 磁盘使用率（百分比）
                double usage = (double) used / total * 100;
                diskDTO.setUsage(Math.round(usage * 100) / 100.0);

                return diskDTO;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("构建磁盘信息失败", e);
            return List.of();
        }
    }

    /**
     * 构建 网络 DTO 列表（所有网卡）
     *
     * @return 网络接口信息列表
     */
    @Override
    public List<SystemHardwareDTO.NetworkDTO> buildNetworkDTOList() {
        try {
            List<NetworkIF> networkIFs = SystemInfoUtil.getNetworkInfo();

            return networkIFs.stream().map(net -> {
                SystemHardwareDTO.NetworkDTO networkDTO = new SystemHardwareDTO.NetworkDTO();

                networkDTO.setName(net.getName());
                networkDTO.setMac(net.getMacaddr());
                networkDTO.setRecvBytes(SystemInfoUtil.formatBytes(net.getBytesRecv()));
                networkDTO.setSendBytes(SystemInfoUtil.formatBytes(net.getBytesSent()));

                return networkDTO;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("构建网络信息失败", e);
            return List.of();
        }
    }
}
