package ltd.xiaomizha.xuyou.common.utils.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.utils.http.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DruidMonitorInitializer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) throws Exception {
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            String druidUsername = ServiceUtils.getServerContextPath(env, "spring.datasource.druid.stat-view-servlet.login-username", "");
            String druidPassword = ServiceUtils.getServerContextPath(env, "spring.datasource.druid.stat-view-servlet.login-password", "");

            log.info("================== Druid 监控初始化 ==================");
            log.info("   监控控制台地址: http://localhost:{}{}/druid", ServiceUtils.getServerPort(env), ServiceUtils.getServerContextPath(env));
            log.info("   登录用户名: {}", druidUsername);
            log.info("   登录密码: {}", druidPassword);
            log.info("   数据库连接池配置:");
            log.info("   URL: {}", druidDataSource.getUrl());
            log.info("   初始连接数: {}", druidDataSource.getInitialSize());
            log.info("   最小空闲连接: {}", druidDataSource.getMinIdle());
            log.info("   最大连接数: {}", druidDataSource.getMaxActive());
            log.info("   获取连接超时时间: {}ms", druidDataSource.getMaxWait());
            log.info("===================================================");

            // 启动定时监控任务
            startMonitorTask(druidDataSource);
        }
    }

    private void startMonitorTask(DruidDataSource druidDataSource) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 每30秒监控一次连接池状态
        scheduler.scheduleAtFixedRate(() -> {
            try {
                monitorConnectionPool(druidDataSource);
            } catch (Exception e) {
                log.warn("连接池监控任务异常: {}", e.getMessage());
            }
        }, 30, 30, TimeUnit.SECONDS);

        log.info("   Druid连接池监控任务已启动, 每30秒检查一次");
    }

    private void monitorConnectionPool(DruidDataSource druidDataSource) {
        int activeCount = druidDataSource.getActiveCount();
        int maxActive = druidDataSource.getMaxActive();
        int poolingCount = druidDataSource.getPoolingCount();
        double usageRate = (double) activeCount / maxActive;

        if (usageRate > 0.8) {
            log.warn("   连接池使用率过高: {}/{} ({}%)", activeCount, maxActive, String.format("%.1f", usageRate * 100));
        }

        // 检查慢查询
        try (Connection connection = druidDataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW PROCESSLIST")) {

            int slowQueries = 0;
            while (rs.next()) {
                long time = rs.getLong("Time");
                if (time > 5000) { // 5秒以上的查询
                    slowQueries++;
                    String info = rs.getString("Info");
                    if (info != null && info.length() > 100) {
                        info = info.substring(0, 100) + "...";
                    }
                    log.warn("   慢查询 detected: Time={}s, Query={}", time, info);
                }
            }

            if (slowQueries > 0) {
                log.warn("   发现 {} 个慢查询", slowQueries);
            }

        } catch (Exception e) {
            // 忽略监控异常
        }
    }
}