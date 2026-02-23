package ltd.xiaomizha.xuyou.common.enums.entity;

import lombok.Getter;

@Getter
public enum LicenseType {

    TRIAL("TRIAL", "试用版"),
    BASIC("BASIC", "基础版"),
    PREMIUM("PREMIUM", "高级版"),
    STANDARD("STANDARD", "标准版"),
    PROFESSIONAL("PROFESSIONAL", "专业版"),
    ENTERPRISE("ENTERPRISE", "企业版"),
    CUSTOM("CUSTOM", "定制版"),
    ;

    private final String value;
    private final String description;

    LicenseType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举
     */
    public static LicenseType getByValue(String value) {
        for (LicenseType type : LicenseType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
