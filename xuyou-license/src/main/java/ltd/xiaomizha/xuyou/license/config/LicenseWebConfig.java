package ltd.xiaomizha.xuyou.license.config;

import ltd.xiaomizha.xuyou.license.interceptor.LicenseValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 许可证Web配置
 * <p>
 * 用于注册许可证验证拦截器
 */
@Configuration
public class LicenseWebConfig implements WebMvcConfigurer {

    private final LicenseValidationInterceptor licenseValidationInterceptor;

    public LicenseWebConfig(LicenseValidationInterceptor licenseValidationInterceptor) {
        this.licenseValidationInterceptor = licenseValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册许可证验证拦截器
        registry.addInterceptor(licenseValidationInterceptor)
                .addPathPatterns("/license/**")
                .excludePathPatterns("/license/validate/**", "/license/activate", "/license/generate-trial");
    }
}
