package ltd.xiaomizha.xuyou.common.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class DemoCircuitBreakerService {

    private int failCount = 0;

    /**
     * 演示熔断功能的方法
     * 使用@CircuitBreaker注解，指定熔断策略和降级方法
     */
    @CircuitBreaker(name = "demoCircuitBreaker", fallbackMethod = "fallbackMethod")
    public String demoCircuitBreaker(String param) {
        log.info("调用demoCircuitBreaker方法，参数：{}", param);

        // 模拟失败情况，每调用3次失败1次
        failCount++;
        if (failCount % 3 == 0) {
            log.error("模拟服务调用失败");
            throw new RuntimeException("服务调用失败");
        }

        // 模拟处理时间
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "服务调用成功，参数：" + param + "，时间：" + LocalDateTime.now();
    }

    /**
     * 降级方法，当服务熔断时调用
     */
    public String fallbackMethod(String param, Throwable throwable) {
        log.warn("服务熔断，执行降级逻辑，参数：{}", param, throwable);
        return "服务暂时不可用，请稍后重试，参数：" + param;
    }

    /**
     * 重置失败计数
     */
    public void resetFailCount() {
        failCount = 0;
        log.info("重置失败计数");
    }

}
