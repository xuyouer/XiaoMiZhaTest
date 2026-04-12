package ltd.xiaomizha.xuyou.sms.service;

import ltd.xiaomizha.xuyou.common.response.ResponseResult;

/**
 * 短信服务
 */
public interface SmsService {

    /**
     * 发送短信验证码
     * <p>
     * 生成6位随机验证码, 通过短信发送给用户,
     * 同时将验证码存入 Redis (5分钟过期)
     *
     * @param phone 手机号
     * @return 发送结果, 包含验证码
     */
    ResponseResult<Void> sendVerificationCode(String phone);

    /**
     * 校验短信验证码
     * <p>
     * 从 Redis 获取存储的验证码, 与用户输入的验证码进行比对
     *
     * @param phone 手机号
     * @param code  用户输入的验证码
     * @return 校验结果
     */
    ResponseResult<Void> verifyCode(String phone, String code);

    /**
     * 发送自定义短信内容
     *
     * @param phone   手机号
     * @param content 短信内容 (需符合模板变量)
     * @param args    模板参数
     * @return 发送结果
     */
    ResponseResult<Void> sendCustomMessage(String phone, String content, String... args);
}
