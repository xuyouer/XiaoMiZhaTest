package ltd.xiaomizha.xuyou.common.utils.validation;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * <p>
 * 提供常用的数据验证方法
 */
public class ValidationUtils {
    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
    );
    /**
     * 手机号正则表达式（中国大陆）
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱地址
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证手机号格式（中国大陆）
     *
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证字符串长度
     *
     * @param str    字符串
     * @param minLen 最小长度
     * @param maxLen 最大长度
     * @return 是否有效
     */
    public static boolean isValidLength(String str, int minLen, int maxLen) {
        if (str == null) {
            return minLen <= 0;
        }
        int len = str.length();
        return len >= minLen && len <= maxLen;
    }

    /**
     * 验证字符串不为空
     *
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * 验证整数ID是否有效
     *
     * @param id ID
     * @return 是否有效
     */
    public static boolean isValidId(Integer id) {
        return id != null && id > 0;
    }

    /**
     * 验证长整型ID是否有效
     *
     * @param id ID
     * @return 是否有效
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * 验证对象不为空
     *
     * @param obj 对象
     * @return 是否不为空
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 验证邮箱格式，如果无效则抛出异常
     *
     * @param email 邮箱地址
     * @throws IllegalArgumentException 如果邮箱格式无效
     */
    public static void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }

    /**
     * 验证手机号格式，如果无效则抛出异常
     *
     * @param phone 手机号
     * @throws IllegalArgumentException 如果手机号格式无效
     */
    public static void validatePhone(String phone) {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
    }

    /**
     * 验证字符串长度，如果无效则抛出异常
     *
     * @param str       字符串
     * @param minLen    最小长度
     * @param maxLen    最大长度
     * @param fieldName 字段名称
     * @throws IllegalArgumentException 如果长度无效
     */
    public static void validateLength(String str, int minLen, int maxLen, String fieldName) {
        if (!isValidLength(str, minLen, maxLen)) {
            throw new IllegalArgumentException(
                    String.format("%s长度必须在%d到%d个字符之间", fieldName, minLen, maxLen)
            );
        }
    }

    /**
     * 验证字符串不为空，如果为空则抛出异常
     *
     * @param str       字符串
     * @param fieldName 字段名称
     * @throws IllegalArgumentException 如果字符串为空
     */
    public static void validateNotEmpty(String str, String fieldName) {
        if (!isNotEmpty(str)) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }

    /**
     * 验证ID是否有效，如果无效则抛出异常
     *
     * @param id        ID
     * @param fieldName 字段名称
     * @throws IllegalArgumentException 如果ID无效
     */
    public static void validateId(Integer id, String fieldName) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException(fieldName + "不能为空且必须大于0");
        }
    }

    /**
     * 验证对象不为空，如果为空则抛出异常
     *
     * @param obj       对象
     * @param fieldName 字段名称
     * @throws IllegalArgumentException 如果对象为空
     */
    public static void validateNotNull(Object obj, String fieldName) {
        if (!isNotNull(obj)) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }
}
