package ltd.xiaomizha.xuyou.common.enums.scan;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扫码登录状态枚举
 */
@Getter
@AllArgsConstructor
public enum ScanLoginStatus {

    /**
     * 未扫码
     */
    NOT_SCAN(0, "未扫码"),

    /**
     * 已扫码, 待确认
     */
    SCANNED(1, "已扫码, 待确认"),

    /**
     * 已确认登录
     */
    CONFIRMED(2, "已确认登录"),

    /**
     * 二维码已过期
     */
    EXPIRED(3, "二维码已过期");

    private final Integer code;

    private final String desc;

    /**
     * 根据 code 获取枚举对象
     *
     * @param code 状态码
     * @return 对应的枚举, 未找到返回 NOT_SCAN
     */
    public static ScanLoginStatus getByCode(Integer code) {
        if (code == null) {
            return NOT_SCAN;
        }
        for (ScanLoginStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NOT_SCAN;
    }

}
