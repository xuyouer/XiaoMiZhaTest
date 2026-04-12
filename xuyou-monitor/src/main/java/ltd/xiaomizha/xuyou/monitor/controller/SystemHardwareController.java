package ltd.xiaomizha.xuyou.monitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.monitor.dto.SystemHardwareDTO;
import ltd.xiaomizha.xuyou.monitor.service.SystemHardwareService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统硬件信息控制器
 */
@Slf4j
@RestController
@RequestMapping("system/hardware")
@Tag(name = "系统硬件监控", description = "系统硬件信息采集API")
public class SystemHardwareController {

    @Resource
    private SystemHardwareService systemHardwareService;

    /**
     * 获取完整系统硬件信息
     * <p>
     * 包含：操作系统、CPU、内存、磁盘、网络等所有信息
     *
     * @return 完整的硬件信息汇总 DTO
     */
    @GetMapping("/info")
    @Operation(summary = "获取完整硬件信息", description = "获取操作系统、CPU、内存、磁盘、网络等完整硬件信息")
    public ResponseResult<?> getHardwareInfo() {
        log.info("请求获取完整系统硬件信息");
        try {
            SystemHardwareDTO hardwareInfo = systemHardwareService.getSystemHardwareInfo();
            return ResponseResult.ok(hardwareInfo);
        } catch (Exception e) {
            log.error("获取系统硬件信息失败", e);
            return ResponseResult.error("获取系统硬件信息失败: " + e.getMessage());
        }
    }

    /**
     * 单独获取操作系统信息
     *
     * @return 操作系统信息 DTO（名称、版本、运行时间）
     */
    @GetMapping("/os")
    @Operation(summary = "获取操作系统信息", description = "单独获取操作系统信息")
    public ResponseResult<?> getOsInfo() {
        log.debug("请求获取操作系统信息");
        try {
            SystemHardwareDTO.OsDTO osInfo = systemHardwareService.buildOsDTO();
            return ResponseResult.ok(osInfo);
        } catch (Exception e) {
            log.error("获取操作系统信息失败", e);
            return ResponseResult.error("获取操作系统信息失败: " + e.getMessage());
        }
    }

    /**
     * 单独获取 CPU 信息
     *
     * @return CPU 信息 DTO（型号、核心数、使用率）
     */
    @GetMapping("/cpu")
    @Operation(summary = "获取CPU信息", description = "单独获取CPU型号、核心数、使用率等信息")
    public ResponseResult<?> getCpuInfo() {
        log.debug("请求获取CPU信息");
        try {
            SystemHardwareDTO.CpuDTO cpuInfo = systemHardwareService.buildCpuDTO();
            return ResponseResult.ok(cpuInfo);
        } catch (Exception e) {
            log.error("获取CPU信息失败", e);
            return ResponseResult.error("获取CPU信息失败: " + e.getMessage());
        }
    }

    /**
     * 单独获取内存信息
     *
     * @return 内存信息 DTO（总容量、可用、已用、使用率）
     */
    @GetMapping("/memory")
    @Operation(summary = "获取内存信息", description = "单独获取内存总容量、可用容量、已用容量、使用率等信息")
    public ResponseResult<?> getMemoryInfo() {
        log.debug("请求获取内存信息");
        try {
            SystemHardwareDTO.MemoryDTO memoryInfo = systemHardwareService.buildMemoryDTO();
            return ResponseResult.ok(memoryInfo);
        } catch (Exception e) {
            log.error("获取内存信息失败", e);
            return ResponseResult.error("获取内存信息失败: " + e.getMessage());
        }
    }
}
