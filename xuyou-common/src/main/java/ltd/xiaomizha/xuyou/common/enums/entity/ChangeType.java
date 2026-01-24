package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 变更类型枚举
 */
@Getter
public enum ChangeType {

    UPGRADE("UPGRADE"),
    DOWNGRADE("DOWNGRADE"),
    POINTS_CHANGE("POINTS_CHANGE"),
    EXPIRE("EXPIRE"),
    RENEW("RENEW"),
    MANUAL_ADJUST("MANUAL_ADJUST"),
    ;

    private final String value;

    ChangeType(String value) {
        this.value = value;
    }
}
