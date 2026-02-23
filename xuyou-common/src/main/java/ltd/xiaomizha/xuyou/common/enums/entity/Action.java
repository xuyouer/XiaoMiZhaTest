package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

@Getter
public enum Action {

    ACTIVATE("ACTIVATE"),
    VALIDATE("VALIDATE"),
    CHECK("CHECK"),
    ;

    private final String value;

    Action(String value) {
        this.value = value;
    }
    
}
