package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairConfig;
import ltd.xiaomizha.xuyou.signin.mapper.SignInRepairConfigMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairConfigService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_config(补签配置表)】的数据库操作Service实现
 * @createDate 2026-03-17 16:02:17
 */
@Service
public class SignInRepairConfigServiceImpl extends ServiceImpl<SignInRepairConfigMapper, SignInRepairConfig>
        implements SignInRepairConfigService {

    /**
     * 获取配置值
     *
     * @param configKey 配置键
     * @return 配置值, 不存在返回null
     */
    @Override
    public String getConfigValue(String configKey) {
        LambdaQueryWrapper<SignInRepairConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairConfig::getConfigKey, configKey)
                .eq(SignInRepairConfig::getIsActive, 1);
        SignInRepairConfig config = getOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 获取整数类型配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值的整数形式, 无效时返回默认值
     */
    @Override
    public int getConfigValueAsInt(String configKey, int defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取浮点数类型配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值的浮点数形式, 无效时返回默认值
     */
    @Override
    public double getConfigValueAsDouble(String configKey, double defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔类型配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值的布尔形式, "true"或"1"返回true
     */
    @Override
    public boolean getConfigValueAsBoolean(String configKey, boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    /**
     * 设置配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    @Override
    public void setConfigValue(String configKey, String configValue) {
        LambdaQueryWrapper<SignInRepairConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairConfig::getConfigKey, configKey);
        SignInRepairConfig config = getOne(wrapper);

        if (config != null) {
            config.setConfigValue(configValue);
            updateById(config);
        }
    }
}




