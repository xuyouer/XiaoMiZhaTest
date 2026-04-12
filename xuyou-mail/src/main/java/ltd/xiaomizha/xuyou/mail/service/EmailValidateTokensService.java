package ltd.xiaomizha.xuyou.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.mail.entity.EmailValidateTokens;

/**
 * @author xiaom
 * @description 针对表【email_validate_tokens(邮箱验证Token记录表)】的数据库操作Service
 * @createDate 2026-04-05 19:08:18
 */
public interface EmailValidateTokensService extends IService<EmailValidateTokens> {

    /**
     * 发送邮箱验证邮件
     * <p>
     * 包含: 参数校验、Token生成、记录保存、邮件发送、历史记录
     *
     * @param userId       用户ID
     * @param email        邮箱地址
     * @param validateType 验证类型: REGISTER-注册, RESET-重置密码, BIND-绑定
     * @return 验证结果DTO, 包含脱敏邮箱、有效期等
     */
    ResponseResult<?> sendValidationEmail(Long userId, String email, TokenType validateType);

    /**
     * 校验邮箱验证Token
     *
     * @param token 验证Token
     * @param ip    客户端IP地址, 用于记录日志
     * @return 校验结果
     */
    ResponseResult<?> validateToken(String token, String ip);

    /**
     * 查询用户邮箱验证状态
     *
     * @param userId 用户ID
     * @param email  邮箱地址
     * @return 是否已验证
     */
    ResponseResult<?> checkEmailValidated(Long userId, String email);

    /**
     * 重新发送验证邮件
     *
     * @param userId 用户ID
     * @param email  邮箱地址
     * @return 重发结果
     */
    ResponseResult<?> resendValidationEmail(Long userId, String email);

    /**
     * 撤销未使用的验证Token
     *
     * @param token Token值
     * @return 撤销结果
     */
    ResponseResult<?> revokeToken(String token);

}
