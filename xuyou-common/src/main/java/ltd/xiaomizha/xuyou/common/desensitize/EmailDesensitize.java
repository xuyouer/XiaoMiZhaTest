package ltd.xiaomizha.xuyou.common.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

/**
 * 邮箱脱敏注解
 * <p>
 * 关联 EmailDesensitizationHandler 处理器
 * <p>
 * 默认规则: 保留@前1位、@后全部保留, 中间用****替换
 */
@Documented
@Target({ElementType.FIELD}) // 作用于实体类字段
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@JacksonAnnotationsInside
@SensitiveDesensitize(handler = EmailDesensitizationHandler.class) // 指定对应的正则脱敏处理器
public @interface EmailDesensitize {

    /**
     * 匹配的正则表达式
     * <p>
     * 默认: 匹配@前除第一位外的所有字符
     */
    String regex() default "(^.)[^@]*(@.*$)";

    /**
     * 替换规则
     * <p>
     * 默认: 保留@前第一位, 中间用****替换, 保留@后所有字符
     * <p>
     * 示例: example@gmail.com → e****@gmail.com
     */
    String maskStr() default "$1****$2";

}
