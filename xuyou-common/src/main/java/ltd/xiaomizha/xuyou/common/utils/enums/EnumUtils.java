package ltd.xiaomizha.xuyou.common.utils.enums;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 枚举工具类
 */
public class EnumUtils {

    /**
     * 根据value获取枚举常量
     *
     * @param enumClass 枚举类
     * @param value     枚举值
     * @param <E>       枚举类型
     * @return 枚举常量
     */
    public static <E extends Enum<E>> E getEnumByValue(Class<E> enumClass, String value) {
        if (enumClass == null || value == null) {
            return null;
        }

        try {
            // 获取枚举的所有常量
            E[] enumConstants = enumClass.getEnumConstants();
            if (enumConstants == null || enumConstants.length == 0) {
                return null;
            }

            // 获取value方法
            Method valueMethod = enumClass.getMethod("getValue");
            if (valueMethod == null) {
                return null;
            }

            // 遍历枚举常量, 匹配value
            for (E enumConstant : enumConstants) {
                String enumValue = (String) valueMethod.invoke(enumConstant);
                if (value.equals(enumValue)) {
                    return enumConstant;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据name获取枚举常量
     *
     * @param enumClass 枚举类
     * @param name      枚举名称
     * @param <E>       枚举类型
     * @return 枚举常量
     */
    public static <E extends Enum<E>> E getEnumByName(Class<E> enumClass, String name) {
        if (enumClass == null || name == null) {
            return null;
        }

        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 从字符串转换为枚举
     * <p>
     * fromValue(LoginType.class, LoginType::getValue, "password");
     *
     * @param enumClass      枚举类
     * @param valueExtractor 值提取函数
     * @param value          要匹配的值
     * @return 对应的枚举实例
     */
    public static <T, E extends Enum<E>> E fromValue(
            Class<E> enumClass,
            Function<E, T> valueExtractor,
            T value) {
        for (E enumConstant : enumClass.getEnumConstants()) {
            if (valueExtractor.apply(enumConstant).equals(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid enum value: " + value + " for enum class: " + enumClass.getSimpleName());
    }

}
