package ltd.xiaomizha.xuyou.common.config;

import lombok.extern.slf4j.Slf4j;
import org.dromara.oa.core.provider.config.OaConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SMS4J OA 模块配置类
 */
@Slf4j
@Configuration
@ConditionalOnClass(OaConfig.class) // 仅当 SMS4J OA 在 classpath 中时生效
public class SmsOaConfig {

    /**
     * 创建 OA 配置 Bean
     *
     * @return OaConfig 配置对象
     */
    @Bean
    public OaConfig oaConfig() {
        log.info("初始化 SMS4J OA 配置 Bean");

        OaConfig config = new OaConfig();

        // 配置线程池核心参数
        config.setCorePoolSize(10); // 核心线程数
        config.setMaxPoolSize(30); // 最大线程数
        config.setQueueCapacity(200); // 队列容量

        log.info("SMS4J OA 配置初始化完成: corePoolSize={}, maxPoolSize={}", config.getCorePoolSize(), config.getMaxPoolSize());

        return config;
    }

}
