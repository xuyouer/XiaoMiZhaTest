package ltd.xiaomizha.xuyou.common.enums;

import lombok.Getter;
import ltd.xiaomizha.xuyou.common.enums.base.FieldBaseEnum;

/**
 * 逻辑字段枚举
 */
@Getter
public enum LogicFieldEnum implements FieldBaseEnum {

    DELETED("deleted", "删除标志"),
    STATUS("status", "状态标志"),
    VERSION("version", "版本标志"),
    ENABLED("enabled", "启用标志"),
    LOCKED("locked", "锁定标志"),
    VISIBLE("visible", "可见标志");

    private final String fieldName;
    private final String description;

    LogicFieldEnum(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }
}