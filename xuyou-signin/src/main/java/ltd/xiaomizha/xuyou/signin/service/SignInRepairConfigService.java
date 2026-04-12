package ltd.xiaomizha.xuyou.signin.service;

import ltd.xiaomizha.xuyou.signin.entity.SignInRepairConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_config(补签配置表)】的数据库操作Service
 * @createDate 2026-03-17 16:02:17
 */
public interface SignInRepairConfigService extends IService<SignInRepairConfig> {

    /**
     * 获取配置值
     *
     * @param configKey 配置键
     * @return 配置值, 不存在返回null
     */
    String getConfigValue(String configKey);

    /**
     * 获取整数类型配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值的整数形式, 无效时返回默认值
     */
    int getConfigValueAsInt(String configKey, int defaultValue);

    /**
     * 获取浮点数类型配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值的浮点数形式, 无效时返回默认值
     */
    double getConfigValueAsDouble(String configKey, double defaultValue);

    /**
     * 获取布尔类型配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值的布尔形式, "true"或"1"返回true
     */
    boolean getConfigValueAsBoolean(String configKey, boolean defaultValue);

    /**
     * 设置配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    void setConfigValue(String configKey, String configValue);

}
