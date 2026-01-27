package ltd.xiaomizha.xuyou.common.enums.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import ltd.xiaomizha.xuyou.common.utils.enums.EnumUtils;

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

    @JsonValue
    @EnumValue
    private final String value;

    BadgeType(String value) {
        this.value = value;
    }

    /**
     * 根据value获取枚举常量
     */
    public static BadgeType getByValue(String value) {
        return EnumUtils.getEnumByValue(BadgeType.class, value);
    }
}
