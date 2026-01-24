package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 授权类型枚举
 */
@Getter
public enum GrantType {

    DIRECT("DIRECT"),
    INHERITED("INHERITED"),
    ROLE_BASED("ROLE_BASED"),
    ;

    private final String value;

    GrantType(String value) {
        this.value = value;
    }
}
