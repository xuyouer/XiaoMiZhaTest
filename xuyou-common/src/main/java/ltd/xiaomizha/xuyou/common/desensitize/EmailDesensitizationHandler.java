package ltd.xiaomizha.xuyou.common.desensitize;

/**
 * 邮箱脱敏处理器, 继承正则脱敏抽象类
 * <p>
 * 关联 @EmailDesensitize 注解, 实现抽象方法, 获取注解中的脱敏参数
 */
public class EmailDesensitizationHandler extends AbstractRegexDesensitizationHandler<EmailDesensitize> {

    /**
     * 获取 @EmailDesensitize 注解中的正则表达式
     *
     * @param annotation 注解信息
     * @return
     */
    @Override
    String getRegex(EmailDesensitize annotation) {
        return annotation.regex();
    }

    /**
     * 获取 @EmailDesensitize 注解中的掩码字符串
     *
     * @param annotation 注解信息
     * @return
     */
    @Override
    String getMaskStr(EmailDesensitize annotation) {
        return annotation.maskStr();
    }

}
