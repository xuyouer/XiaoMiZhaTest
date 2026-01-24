package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

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

    private final String value;

    Target(String value) {
        this.value = value;
    }
}
