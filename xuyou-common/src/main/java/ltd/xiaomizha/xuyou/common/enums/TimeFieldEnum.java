package ltd.xiaomizha.xuyou.common.enums;

import lombok.Getter;
import ltd.xiaomizha.xuyou.common.enums.base.FieldBaseEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 时间字段枚举
 */
@Getter
public enum TimeFieldEnum implements FieldBaseEnum {

    CREATE_AT("createdAt", "创建时间"),
    UPDATE_AT("updatedAt", "更新时间"),
    CREATE_TIME("createTime", "创建时间"),
    UPDATE_TIME("updateTime", "更新时间"),
    DELETE_TIME("deleteTime", "删除时间"),
    EXPIRE_TIME("expireTime", "过期时间");

    private final String fieldName;
    private final String description;

    TimeFieldEnum(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }

    /**
     * 是否为创建时间字段
     */
    public boolean isCreateField() {
        return this == CREATE_AT || this == CREATE_TIME;
    }

    /**
     * 是否为更新时间字段
     */
    public boolean isUpdateField() {
        return this == UPDATE_AT || this == UPDATE_TIME;
    }

    /**
     * 获取所有创建时间字段枚举
     */
    public static List<TimeFieldEnum> createTimeFields() {
        return Arrays.stream(values())
                .filter(TimeFieldEnum::isCreateField)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有更新时间字段枚举
     */
    public static List<TimeFieldEnum> updateTimeFields() {
        return Arrays.stream(values())
                .filter(TimeFieldEnum::isUpdateField)
                .collect(Collectors.toList());
    }
}