package ltd.xiaomizha.xuyou.common.desensitize;

import java.lang.annotation.Annotation;

/**
 * 滑动脱敏抽象类
 * <p>
 * 适用于: 手机号、身份证号、姓名等需要保留前后缀的场景
 */
public abstract class AbstractSliderDesensitizationHandler<T extends Annotation> implements DesensitizationHandler<T> {

    @Override
    public String desensitize(String origin, T annotation) {
        // 从注解中获取脱敏参数, 前缀保留长度、后缀保留长度、掩码字符
        int prefixKeep = getPrefixKeep(annotation);
        int suffixKeep = getSuffixKeep(annotation);
        String replacer = getMaskStr(annotation);
        int length = origin.length();
        int interval = length - prefixKeep - suffixKeep;

        // 原始字符串长度小于等于前后缀保留总长度, 全部用掩码替换
        if (interval <= 0) {
            return buildReplacerByLength(replacer, length);
        }

        // 原始字符串长度大于前后缀保留总长度, 替换中间部分
        return origin.substring(0, prefixKeep) + buildReplacerByLength(replacer, interval) + origin.substring(prefixKeep + interval);
    }

    /**
     * 根据长度循环构建掩码字符串
     *
     * @param replacer 单个掩码字符, 如: *、#
     * @param length   需要构建的掩码长度
     * @return 完整掩码字符串
     */
    private String buildReplacerByLength(String replacer, int length) {
        return replacer.repeat(length);
    }

    /**
     * 获取注解中的前缀保留长度
     *
     * @param annotation 注解信息
     * @return 前缀保留长度
     */
    abstract Integer getPrefixKeep(T annotation);

    /**
     * 获取注解中的后缀保留长度
     *
     * @param annotation 注解信息
     * @return 后缀保留长度
     */
    abstract Integer getSuffixKeep(T annotation);

    /**
     * 获取注解中的掩码字符
     *
     * @param annotation 注解信息
     * @return 掩码字符
     */
    abstract String getMaskStr(T annotation);

}
