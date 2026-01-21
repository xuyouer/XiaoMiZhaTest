package ltd.xiaomizha.xuyou.common.utils.mybatis;

import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.LogicFieldEnum;
import ltd.xiaomizha.xuyou.common.enums.TimeFieldEnum;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字段工具类
 */
@Slf4j
public class FieldUtils {

    private FieldUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 获取所有时间字段名
     */
    @NonNull
    public static Set<String> getAllTimeFieldNames() {
        return Arrays.stream(TimeFieldEnum.values())
                .map(TimeFieldEnum::getFieldName)
                .collect(Collectors.toSet());
    }

    /**
     * 获取所有逻辑字段名
     */
    @NonNull
    public static Set<String> getAllLogicFieldNames() {
        return Arrays.stream(LogicFieldEnum.values())
                .map(LogicFieldEnum::getFieldName)
                .collect(Collectors.toSet());
    }

    /**
     * 检查对象是否包含特定字段
     */
    public static boolean hasField(@NonNull MetaObject metaObject, @NonNull String fieldName) {
        return metaObject.hasSetter(fieldName);
    }

    /**
     * 检查对象是否包含时间字段
     */
    public static boolean hasTimeField(@NonNull MetaObject metaObject) {
        return getAllTimeFieldNames().stream()
                .anyMatch(metaObject::hasSetter);
    }

    /**
     * 检查对象是否包含逻辑字段
     */
    public static boolean hasLogicField(@NonNull MetaObject metaObject) {
        return getAllLogicFieldNames().stream()
                .anyMatch(metaObject::hasSetter);
    }

    /**
     * 获取对象中存在的所有时间字段
     */
    @NonNull
    public static List<String> getExistingTimeFields(@NonNull MetaObject metaObject) {
        return getAllTimeFieldNames().stream()
                .filter(metaObject::hasSetter)
                .collect(Collectors.toList());
    }

    /**
     * 获取对象中存在的所有逻辑字段
     */
    @NonNull
    public static List<String> getExistingLogicFields(@NonNull MetaObject metaObject) {
        return getAllLogicFieldNames().stream()
                .filter(metaObject::hasSetter)
                .collect(Collectors.toList());
    }

    /**
     * 是否为创建时间字段
     */
    public static boolean isCreateTimeField(@NonNull String fieldName) {
        return fieldName.equals(TimeFieldEnum.CREATE_AT.getFieldName()) ||
                fieldName.equals(TimeFieldEnum.CREATE_TIME.getFieldName());
    }

    /**
     * 是否为更新时间字段
     */
    public static boolean isUpdateTimeField(@NonNull String fieldName) {
        return fieldName.equals(TimeFieldEnum.UPDATE_AT.getFieldName()) ||
                fieldName.equals(TimeFieldEnum.UPDATE_TIME.getFieldName());
    }
}
