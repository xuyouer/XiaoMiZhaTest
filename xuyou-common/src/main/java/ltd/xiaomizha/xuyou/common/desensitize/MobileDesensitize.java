package ltd.xiaomizha.xuyou.common.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

/**
 * 手机号脱敏注解
 * <p>
 * 关联MobileDesensitization处理器
 * <p>
 * 默认规则: 保留前3位、后4位，中间用****替换
 */
@Documented
@Target({ElementType.FIELD}) // 作用于实体类字段
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@JacksonAnnotationsInside
@SensitiveDesensitize(handler = MobileDesensitization.class) // 指定对应的滑动脱敏处理器
public @interface MobileDesensitize {

    /**
     * 自定义保留前缀长度
     * <p>
     * 默认: 3
     */
    int prefixKeep() default 3;

    /**
     * 自定义保留后缀长度
     * <p>
     * 默认: 4
     */
    int suffixKeep() default 4;

    /**
     * 自定义掩码字符
     * <p>
     * 默认: *
     */
    String maskStr() default "*";

}
