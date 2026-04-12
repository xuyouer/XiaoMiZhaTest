package ltd.xiaomizha.xuyou.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类
 */
@Slf4j
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 路由拦截器
        // 使用路由匹配方式
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 登录、注册、刷新 Token 等接口不需要登录校验
            SaRouter.match("/**")
                    .notMatch(
                            "/auth/login", // JWT/Sa-Token 登录
                            "/auth/sso/**", // SSO 相关接口
                            "/auth/refresh-token", // 刷新 AccessToken
                            "/auth/info", // 获取用户信息（可选放行）
                            "/doc.html", // Knife4j 文档
                            "/swagger-ui/**", // Swagger UI
                            "/v3/api-docs/**" // OpenAPI 文档
                    )
                    .check(r -> StpUtil.checkLogin()); // 校验是否登录
        })).addPathPatterns("/**");
    }

}
