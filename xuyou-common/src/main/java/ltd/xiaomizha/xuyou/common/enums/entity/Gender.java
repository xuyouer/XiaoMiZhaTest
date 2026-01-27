package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
public enum Gender {

    MALE("MALE"),
    FEMALE("FEMALE"),
    OTHER("OTHER"),
    UNKNOWN("UNKNOWN"),
    ;

    private final String value;

    Gender(String value) {
        this.value = value;
    }
}
