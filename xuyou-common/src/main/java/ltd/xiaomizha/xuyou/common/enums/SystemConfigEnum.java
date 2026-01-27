package ltd.xiaomizha.xuyou.common.enums;

import lombok.Getter;

/**
 * 系统配置枚举
 */
@Getter
public enum SystemConfigEnum {

    /**
     * 是否允许更新用户名
     */
    ALLOW_UPDATE_USERNAME("allow_update_username", "false", "是否允许更新用户名");

    private final String key;
    private final String defaultValue;
    private final String description;

    SystemConfigEnum(String key, String defaultValue, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    /**
     * 获取配置键名
     */
    public String getKey() {
        return key;
    }

    /**
     * 获取默认值
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return description;
    }
}
