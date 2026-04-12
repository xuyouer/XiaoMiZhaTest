package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 链接打开方式枚举
 */
@Getter
public enum TokenType {

    REGISTER("REGISTER"),
    RESET("RESET"),
    BIND("BIND"),
    OTHER("OTHER"),
    ;

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

}
