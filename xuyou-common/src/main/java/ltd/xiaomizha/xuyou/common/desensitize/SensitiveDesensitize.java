package ltd.xiaomizha.xuyou.common.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 顶级脱敏注解
 * <p>
 * 所有自定义脱敏注解都需要使用此注解
 * <p>
 * 用于关联脱敏处理器和序列化器
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE) // 作用于注解上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留, 便于序列化器获取注解信息
@JacksonAnnotationsInside // 标记为Jackson注解的一部分, 确保Jackson能识别
@JsonSerialize(using = StringDesensitizeSerializer.class) // 指定自定义脱敏序列化器
public @interface SensitiveDesensitize {

    /**
     * 指定当前脱敏注解对应的处理器
     */
    @SuppressWarnings("rawtypes")
    Class<? extends DesensitizationHandler> handler();
}
