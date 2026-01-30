package ltd.xiaomizha.xuyou.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    /**
     * 用户服务降级处理
     */
    @RequestMapping("/fallback/user")
    public String userFallback() {
        return "{\"code\": 503, \"message\": \"用户服务暂时不可用, 请稍后重试\"}";
    }

    /**
     * 通用降级处理
     */
    @RequestMapping("/fallback/default")
    public String defaultFallback() {
        return "{\"code\": 503, \"message\": \"服务暂时不可用, 请稍后重试\"}";
    }

}
