package ltd.xiaomizha.xuyou.common.constant;

import java.time.format.DateTimeFormatter;

/**
 * 日期相关常量
 */
public final class DateConstant {
    
    public DateConstant() {
    }

    /**
     * 日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 日期格式：yyyy-MM-dd
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 日期格式：yyyy-MM
     */
    public static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 日期格式：yyyyMM
     */
    public static final DateTimeFormatter MONTH_FORMATTER_NU = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 时间格式：HH:mm:ss
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

}
