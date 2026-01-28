package ltd.xiaomizha.xuyou.common.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

/**
 * Druid监控服务
 * <p>
 * 仅在存在DataSource时才会加载
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(DataSource.class)
public class SimpleDruidMonitorService {

    private final DataSource dataSource;

    /**
     * 获取连接池摘要信息
     */
    public Map<String, Object> getConnectionPoolSummary() {
        Map<String, Object> summary = new HashMap<>();

        if (!(dataSource instanceof DruidDataSource)) {
            summary.put("type", "Non-Druid DataSource");
            summary.put("message", "当前数据源不是Druid类型");
            return summary;
        }

        DruidDataSource druidDataSource = (DruidDataSource) dataSource;

        // 基础信息
        summary.put("type", "DruidDataSource");
        summary.put("name", druidDataSource.getName());
        summary.put("url", maskPassword(druidDataSource.getUrl()));
        summary.put("username", druidDataSource.getUsername());

        // 连接池状态
        Map<String, Object> poolStatus = new HashMap<>();
        poolStatus.put("active", druidDataSource.getActiveCount());
        poolStatus.put("idle", druidDataSource.getPoolingCount());
        poolStatus.put("max", druidDataSource.getMaxActive());
        poolStatus.put("minIdle", druidDataSource.getMinIdle());
        poolStatus.put("initialSize", druidDataSource.getInitialSize());

        // 尝试获取等待线程数
        try {
            poolStatus.put("waitThreads", druidDataSource.getWaitThreadCount());
        } catch (Exception e) {
            poolStatus.put("waitThreads", "N/A");
        }

        // 性能统计
        Map<String, Object> performance = new HashMap<>();
        performance.put("createCount", druidDataSource.getCreateCount());
        performance.put("destroyCount", druidDataSource.getDestroyCount());
        performance.put("connectCount", druidDataSource.getConnectCount());
        performance.put("closeCount", druidDataSource.getCloseCount());
        performance.put("createErrorCount", druidDataSource.getCreateErrorCount());
        performance.put("connectErrorCount", druidDataSource.getConnectErrorCount());

        // 使用率
        double usageRate = 0;
        int maxActive = druidDataSource.getMaxActive();
        if (maxActive > 0) {
            usageRate = (double) druidDataSource.getActiveCount() / maxActive;
        }

        Map<String, Object> usage = new HashMap<>();
        usage.put("usageRate", String.format("%.2f%%", usageRate * 100));
        usage.put("status", getUsageStatus(usageRate));

        summary.put("poolStatus", poolStatus);
        summary.put("performance", performance);
        summary.put("usage", usage);
        summary.put("timestamp", new Date());
        summary.put("healthy", isPoolHealthy(druidDataSource));

        return summary;
    }

    /**
     * 获取健康状态
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();

        if (!(dataSource instanceof DruidDataSource)) {
            health.put("status", "UNKNOWN");
            health.put("message", "非Druid数据源");
            return health;
        }

        DruidDataSource druidDataSource = (DruidDataSource) dataSource;

        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 检查连接使用率
        int active = druidDataSource.getActiveCount();
        int maxActive = druidDataSource.getMaxActive();

        if (maxActive > 0) {
            double usage = (double) active / maxActive;
            if (usage > 0.9) {
                issues.add("连接池使用率超过90%");
            } else if (usage > 0.7) {
                warnings.add("连接池使用率较高");
            }
            health.put("usageRate", String.format("%.2f%%", usage * 100));
        }

        // 检查错误
        if (druidDataSource.getCreateErrorCount() > 0) {
            issues.add("存在创建连接错误");
        }

        if (druidDataSource.getConnectErrorCount() > 0) {
            issues.add("存在连接错误");
        }

        // 汇总状态
        String status;
        if (!issues.isEmpty()) {
            status = "UNHEALTHY";
        } else if (!warnings.isEmpty()) {
            status = "WARNING";
        } else {
            status = "HEALTHY";
        }

        health.put("status", status);
        health.put("issues", issues);
        health.put("warnings", warnings);
        health.put("activeConnections", active);
        health.put("maxConnections", maxActive);
        health.put("idleConnections", druidDataSource.getPoolingCount());
        health.put("timestamp", new Date());

        return health;
    }

    /**
     * 获取配置信息
     */
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();

        if (!(dataSource instanceof DruidDataSource)) {
            config.put("error", "非Druid数据源");
            return config;
        }

        DruidDataSource druidDataSource = (DruidDataSource) dataSource;

        // 基础配置
        config.put("initialSize", druidDataSource.getInitialSize());
        config.put("minIdle", druidDataSource.getMinIdle());
        config.put("maxActive", druidDataSource.getMaxActive());
        config.put("maxWait", druidDataSource.getMaxWait());
        config.put("timeBetweenEvictionRuns", druidDataSource.getTimeBetweenEvictionRunsMillis());
        config.put("minEvictableIdleTime", druidDataSource.getMinEvictableIdleTimeMillis());

        // 验证配置
        config.put("validationQuery", druidDataSource.getValidationQuery());
        config.put("testWhileIdle", druidDataSource.isTestWhileIdle());
        config.put("testOnBorrow", druidDataSource.isTestOnBorrow());
        config.put("testOnReturn", druidDataSource.isTestOnReturn());

        // 其他配置
        config.put("poolPreparedStatements", druidDataSource.isPoolPreparedStatements());

        try {
            config.put("maxPoolPreparedStatementPerConnectionSize",
                    druidDataSource.getMaxPoolPreparedStatementPerConnectionSize());
        } catch (Exception e) {
            config.put("maxPoolPreparedStatementPerConnectionSize", "N/A");
        }

        return config;
    }

    /**
     * 重置统计
     */
    public Map<String, String> resetStatistics() {
        Map<String, String> result = new HashMap<>();

        try {
            DruidStatManagerFacade.getInstance().resetAll();

            if (dataSource instanceof DruidDataSource) {
                DruidDataSource druidDataSource = (DruidDataSource) dataSource;
                // 重置连接池统计
                druidDataSource.resetStat();
            }

            result.put("status", "SUCCESS");
            result.put("message", "统计信息已重置");
            result.put("timestamp", new Date().toString());

            log.info("Druid统计信息已重置");
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "重置失败: " + e.getMessage());
            log.error("重置Druid统计失败", e);
        }

        return result;
    }

    /**
     * 检查监控功能可用性
     */
    public Map<String, Object> checkMonitoringCapabilities() {
        Map<String, Object> capabilities = new HashMap<>();

        capabilities.put("dataSourceType", dataSource.getClass().getName());
        capabilities.put("isDruid", dataSource instanceof DruidDataSource);

        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;

            List<String> availableMethods = new ArrayList<>();
            List<String> unavailableMethods = new ArrayList<>();

            // 检查关键方法
            String[] methodsToCheck = {
                    "getActiveCount",
                    "getMaxActive",
                    "getPoolingCount",
                    "getCreateCount",
                    "getDestroyCount",
                    "getWaitThreadCount",
                    "getErrorCount",
                    "getActivePeak",
                    "getActivePeakTime"
            };

            for (String method : methodsToCheck) {
                try {
                    druidDataSource.getClass().getMethod(method);
                    availableMethods.add(method);
                } catch (NoSuchMethodException e) {
                    unavailableMethods.add(method);
                }
            }

            capabilities.put("availableMethods", availableMethods);
            capabilities.put("unavailableMethods", unavailableMethods);
            capabilities.put("totalMethods", methodsToCheck.length);
            capabilities.put("availablePercentage",
                    String.format("%.1f%%", (double) availableMethods.size() / methodsToCheck.length * 100));
        }

        return capabilities;
    }

    private String maskPassword(String url) {
        if (url == null) return null;
        // 简单隐藏密码
        return url.replaceAll("password=[^&]*", "password=***");
    }

    private String getUsageStatus(double usageRate) {
        if (usageRate > 0.9) return "CRITICAL";
        if (usageRate > 0.7) return "WARNING";
        if (usageRate > 0.5) return "MODERATE";
        return "NORMAL";
    }

    private boolean isPoolHealthy(DruidDataSource druidDataSource) {
        try {
            // 简单健康检查
            double usageRate = (double) druidDataSource.getActiveCount() / druidDataSource.getMaxActive();
            return usageRate < 0.9 &&
                    druidDataSource.getCreateErrorCount() == 0 &&
                    druidDataSource.getConnectErrorCount() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}