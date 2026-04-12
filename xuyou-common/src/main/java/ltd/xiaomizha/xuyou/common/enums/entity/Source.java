package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 来源
 */
@Getter
public enum Source {

    MONTHLY_GRANT("MONTHLY_GRANT"),
    PURCHASE("PURCHASE"),
    REWARD("REWARD"),
    ADMIN_GRANT("ADMIN_GRANT"),
    ;

    private final String value;

    Source(String value) {
        this.value = value;
    }
}
