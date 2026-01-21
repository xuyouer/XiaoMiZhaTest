package ltd.xiaomizha.xuyou.common.utils.datasource;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
public class DatabaseChecker {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void checkDatabaseConnection() {
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
                "achievements", "cards", "characters", "chat_messages", "daily_missions", "friends", "game_players", "game_rooms", "game_rounds", "game_statistics", "items", "leaderboard_records", "leaderboards", "map_cells", "player_assets", "player_cards", "player_items", "seasons", "system_configs", "theme_achievements", "theme_features", "themes", "transactions", "user_achievements", "user_daily_missions", "user_feedback", "user_theme_progress", "users"
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