package ltd.xiaomizha.xuyou.mail.service;

import ltd.xiaomizha.xuyou.common.response.ResponseResult;

public interface EmailCodeService {

    /**
     * 发送邮箱验证码
     * <p>
     * 生成6位数字验证码, 保存到Redis或数据库, 并发送邮件
     *
     * @param email 收件人邮箱地址
     * @param scene 使用场景: LOGIN-登录, REGISTER-注册, RESET_PASSWORD-重置密码, BIND-绑定
     * @return 发送结果 (包含脱敏邮箱、有效期等)
     */
    ResponseResult<?> sendVerificationCode(String email, String scene);

    /**
     * 校验邮箱验证码
     *
     * @param email 邮箱地址
     * @param code  用户输入的验证码
     * @param ip    客户端IP地址 (用于日志记录)
     * @return 校验结果
     */
    ResponseResult<?> verifyCode(String email, String code, String ip);

    /**
     * 查询验证码发送状态, 是否可以重新发送
     *
     * @param email 邮箱地址
     * @param scene 使用场景
     * @return 是否可重发及剩余冷却时间 (秒)
     */
    ResponseResult<?> checkSendStatus(String email, String scene);

}
