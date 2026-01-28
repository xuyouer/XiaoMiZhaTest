package ltd.xiaomizha.xuyou.common.utils.datasource;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接检查器
 * <p>
 * 仅在存在DataSource和JdbcTemplate时才会加载和执行
 */
@Slf4j
@Component
@ConditionalOnBean({DataSource.class, JdbcTemplate.class})
public class DatabaseChecker {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void checkDatabaseConnection() {
        if (dataSource == null || jdbcTemplate == null) {
            log.debug("数据源或JdbcTemplate未配置, 跳过数据库连接检查");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                log.info("数据库连接成功");

                // 测试查询数据库版本
                String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
                log.info("数据库版本: {}", version);

                // 检查必要的表是否存在
                checkTables();
            }
        } catch (SQLException e) {
            log.error("数据库连接失败: {}", e.getMessage());
        }
    }

    private void checkTables() {
        String[] requiredTables = {
                "role_resource_relations",
                "system_configs",
                "user_feedback",
                "user_login_records",
                "user_logs",
                "user_name_history",
                "user_names",
                "user_points",
                "user_points_log",
                "user_profiles",
                "user_resource_relations",
                "user_resources",
                "user_role_relations",
                "user_roles",
                "user_vip_info",
                "user_vip_log",
                "user_vip_points_log",
                "users",
                "vip_level_config",
                "vip_points_rules",
        };

        for (String table : requiredTables) {
            try {
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                        Integer.class,
                        table
                );
                if (count != null && count > 0) {
                    log.info("表 {} 存在", table);
                } else {
                    log.warn("表 {} 不存在，请运行SQL脚本初始化数据库", table);
                }
            } catch (Exception e) {
                log.error("检查表 {} 时出错: {}", table, e.getMessage());
            }
        }
    }
}