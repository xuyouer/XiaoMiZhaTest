package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 日志级别枚举
 */
@Getter
public enum Level {

    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    CRITICAL("CRITICAL"),
    ;

    private final String value;

    Level(String value) {
        this.value = value;
    }
}
