package ltd.xiaomizha.xuyou.common.utils.date;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日期时间工具类
 */
public class DateUtils {

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前日期时间
     *
     * @return 当前日期时间
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取今天的开始时间
     *
     * @return 今天的开始时间
     */
    public static LocalDateTime getStartOfDay() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * 获取今天的结束时间
     *
     * @return 今天的结束时间
     */
    public static LocalDateTime getEndOfDay() {
        return LocalDate.now().atTime(23, 59, 59, 999999999);
    }

    /**
     * 获取指定日期的开始时间
     *
     * @param date 指定日期
     * @return 指定日期的开始时间
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 获取指定日期的结束时间
     *
     * @param date 指定日期
     * @return 指定日期的结束时间
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59, 999999999);
    }

    /**
     * 获取本月的开始时间
     *
     * @return 本月的开始时间
     */
    public static LocalDateTime getStartOfMonth() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        return firstDayOfMonth.atStartOfDay();
    }

    /**
     * 获取本月的结束时间
     *
     * @return 本月的结束时间
     */
    public static LocalDateTime getEndOfMonth() {
        LocalDate now = LocalDate.now();
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        return lastDayOfMonth.atTime(23, 59, 59, 999999999);
    }

    /**
     * 获取指定月份的开始时间
     *
     * @param year  年份
     * @param month 月份
     * @return 指定月份的开始时间
     */
    public static LocalDateTime getStartOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).atStartOfDay();
    }

    /**
     * 获取指定月份的结束时间
     *
     * @param year  年份
     * @param month 月份
     * @return 指定月份的结束时间
     */
    public static LocalDateTime getEndOfMonth(int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        return lastDayOfMonth.atTime(23, 59, 59, 999999999);
    }

}
