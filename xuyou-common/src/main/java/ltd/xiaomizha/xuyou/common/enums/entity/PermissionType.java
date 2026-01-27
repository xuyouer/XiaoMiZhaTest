package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 权限类型枚举
 */
@Getter
public enum PermissionType {

    READ("READ"),
    WRITE("WRITE"),
    DELETE("DELETE"),
    EXECUTE("EXECUTE"),
    MANAGE("MANAGE"),
    ALL("ALL"),
    ;

    private final String value;

    PermissionType(String value) {
        this.value = value;
    }
}
