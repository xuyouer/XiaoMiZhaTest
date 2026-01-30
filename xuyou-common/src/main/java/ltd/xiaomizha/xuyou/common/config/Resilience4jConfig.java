package ltd.xiaomizha.xuyou.common.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    /**
     * 配置熔断策略
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                // 失败率阈值，超过这个阈值就会熔断
                .failureRateThreshold(50)
                // 熔断持续时间（默认60秒）
                .waitDurationInOpenState(Duration.ofSeconds(60))
                // 半开状态下的请求数
                .slidingWindowSize(20)
                // 最小请求数，只有达到这个数才开始计算失败率
                .minimumNumberOfCalls(5)
                // 滑动窗口类型：COUNT_BASED或TIME_BASED
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                // 慢调用阈值
                .slowCallRateThreshold(50)
                // 慢调用定义的时间
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                // 自动从半开状态恢复到闭合状态
                .permittedNumberOfCallsInHalfOpenState(10)
                .build();
    }

    /**
     * 配置时间限制器
     */
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
                // 超时时间
                .timeoutDuration(Duration.ofSeconds(3))
                .build();
    }

    /**
     * 熔断注册表
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(circuitBreakerConfig());
    }

    /**
     * 时间限制器注册表
     */
    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        return TimeLimiterRegistry.of(timeLimiterConfig());
    }

}