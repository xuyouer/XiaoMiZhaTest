package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 状态
 */
@Getter
public enum Status {

    INACTIVE("INACTIVE"),
    ACTIVE("ACTIVE"),
    EXPIRED("EXPIRED"),
    SUSPENDED("SUSPENDED"),
    REVOKED("REVOKED"),
    ;

    private final String value;

    Status(String value) {
        this.value = value;
    }
}
