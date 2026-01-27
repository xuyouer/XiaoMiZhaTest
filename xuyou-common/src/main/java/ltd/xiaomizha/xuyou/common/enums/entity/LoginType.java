package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 登录类型
 */
@Getter
public enum LoginType {
    LOGIN("LOGIN"),
    AUTO_LOGIN("AUTO_LOGIN"),
    TOKEN_REFRESH("TOKEN_REFRESH"),
    ;

    private final String value;

    LoginType(String value) {
        this.value = value;
    }
}
