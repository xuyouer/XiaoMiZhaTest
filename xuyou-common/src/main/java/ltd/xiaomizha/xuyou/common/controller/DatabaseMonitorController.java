package ltd.xiaomizha.xuyou.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.service.SimpleDruidMonitorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据库监控Controller
 * <p>
 * 仅在存在DataSource时才会加载
 */
@Slf4j
@RestController
@RequestMapping("monitor/druid")
@Tag(name = "数据库监控", description = "数据库连接池监控")
@RequiredArgsConstructor
@ConditionalOnBean(DataSource.class)
public class DatabaseMonitorController {

    private final SimpleDruidMonitorService monitorService;

    @GetMapping("/summary")
    @Operation(summary = "获取Druid连接池摘要")
    public Map<String, Object> getDruidSummary() {
        return monitorService.getConnectionPoolSummary();
    }

    @GetMapping("/health")
    @Operation(summary = "检查连接池健康状态")
    public Map<String, Object> checkDruidHealth() {
        return monitorService.getHealthStatus();
    }

    @GetMapping("/config")
    @Operation(summary = "获取Druid配置")
    public Map<String, Object> getDruidConfig() {
        return monitorService.getConfiguration();
    }

    @PostMapping("/reset")
    @Operation(summary = "重置Druid统计")
    public Map<String, String> resetDruidStats() {
        return monitorService.resetStatistics();
    }

    @GetMapping("/capabilities")
    @Operation(summary = "检查监控功能")
    public Map<String, Object> checkCapabilities() {
        return monitorService.checkMonitoringCapabilities();
    }

    @GetMapping("/status")
    @Operation(summary = "获取监控状态")
    public Map<String, Object> getMonitorStatus() {
        Map<String, Object> summary = monitorService.getConnectionPoolSummary();
        Map<String, Object> health = monitorService.getHealthStatus();
        Map<String, Object> capabilities = monitorService.checkMonitoringCapabilities();

        return Map.of(
                "summary", summary,
                "health", health,
                "capabilities", capabilities,
                "timestamp", new java.util.Date()
        );
    }
}