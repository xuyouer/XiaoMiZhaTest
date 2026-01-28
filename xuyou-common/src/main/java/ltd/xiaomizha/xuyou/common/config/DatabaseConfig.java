package ltd.xiaomizha.xuyou.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 数据库配置类
 * <p>
 * 仅在未配置数据源且存在数据源配置属性时才会创建数据源
 */
@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@ConditionalOnMissingBean(DataSource.class)
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return new DruidDataSource();
    }
}