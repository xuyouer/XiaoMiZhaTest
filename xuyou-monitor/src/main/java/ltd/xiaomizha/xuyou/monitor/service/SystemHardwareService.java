package ltd.xiaomizha.xuyou.monitor.service;

import ltd.xiaomizha.xuyou.monitor.dto.SystemHardwareDTO;

import java.util.List;

/**
 * 系统硬件信息服务
 */
public interface SystemHardwareService {

    /**
     * 获取完整的系统硬件信息
     *
     * @return 系统硬件信息汇总 DTO
     */
    SystemHardwareDTO getSystemHardwareInfo();

    /**
     * 构建操作系统 DTO
     *
     * @return 操作系统信息 DTO
     */
    SystemHardwareDTO.OsDTO buildOsDTO();

    /**
     * 构建 CPU DTO
     * <p>
     * 注意：CPU 使用率需要间隔 100ms 采样计算
     *
     * @return CPU 信息 DTO
     */
    SystemHardwareDTO.CpuDTO buildCpuDTO();

    /**
     * 构建内存 DTO
     *
     * @return 内存信息 DTO
     */
    SystemHardwareDTO.MemoryDTO buildMemoryDTO();

    /**
     * 构建磁盘 DTO 列表（所有挂载点）
     *
     * @return 磁盘信息列表（过滤掉容量为0的虚拟磁盘）
     */
    List<SystemHardwareDTO.DiskDTO> buildDiskDTOList();

    /**
     * 构建网络 DTO 列表（所有网卡）
     *
     * @return 网络接口信息列表
     */
    List<SystemHardwareDTO.NetworkDTO> buildNetworkDTOList();
}
