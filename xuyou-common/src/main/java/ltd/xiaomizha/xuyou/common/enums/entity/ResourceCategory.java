package ltd.xiaomizha.xuyou.common.enums.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
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
    
    @JsonValue
    @EnumValue
    private final String value;

    ResourceCategory(String value) {
        this.value = value;
    }
}
