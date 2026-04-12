package ltd.xiaomizha.xuyou.common.desensitize;

/**
 * 手机号脱敏处理器, 继承滑动脱敏抽象类
 * <p>
 * 关联 @MobileDesensitize 注解, 实现抽象方法, 获取注解中的脱敏参数
 */
public class MobileDesensitization extends AbstractSliderDesensitizationHandler<MobileDesensitize> {

    /**
     * 获取 @MobileDesensitize 注解中的前缀保留长度
     *
     * @param annotation 注解信息
     * @return
     */
    @Override
    Integer getPrefixKeep(MobileDesensitize annotation) {
        return annotation.prefixKeep();
    }

    /**
     * 获取 @MobileDesensitize 注解中的后缀保留长度
     *
     * @param annotation 注解信息
     * @return
     */
    @Override
    Integer getSuffixKeep(MobileDesensitize annotation) {
        return annotation.suffixKeep();
    }

    /**
     * 获取 @MobileDesensitize 注解中的掩码字符
     *
     * @param annotation 注解信息
     * @return
     */
    @Override
    String getMaskStr(MobileDesensitize annotation) {
        return annotation.maskStr();
    }

}
