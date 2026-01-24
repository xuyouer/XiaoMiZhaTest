package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

/**
 * 资源类型枚举
 */
@Getter
public enum ResourceCategory {

    CATALOG("CATALOG"),
    MENU("MENU"),
    BUTTON("BUTTON"),
    API("API"),
    PAGE("PAGE"),
    MODULE("MODULE"),
    OTHER("OTHER"),
    ;

    private final String value;

    ResourceCategory(String value) {
        this.value = value;
    }
}
