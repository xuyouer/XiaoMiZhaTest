package ltd.xiaomizha.xuyou.common.debounce.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 接口防抖注解
 * <p>
 * 用于标记需要做防抖的接口
 */
@Target(ElementType.METHOD) // 只作用于方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时生效, 允许反射获取
@Documented // 生成JavaDoc时会包含该注解
public @interface InterfaceDebounce {

    /**
     * 防抖时间窗口, 默认: 5s
     */
    long timeout() default 5;

    /**
     * 时间单位, 默认: s
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 防抖提示信息
     */
    String message() default "操作过于频繁, 请稍后再试";

    /**
     * 防抖唯一标识Key, SpEL表达式数组
     */
    String[] key() default {};

}
