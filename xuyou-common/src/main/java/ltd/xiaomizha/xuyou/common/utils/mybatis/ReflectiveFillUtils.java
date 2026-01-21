package ltd.xiaomizha.xuyou.common.utils.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射方式调用填充方法
 */
@Slf4j
public class ReflectiveFillUtils {

    private static final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();

    private ReflectiveFillUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 通过反射调用填充方法
     */
    public static void fillByReflection(MetaObjectHandler handler, MetaObject metaObject,
                                        String fieldName, Object value,
                                        Class<?> fieldType, boolean isUpdate) {
        try {
            String methodName = isUpdate ? "strictUpdateFill" : "strictInsertFill";
            String cacheKey = methodName + ":" + fieldType.getName();

            Method method = METHOD_CACHE.computeIfAbsent(cacheKey, key -> {
                try {
                    return MetaObjectHandler.class.getMethod(methodName,
                            MetaObject.class,
                            String.class,
                            Class.class,
                            Object.class);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            });

            if (method != null) {
                method.setAccessible(true);
                method.invoke(handler, metaObject, fieldName, fieldType, value);
            } else {
                // 方法未找到, 使用直接设置值的方式
                metaObject.setValue(fieldName, value);
            }
        } catch (Exception e) {
            // 反射失败, 使用直接设置值的方式
            metaObject.setValue(fieldName, value);
        }
    }
}
