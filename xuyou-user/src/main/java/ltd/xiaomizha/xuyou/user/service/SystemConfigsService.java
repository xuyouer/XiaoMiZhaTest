package ltd.xiaomizha.xuyou.user.service;

import ltd.xiaomizha.xuyou.user.entity.SystemConfigs;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author xiaom
 * @description 针对表【system_configs(系统配置表)】的数据库操作Service
 * @createDate 2026-01-23 21:12:33
 */
public interface SystemConfigsService extends IService<SystemConfigs> {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值, 如果不存在返回null
     */
    String getConfigValueByKey(String configKey);

    /**
     * 根据配置键获取配置值
     * <p>
     * 支持默认值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    String getConfigValueByKey(String configKey, String defaultValue);

    /**
     * 使用LambdaQueryWrapper查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getValueByKeyOrLambda(String configKey);

    /**
     * 根据配置键获取配置对象
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    SystemConfigs getConfigObjectByKey(String configKey);

}
