package ltd.xiaomizha.xuyou.common.utils.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * MyBatis-Plus 元对象填充工具类
 */
@Slf4j
// @Configuration
public class MetaFillUtils {

    private static final Map<Class<?>, BiConsumer<MetaObjectHandler, FillContext>> FILL_MAP = new HashMap<>();

    static {
        // 注册已知类型的填充处理器
        FILL_MAP.put(LocalDateTime.class, (handler, ctx) -> {
            if (ctx.isUpdate) {
                handler.strictUpdateFill(ctx.metaObject, ctx.fieldName,
                        LocalDateTime.class, (LocalDateTime) ctx.value);
            } else {
                handler.strictInsertFill(ctx.metaObject, ctx.fieldName,
                        LocalDateTime.class, (LocalDateTime) ctx.value);
            }
        });

        FILL_MAP.put(Boolean.class, (handler, ctx) -> {
            if (ctx.isUpdate) {
                handler.strictUpdateFill(ctx.metaObject, ctx.fieldName,
                        Boolean.class, (Boolean) ctx.value);
            } else {
                handler.strictInsertFill(ctx.metaObject, ctx.fieldName,
                        Boolean.class, (Boolean) ctx.value);
            }
        });

        FILL_MAP.put(Integer.class, (handler, ctx) -> {
            if (ctx.isUpdate) {
                handler.strictUpdateFill(ctx.metaObject, ctx.fieldName,
                        Integer.class, (Integer) ctx.value);
            } else {
                handler.strictInsertFill(ctx.metaObject, ctx.fieldName,
                        Integer.class, (Integer) ctx.value);
            }
        });

        FILL_MAP.put(Long.class, (handler, ctx) -> {
            if (ctx.isUpdate) {
                handler.strictUpdateFill(ctx.metaObject, ctx.fieldName,
                        Long.class, (Long) ctx.value);
            } else {
                handler.strictInsertFill(ctx.metaObject, ctx.fieldName,
                        Long.class, (Long) ctx.value);
            }
        });

        FILL_MAP.put(String.class, (handler, ctx) -> {
            if (ctx.isUpdate) {
                handler.strictUpdateFill(ctx.metaObject, ctx.fieldName,
                        String.class, (String) ctx.value);
            } else {
                handler.strictInsertFill(ctx.metaObject, ctx.fieldName,
                        String.class, (String) ctx.value);
            }
        });
    }

    private MetaFillUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    // @PostConstruct
    // public void init() {
    //     // 注册 Double 类型
    //     MetaFillUtils.registerHandler(Double.class, (handler, ctx) -> {
    //         if (ctx.isUpdate()) {
    //             handler.strictUpdateFill(ctx.getMetaObject(), ctx.getFieldName(),
    //                     Double.class, (Double) ctx.getValue());
    //         } else {
    //             handler.strictInsertFill(ctx.getMetaObject(), ctx.getFieldName(),
    //                     Double.class, (Double) ctx.getValue());
    //         }
    //     });
    //
    //     // 注册 BigDecimal 类型
    //     MetaFillUtils.registerHandler(BigDecimal.class, (handler, ctx) -> {
    //         if (ctx.isUpdate()) {
    //             handler.strictUpdateFill(ctx.getMetaObject(), ctx.getFieldName(),
    //                     BigDecimal.class, (BigDecimal) ctx.getValue());
    //         } else {
    //             handler.strictInsertFill(ctx.getMetaObject(), ctx.getFieldName(),
    //                     BigDecimal.class, (BigDecimal) ctx.getValue());
    //         }
    //     });
    // }

    /**
     * 注册自定义类型的填充处理器
     */
    public static void registerHandler(Class<?> clazz, BiConsumer<MetaObjectHandler, FillContext> handler) {
        FILL_MAP.put(clazz, handler);
    }

    /**
     * 执行填充
     */
    public static void doFill(MetaObjectHandler handler, MetaObject metaObject,
                              String fieldName, Object value,
                              Class<?> fieldType, boolean isUpdate) {
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }

        FillContext context = new FillContext(metaObject, fieldName, value, fieldType, isUpdate);
        BiConsumer<MetaObjectHandler, FillContext> fillHandler = FILL_MAP.get(fieldType);

        if (fillHandler != null) {
            fillHandler.accept(handler, context);
        } else {
            // 对于未注册的类型, 使用直接设置值的方式
            metaObject.setValue(fieldName, value);
        }
    }

    /**
     * 填充上下文
     */
    public static class FillContext {
        private final MetaObject metaObject;
        private final String fieldName;
        private final Object value;
        private final Class<?> fieldType;
        private final boolean isUpdate;

        public FillContext(MetaObject metaObject, String fieldName,
                           Object value, Class<?> fieldType, boolean isUpdate) {
            this.metaObject = metaObject;
            this.fieldName = fieldName;
            this.value = value;
            this.fieldType = fieldType;
            this.isUpdate = isUpdate;
        }

        public MetaObject getMetaObject() {
            return metaObject;
        }

        public String getFieldName() {
            return fieldName;
        }

        @Nullable
        public Object getValue() {
            return value;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }

        public boolean isUpdate() {
            return isUpdate;
        }
    }
}
