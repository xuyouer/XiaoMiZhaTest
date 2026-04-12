package ltd.xiaomizha.xuyou.common.desensitize;

import java.lang.annotation.Annotation;

/**
 * 正则表达式脱敏处理器抽象类
 * <p>
 * 适用于: 邮箱、银行卡号等需要正则匹配的场景
 */
public abstract class AbstractRegexDesensitizationHandler<T extends Annotation> implements DesensitizationHandler<T> {

    @Override
    public String desensitize(String origin, T annotation) {
        String regex = getRegex(annotation);
        String replacer = getMaskStr(annotation);
        return origin.replaceAll(regex, replacer);
    }

    /**
     * 获取注解中的正则表达式
     *
     * @param annotation 注解信息
     * @return 正则表达式
     */
    abstract String getRegex(T annotation);

    /**
     * 获取注解中的掩码字符串
     *
     * @param annotation 注解信息
     * @return 用于替换的掩码字符串
     */
    abstract String getMaskStr(T annotation);

}
