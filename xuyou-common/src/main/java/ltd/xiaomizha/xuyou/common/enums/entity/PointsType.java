package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 积分类型枚举
 */
@Getter
public enum PointsType {

    SIGN_IN("SIGN_IN"),
    TASK("TASK"),
    PURCHASE("PURCHASE"),
    CONSUME("CONSUME"),
    ADMIN_ADJUST("ADMIN_ADJUST"),
    REFUND("REFUND"),
    OTHER("OTHER"),
    DAILY("DAILY"),
    ONCE("ONCE"),
    EVERYTIME("EVERYTIME"),
    ;

    private final String value;

    PointsType(String value) {
        this.value = value;
    }
}
