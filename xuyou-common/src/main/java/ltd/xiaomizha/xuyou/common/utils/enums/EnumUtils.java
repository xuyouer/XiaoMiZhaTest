package ltd.xiaomizha.xuyou.common.utils.enums;

import java.util.function.Function;

/**
 * 枚举工具类
 */
public class EnumUtils {

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
