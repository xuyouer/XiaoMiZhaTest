package ltd.xiaomizha.xuyou.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.xiaomizha.xuyou.user.entity.SystemConfigs;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

/**
 * @author xiaom
 * @description 针对表【system_configs(系统配置表)】的数据库操作Mapper
 * @createDate 2026-01-23 21:12:33
 * @Entity ltd.xiaomizha.xuyou.user.entity.SystemConfigs
 */
public interface SystemConfigsMapper extends BaseMapper<SystemConfigs> {

    /**
     * 根据配置键查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Select("SELECT config_value FROM system_configs WHERE config_key = #{configKey}")
    String getConfigValueByKey(@Param("configKey") String configKey);

    /**
     * 根据配置键查询配置(包含所有字段)
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    @Select("SELECT * FROM system_configs WHERE config_key = #{configKey} LIMIT 1")
    SystemConfigs getConfigObjectByKey(@Param("configKey") String configKey);

}




