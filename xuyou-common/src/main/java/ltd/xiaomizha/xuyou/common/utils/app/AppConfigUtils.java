package ltd.xiaomizha.xuyou.common.utils.app;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 应用配置工具类
 */
@Slf4j
@Component
public class AppConfigUtils {

    private static Environment staticEnvironment;
    private static Boolean showDetailedError;

    @Value("${xuyou.config.show-detailed-error:false}")
    private Boolean showDetailedErrorProperty;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        staticEnvironment = this.environment;
        showDetailedError = this.showDetailedErrorProperty;
    }

    /**
     * 判断当前是否为生产环境
     *
     * @return 如果是生产环境返回 true, 否则返回 false
     */
    public static boolean isProduction() {
        if (staticEnvironment == null) {
            return false;
        }
        return staticEnvironment.acceptsProfiles(Profiles.of("prod", "production"));
    }

    /**
     * 判断当前是否为开发环境
     *
     * @return 如果是开发环境返回 true, 否则返回 false
     */
    public static boolean isDevelopment() {
        if (staticEnvironment == null) {
            return true; // 默认开发环境
        }
        return staticEnvironment.acceptsProfiles(Profiles.of("dev", "development"));
    }

    /**
     * 判断当前是否为测试环境
     *
     * @return 如果是测试环境返回 true, 否则返回 false
     */
    public static boolean isTest() {
        if (staticEnvironment == null) {
            return false;
        }
        return staticEnvironment.acceptsProfiles(Profiles.of("test"));
    }

    /**
     * 获取当前激活的配置文件
     *
     * @return 激活的配置文件列表
     */
    public static List<String> getActiveProfiles() {
        if (staticEnvironment == null) {
            return Arrays.asList("default");
        }
        return Arrays.asList(staticEnvironment.getActiveProfiles());
    }

    /**
     * 获取指定配置属性
     *
     * @param key          属性键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getProperty(String key, String defaultValue) {
        if (staticEnvironment == null) {
            return defaultValue;
        }
        return staticEnvironment.getProperty(key, defaultValue);
    }

    /**
     * 获取指定配置属性
     *
     * @param key 属性键
     * @return 配置值
     */
    public static String getProperty(String key) {
        if (staticEnvironment == null) {
            return null;
        }
        return staticEnvironment.getProperty(key);
    }

    /**
     * 是否显示详细错误信息
     * <p>
     * 优先级: 配置 > 环境
     */
    public static boolean shouldShowDetailedError() {
        if (showDetailedError != null) {
            return showDetailedError;
        }
        return !isProduction();
    }

    /**
     * 获取应用名称
     */
    public static String getAppName() {
        return getProperty("spring.application.name", "monopoly");
    }

}
