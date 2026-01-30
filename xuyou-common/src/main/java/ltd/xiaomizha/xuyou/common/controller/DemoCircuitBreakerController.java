package ltd.xiaomizha.xuyou.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.service.DemoCircuitBreakerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
@Tag(name = "熔断演示", description = "熔断机制演示API")
public class DemoCircuitBreakerController {

    @Resource
    private DemoCircuitBreakerService demoCircuitBreakerService;

    /**
     * 测试熔断功能
     */
    @GetMapping("/circuit-breaker")
    @Operation(summary = "测试熔断功能", description = "测试Resilience4j熔断机制")
    public ResponseResult<String> testCircuitBreaker(@RequestParam String param) {
        String result = demoCircuitBreakerService.demoCircuitBreaker(param);
        return new ResponseResult<String>().code(200).message("成功").data(result);
    }

    /**
     * 重置失败计数
     */
    @GetMapping("/reset-fail-count")
    @Operation(summary = "重置失败计数", description = "重置熔断测试的失败计数")
    public ResponseResult<Void> resetFailCount() {
        demoCircuitBreakerService.resetFailCount();
        return ResponseResult.ok("重置失败计数成功");
    }

}