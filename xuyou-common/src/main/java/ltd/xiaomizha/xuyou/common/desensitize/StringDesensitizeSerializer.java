package ltd.xiaomizha.xuyou.common.desensitize;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 自定义脱敏序列化器
 * <p>
 * JSON序列化时, 自动识别字段上的脱敏注解, 调用对应处理器执行脱敏
 */
@SuppressWarnings("rawtypes")
public class StringDesensitizeSerializer extends StdSerializer<String> implements ContextualSerializer {

    @Getter
    @Setter
    private DesensitizationHandler desensitizationHandler;

    protected StringDesensitizeSerializer() {
        super(String.class);
    }

    /**
     * 上下文序列化
     * <p>
     * 获取字段上的 @SensitiveDesensitize 注解, 关联对应的脱敏处理器
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) {
        if (beanProperty == null) {
            return this;
        }
        // 获取字段上的 @SensitiveDesensitize 注解
        SensitiveDesensitize annotation = beanProperty.getAnnotation(SensitiveDesensitize.class);
        if (annotation == null) {
            // 无脱敏注解, 返回默认序列化器, 不脱敏
            return this;
        }
        // 创建序列化器实例, 通过单例模式获取注解指定的处理器
        StringDesensitizeSerializer serializer = new StringDesensitizeSerializer();
        serializer.setDesensitizationHandler(Singleton.get(annotation.handler()));
        return serializer;
    }

    /**
     * 执行脱敏逻辑, 将脱敏后的值写入JSON
     */
    @Override
    @SuppressWarnings("unchecked")
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        // 处理null值或空字符串, 直接返回, 避免空指针
        if (StrUtil.isBlank(value)) {
            gen.writeNull();
            return;
        }
        // 获取当前序列化的字段
        Field field = getField(gen);
        if (field == null) {
            // 无法获取字段, 返回原始值
            gen.writeString(value);
            return;
        }
        // 获取字段上的所有 @SensitiveDesensitize 相关注解
        SensitiveDesensitize[] annotations = AnnotationUtil.getCombinationAnnotations(field, SensitiveDesensitize.class);
        if (ArrayUtil.isEmpty(annotations)) {
            gen.writeString(value);
            return;
        }
        for (Annotation annotation : field.getAnnotations()) {
            if (AnnotationUtil.hasAnnotation(annotation.annotationType(), SensitiveDesensitize.class)) {
                value = this.desensitizationHandler.desensitize(value, annotation);
                gen.writeString(value);
                return;
            }
        }
        gen.writeString(value);
    }

    /**
     * 获取当前序列化的字段
     *
     * @param generator JsonGenerator
     * @return 当前序列化的字段
     */
    private Field getField(JsonGenerator generator) {
        // 获取字段名
        String currentName = generator.getOutputContext().getCurrentName();
        // 获取当前序列化对象
        Object currentValue = generator.currentValue();
        if (currentValue == null) {
            return null;
        }
        // 获取对象类型
        Class<?> currentValueClass = currentValue.getClass();
        // 反射获取字段
        return ReflectUtil.getField(currentValueClass, currentName);
    }
}
