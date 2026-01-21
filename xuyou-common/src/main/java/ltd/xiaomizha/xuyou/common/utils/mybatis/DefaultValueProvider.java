package ltd.xiaomizha.xuyou.common.utils.mybatis;

import ltd.xiaomizha.xuyou.common.enums.LogicFieldEnum;
import ltd.xiaomizha.xuyou.common.enums.TimeFieldEnum;
import ltd.xiaomizha.xuyou.common.enums.base.FieldBaseEnum;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 默认值提供器
 */
public class DefaultValueProvider {

    // 字段类型映射
    private static final Map<FieldBaseEnum, Class<?>> FIELD_TYPE_MAP = new HashMap<>();

    // 默认值映射
    private static final Map<FieldBaseEnum, Supplier<Object>> DEFAULT_VALUE_MAP = new HashMap<>();

    static {
        // 初始化字段类型映射
        initFieldTypeMap();

        // 初始化默认值映射
        initDefaultValueMap();
    }

    private DefaultValueProvider() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 初始化字段类型映射
     */
    private static void initFieldTypeMap() {
        // 时间字段类型
        for (TimeFieldEnum field : TimeFieldEnum.values()) {
            FIELD_TYPE_MAP.put(field, LocalDateTime.class);
        }

        // 逻辑字段类型
        FIELD_TYPE_MAP.put(LogicFieldEnum.DELETED, Boolean.class);
        FIELD_TYPE_MAP.put(LogicFieldEnum.ENABLED, Boolean.class);
        FIELD_TYPE_MAP.put(LogicFieldEnum.LOCKED, Boolean.class);
        FIELD_TYPE_MAP.put(LogicFieldEnum.VISIBLE, Boolean.class);
        FIELD_TYPE_MAP.put(LogicFieldEnum.VERSION, Integer.class);
        FIELD_TYPE_MAP.put(LogicFieldEnum.STATUS, Integer.class);
    }

    /**
     * 初始化默认值映射
     */
    private static void initDefaultValueMap() {
        // 时间字段默认值(当前时间)
        Supplier<Object> timeSupplier = LocalDateTime::now;
        for (TimeFieldEnum field : TimeFieldEnum.values()) {
            DEFAULT_VALUE_MAP.put(field, timeSupplier);
        }

        // 逻辑字段默认值
        DEFAULT_VALUE_MAP.put(LogicFieldEnum.DELETED, () -> Boolean.FALSE);
        DEFAULT_VALUE_MAP.put(LogicFieldEnum.ENABLED, () -> Boolean.TRUE);
        DEFAULT_VALUE_MAP.put(LogicFieldEnum.LOCKED, () -> Boolean.FALSE);
        DEFAULT_VALUE_MAP.put(LogicFieldEnum.VISIBLE, () -> Boolean.TRUE);
        DEFAULT_VALUE_MAP.put(LogicFieldEnum.VERSION, () -> 1);
        DEFAULT_VALUE_MAP.put(LogicFieldEnum.STATUS, () -> 0);
    }

    /**
     * 根据字段名获取默认值
     */
    @Nullable
    public static Object getDefaultValue(FieldBaseEnum fieldEnum) {
        Supplier<Object> supplier = DEFAULT_VALUE_MAP.get(fieldEnum);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * 根据字段名获取默认值
     */
    @Nullable
    public static Object getDefaultValue(String fieldName) {
        // 尝试查找时间字段
        for (TimeFieldEnum field : TimeFieldEnum.values()) {
            if (field.getFieldName().equals(fieldName)) {
                return getDefaultValue(field);
            }
        }

        // 尝试查找逻辑字段
        for (LogicFieldEnum field : LogicFieldEnum.values()) {
            if (field.getFieldName().equals(fieldName)) {
                return getDefaultValue(field);
            }
        }

        return null;
    }

    /**
     * 根据字段枚举获取字段类型
     */
    @Nullable
    public static Class<?> getFieldType(FieldBaseEnum fieldEnum) {
        return FIELD_TYPE_MAP.get(fieldEnum);
    }

    /**
     * 根据字段名获取字段类型
     */
    @Nullable
    public static Class<?> getFieldType(String fieldName) {
        // 尝试查找时间字段
        for (TimeFieldEnum field : TimeFieldEnum.values()) {
            if (field.getFieldName().equals(fieldName)) {
                return getFieldType(field);
            }
        }

        // 尝试查找逻辑字段
        for (LogicFieldEnum field : LogicFieldEnum.values()) {
            if (field.getFieldName().equals(fieldName)) {
                return getFieldType(field);
            }
        }

        return null;
    }

    /**
     * 是否为时间字段
     */
    public static boolean isTimeField(String fieldName) {
        for (TimeFieldEnum field : TimeFieldEnum.values()) {
            if (field.getFieldName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为逻辑字段
     */
    public static boolean isLogicField(String fieldName) {
        for (LogicFieldEnum field : LogicFieldEnum.values()) {
            if (field.getFieldName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有时间字段的默认值映射
     */
    public static Map<String, Object> getTimeFieldDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        for (TimeFieldEnum field : TimeFieldEnum.values()) {
            Object value = getDefaultValue(field);
            if (value != null) {
                defaults.put(field.getFieldName(), value);
            }
        }
        return defaults;
    }

    /**
     * 获取所有逻辑字段的默认值映射
     */
    public static Map<String, Object> getLogicFieldDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        for (LogicFieldEnum field : LogicFieldEnum.values()) {
            Object value = getDefaultValue(field);
            if (value != null) {
                defaults.put(field.getFieldName(), value);
            }
        }
        return defaults;
    }
}
