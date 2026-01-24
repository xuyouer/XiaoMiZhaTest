package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 徽章类型枚举
 */
@Getter
public enum BadgeType {

    DANGER("danger"),
    WARNING("warning"),
    SUCCESS("success"),
    INFO("info"),
    PRIMARY("primary"),
    ;
    
    private final String value;

    BadgeType(String value) {
        this.value = value;
    }
}
