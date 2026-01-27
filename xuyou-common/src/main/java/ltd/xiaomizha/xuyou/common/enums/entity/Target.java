package ltd.xiaomizha.xuyou.common.enums.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import ltd.xiaomizha.xuyou.common.utils.enums.EnumUtils;

/**
 * 链接打开方式枚举
 */
@Getter
public enum Target {

    SELF("_self"),
    BLANK("_blank"),
    PARENT("_parent"),
    TOP("_top"),
    ;

    @JsonValue
    @EnumValue
    private final String value;

    Target(String value) {
        this.value = value;
    }
    
    /**
     * 根据value获取枚举常量
     */
    public static Target getByValue(String value) {
        return EnumUtils.getEnumByValue(Target.class, value);
    }
}
