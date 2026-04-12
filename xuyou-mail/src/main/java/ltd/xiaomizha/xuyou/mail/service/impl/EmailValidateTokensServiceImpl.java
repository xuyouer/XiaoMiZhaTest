package ltd.xiaomizha.xuyou.mail.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.Action;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;
import ltd.xiaomizha.xuyou.common.response.ResponseBuilder;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.email.EmailSendUtil;
import ltd.xiaomizha.xuyou.common.utils.email.EmailValidateUtil;
import ltd.xiaomizha.xuyou.mail.dto.EmailValidateRequestDTO;
import ltd.xiaomizha.xuyou.mail.dto.EmailValidateResponseDTO;
import ltd.xiaomizha.xuyou.mail.entity.EmailValidateTokens;
import ltd.xiaomizha.xuyou.mail.mapper.EmailValidateTokensMapper;
import ltd.xiaomizha.xuyou.mail.service.EmailValidateHistoryService;
import ltd.xiaomizha.xuyou.mail.service.EmailValidateTokensService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author xiaom
 * @description 针对表【email_validate_tokens(邮箱验证Token记录表)】的数据库操作Service实现
 * @createDate 2026-04-05 19:08:18
 */
@Slf4j
@Service
public class EmailValidateTokensServiceImpl extends ServiceImpl<EmailValidateTokensMapper, EmailValidateTokens>
        implements EmailValidateTokensService {

    @Resource
    private EmailSendUtil emailSendUtil;

    @Resource
    private EmailValidateUtil emailValidateUtil;

    @Resource
    private EmailValidateTokensMapper emailValidateTokensMapper;

    @Resource
    private EmailValidateHistoryService emailValidateHistoryService;

    @Lazy
    @Resource
    private EmailValidateTokensService self;

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<?> sendValidationEmail(Long userId, String email, TokenType validateType) {
        try {
            log.info("开始发送邮箱验证邮件: userId={}, email={}, type={}", userId, email, validateType);

            EmailValidateRequestDTO requestDTO = buildAndValidateRequest(userId, email, validateType);
            String token = emailValidateUtil.generateValidateToken();
            LocalDateTime expireTime = emailValidateUtil.calculateExpireTime();
            log.info("生成邮箱验证Token: userId={}, email={}, token={}, expireTime={}", userId, email, token, expireTime);

            saveTokenRecord(requestDTO, token, expireTime);
            emailSendUtil.sendValidationEmail(requestDTO.getEmail(), token);

            String maskedEmail = emailValidateUtil.maskEmail(email);
            Integer expireMinutes = emailValidateUtil.getValidateExpireMinutes();
            saveHistory(userId, email, Action.SEND, validateType, true, null, null);

            log.info("邮箱验证邮件发送成功: userId={}, email={}", userId, maskedEmail);

            EmailValidateResponseDTO responseDTO = EmailValidateResponseDTO.success(
                    "验证邮件已发送成功，请查收（包括垃圾邮件箱）",
                    maskedEmail,
                    expireMinutes
            );

            return ResponseResult.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return ResponseBuilder.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("发送邮箱验证邮件失败: userId={}, email={}", userId, email, e);
            saveHistory(userId, email, Action.SEND, validateType, false, e.getMessage(), null);
            return ResponseBuilder.error("发送验证邮件失败: " + e.getMessage());
        }
    }

    /**
     * 校验邮箱验证Token
     *
     * @param token 验证Token
     * @param ip    客户端IP地址, 用于记录日志
     * @return 校验结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<?> validateToken(String token, String ip) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseBuilder.error("Token不能为空");
            }

            log.info("开始校验邮箱验证Token: token={}, ip={}", token, ip);

            // 查询Token记录
            LambdaQueryWrapper<EmailValidateTokens> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmailValidateTokens::getToken, token);
            EmailValidateTokens tokenRecord = emailValidateTokensMapper.selectOne(queryWrapper);

            if (tokenRecord == null) {
                // Token不存在
                saveHistory(null, "", Action.VALIDATE, null, false, "Token不存在", ip);
                return ResponseBuilder.error("验证链接无效或已过期，请重新申请验证");
            }

            Long userId = tokenRecord.getUserId();
            String email = tokenRecord.getEmail();
            TokenType tokenType = tokenRecord.getTokenType();
            Integer status = tokenRecord.getStatus();
            LocalDateTime expireAt = tokenRecord.getExpireAt();

            // 状态校验
            switch (status) {
                case 1:
                    // 已验证
                    saveHistory(userId, email, Action.VALIDATE, tokenType, false, "Token已被使用", ip);
                    return ResponseBuilder.error("该邮箱已完成验证，无需重复操作");

                case 3:
                    // 已撤销
                    saveHistory(userId, email, Action.VALIDATE, tokenType, false, "Token已被撤销", ip);
                    return ResponseBuilder.error("验证链接已被撤销，请重新申请");

                default:
                    break;
            }

            // 过期校验
            if (expireAt.isBefore(LocalDateTime.now())) {
                // 更新状态为已过期
                tokenRecord.setStatus(2);
                emailValidateTokensMapper.updateById(tokenRecord);
                saveHistory(userId, email, Action.VALIDATE, tokenType, false, "Token已过期", ip);
                return ResponseBuilder.error("验证链接已过期，请重新申请验证");
            }

            // 验证成功 - 更新状态
            tokenRecord.setStatus(1); // 已验证
            tokenRecord.setValidatedAt(LocalDateTime.now());
            emailValidateTokensMapper.updateById(tokenRecord);

            saveHistory(userId, email, Action.VALIDATE, tokenType, true, null, ip);

            log.info("邮箱验证成功: userId={}, email={}", userId, email);

            return ResponseResult.ok("邮箱验证成功，账号已激活");
        } catch (Exception e) {
            log.error("校验Token异常: token={}", token, e);
            return ResponseBuilder.error("验证过程出现异常: " + e.getMessage());
        }
    }

    /**
     * 查询用户邮箱验证状态
     *
     * @param userId 用户ID
     * @param email  邮箱地址
     * @return 是否已验证
     */
    @Override
    public ResponseResult<?> checkEmailValidated(Long userId, String email) {
        try {
            LambdaQueryWrapper<EmailValidateTokens> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmailValidateTokens::getUserId, userId)
                    .eq(EmailValidateTokens::getEmail, email)
                    .eq(EmailValidateTokens::getStatus, 1); // 已验证

            Long count = emailValidateTokensMapper.selectCount(queryWrapper);
            boolean isValidated = count != null && count > 0;

            return ResponseResult.successWithMessage(
                    isValidated ? "该邮箱已完成验证" : "该邮箱尚未验证",
                    isValidated
            );
        } catch (Exception e) {
            log.error("查询邮箱验证状态失败: userId={}, email={}", userId, email, e);
            return ResponseBuilder.error("查询验证状态失败: " + e.getMessage());
        }
    }

    /**
     * 重新发送验证邮件
     *
     * @param userId 用户ID
     * @param email  邮箱地址
     * @return 重发结果
     */
    @Override
    public ResponseResult<?> resendValidationEmail(Long userId, String email) {
        return self.sendValidationEmail(userId, email, TokenType.REGISTER);
    }

    /**
     * 撤销未使用的验证Token
     *
     * @param token Token值
     * @return 撤销结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<?> revokeToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseBuilder.error("Token不能为空");
            }

            // 查询Token
            LambdaQueryWrapper<EmailValidateTokens> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EmailValidateTokens::getToken, token);
            EmailValidateTokens tokenRecord = emailValidateTokensMapper.selectOne(queryWrapper);

            if (tokenRecord == null) {
                return ResponseBuilder.error("Token不存在");
            }

            if (tokenRecord.getStatus() != 0) {
                return ResponseBuilder.error("该Token已无法撤销（当前状态：" +
                        (tokenRecord.getStatus() == 1 ? "已验证" : tokenRecord.getStatus() == 2 ? "已过期" : "已撤销") + "）");
            }

            // 更新为已撤销状态
            tokenRecord.setStatus(3); // 已撤销
            tokenRecord.setRevokedAt(LocalDateTime.now());
            emailValidateTokensMapper.updateById(tokenRecord);

            saveHistory(tokenRecord.getUserId(), tokenRecord.getEmail(), Action.REVOKE, tokenRecord.getTokenType(), true, null, null);

            log.info("Token已撤销: tokenId={}, userId={}, email={}",
                    tokenRecord.getTokenId(), tokenRecord.getUserId(), tokenRecord.getEmail());

            return ResponseResult.ok("Token已成功撤销");
        } catch (Exception e) {
            log.error("撤销Token失败: token={}", token, e);
            return ResponseBuilder.error("撤销Token失败: " + e.getMessage());
        }
    }

    /**
     * 构建并校验请求参数
     */
    private EmailValidateRequestDTO buildAndValidateRequest(Long userId, String email, TokenType validateType) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("邮箱地址不能为空");
        }
        if (!emailValidateUtil.isValidEmailFormat(email)) {
            throw new IllegalArgumentException("邮箱格式不合法: " + email);
        }
        if (validateType == null) {
            validateType = TokenType.REGISTER; // 默认注册验证
        }
        return EmailValidateRequestDTO.builder()
                .userId(userId)
                .email(email)
                .validateType(validateType)
                .build();
    }

    /**
     * 保存Token记录到数据库
     */
    private void saveTokenRecord(EmailValidateRequestDTO requestDTO, String token, LocalDateTime expireTime) {
        try {
            EmailValidateTokens tokenEntity = new EmailValidateTokens();
            tokenEntity.setUserId(requestDTO.getUserId());
            tokenEntity.setEmail(requestDTO.getEmail());
            tokenEntity.setToken(token);
            tokenEntity.setTokenType(requestDTO.getValidateType());
            tokenEntity.setStatus(0); // 未使用
            tokenEntity.setCreatedAt(LocalDateTime.now());
            tokenEntity.setExpireAt(expireTime);

            emailValidateTokensMapper.insert(tokenEntity);
            log.debug("Token记录已保存到数据库: tokenId={}", tokenEntity.getTokenId());
        } catch (Exception e) {
            log.error("保存Token记录失败", e);
            throw new RuntimeException("保存Token记录失败: " + e.getMessage());
        }
    }

    /**
     * 保存操作历史记录
     *
     * @param userId       用户ID
     * @param email        邮箱地址
     * @param actionType   操作类型
     * @param tokenType    Token类型
     * @param result       操作结果
     * @param errorMessage 错误信息
     * @param clientIp     客户端IP
     */
    private void saveHistory(Long userId, String email, Action actionType,
                             TokenType tokenType, boolean result,
                             String errorMessage, String clientIp) {
        emailValidateHistoryService.saveHistory(userId, email, actionType, tokenType, result, errorMessage, clientIp);
    }
}
