package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.entity.SystemConfigs;
import ltd.xiaomizha.xuyou.user.mapper.SystemConfigsMapper;
import ltd.xiaomizha.xuyou.user.service.SystemConfigsService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author xiaom
 * @description 针对表【system_configs(系统配置表)】的数据库操作Service实现
 * @createDate 2026-01-23 21:12:33
 */
@Slf4j
@Service
public class SystemConfigsServiceImpl extends ServiceImpl<SystemConfigsMapper, SystemConfigs> implements SystemConfigsService {

    @Resource
    private SystemConfigsMapper systemConfigsMapper;

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值, 如果不存在返回null
     */
    @Override
    @Cacheable(value = "systemConfigs", key = "#configKey")
    public String getConfigValueByKey(String configKey) {
        return systemConfigsMapper.getConfigValueByKey(configKey);
    }

    /**
     * 根据配置键获取配置值
     * <p>
     * 支持默认值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    @Override
    @Cacheable(value = "systemConfigs", key = "#configKey + '_' + #defaultValue")
    public String getConfigValueByKey(String configKey, String defaultValue) {
        String value = getConfigValueByKey(configKey);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    /**
     * 使用LambdaQueryWrapper查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Override
    @Cacheable(value = "systemConfigs", key = "#configKey")
    public String getValueByKeyOrLambda(String configKey) {
        LambdaQueryWrapper<SystemConfigs> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfigs::getConfigKey, configKey)
                .select(SystemConfigs::getConfigValue)
                .last("LIMIT 1");
        SystemConfigs config = systemConfigsMapper.selectOne(queryWrapper);
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 根据配置键获取配置对象
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    @Override
    @Cacheable(value = "systemConfigs", key = "'config:' + #configKey")
    public SystemConfigs getConfigObjectByKey(String configKey) {
        clearCache(configKey);
        LambdaQueryWrapper<SystemConfigs> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfigs::getConfigKey, configKey)
                .last("LIMIT 1");
        SystemConfigs config = this.getOne(queryWrapper);
        return systemConfigsMapper.getConfigObjectByKey(configKey);
    }

    /**
     * 清除指定配置的缓存
     */
    @CacheEvict(value = "systemConfigs", key = "'config:' + #configKey")
    public void clearCache(String configKey) {
        log.info("清除配置缓存: {}", configKey);
    }

    /**
     * 清除所有配置缓存
     */
    @CacheEvict(value = "systemConfigs", allEntries = true)
    public void clearAllCache() {
        log.info("清除所有配置缓存");
    }
}




