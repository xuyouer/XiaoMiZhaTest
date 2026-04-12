package ltd.xiaomizha.xuyou.common.desensitize;

import java.lang.annotation.Annotation;

/**
 * 脱敏处理器接口 (顶层规范)
 * <p>
 * 所有具体脱敏处理器 (手机号、邮箱等) 需实现此接口
 */
public interface DesensitizationHandler<T extends Annotation> {

    /**
     * 核心脱敏方法
     *
     * @param origin     原始敏感字符串
     * @param annotation 注解信息, 携带脱敏规则参数
     * @return 脱敏后的字符串
     */
    String desensitize(String origin, T annotation);

}
