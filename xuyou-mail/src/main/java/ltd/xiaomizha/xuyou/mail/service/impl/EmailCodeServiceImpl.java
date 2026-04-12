package ltd.xiaomizha.xuyou.mail.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.CacheConstant;
import ltd.xiaomizha.xuyou.common.enums.entity.Action;
import ltd.xiaomizha.xuyou.common.response.ResponseBuilder;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.email.EmailSendUtil;
import ltd.xiaomizha.xuyou.common.utils.email.EmailValidateUtil;
import ltd.xiaomizha.xuyou.common.utils.geo.IpRegionUtil;
import ltd.xiaomizha.xuyou.common.utils.redis.RateLimitUtil;
import ltd.xiaomizha.xuyou.mail.dto.CooldownStatusDTO;
import ltd.xiaomizha.xuyou.mail.dto.EmailCodeResponseDTO;
import ltd.xiaomizha.xuyou.mail.service.EmailCodeService;
import ltd.xiaomizha.xuyou.mail.service.EmailValidateHistoryService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    @Resource
    private EmailSendUtil emailSendUtil;

    @Resource
    private EmailValidateUtil emailValidateUtil;

    @Resource
    private IpRegionUtil ipRegionUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private EmailValidateHistoryService emailValidateHistoryService;

    @Resource
    private RateLimitUtil rateLimitUtil;

    /**
     * 发送邮箱验证码
     * <p>
     * 生成6位数字验证码, 保存到Redis或数据库, 并发送邮件
     *
     * @param email 收件人邮箱地址
     * @param scene 使用场景: LOGIN-登录, REGISTER-注册, RESET_PASSWORD-重置密码, BIND-绑定
     * @return 发送结果 (包含脱敏邮箱、有效期等)
     */
    @Override
    public ResponseResult<?> sendVerificationCode(String email, String scene) {
        try {
            log.info("开始发送邮箱验证码: email={}, scene={}", email, scene);

            // 参数校验
            if (email == null || email.isEmpty()) {
                return ResponseBuilder.error("邮箱地址不能为空");
            }
            if (!emailValidateUtil.isValidEmailFormat(email)) {
                return ResponseBuilder.error("邮箱格式不合法");
            }
            if (scene == null || scene.isEmpty()) {
                scene = "LOGIN"; // 默认登录场景
            }

            // 冷却时间检查, 防止频繁发送
            // String cooldownKey = CacheConstant.REDIS_PREFIX_COOLDOWN + email + ":" + scene.toLowerCase();
            // String remainingCooldown = stringRedisTemplate.opsForValue().get(cooldownKey);
            // if (remainingCooldown != null) {
            //     long remainingSeconds = Long.parseLong(remainingCooldown);
            //     log.warn("邮箱验证码发送过于频繁: email={}, scene={}, 剩余冷却时间={}s", email, scene, remainingSeconds);
            //     return ResponseBuilder.error("发送过于频繁，请 " + remainingSeconds + " 秒后重试");
            // }
            String cooldownKey = email + ":" + scene.toLowerCase();
            RateLimitUtil.CooldownStatus cooldownStatus = rateLimitUtil.checkCooldown(cooldownKey, emailValidateUtil.getCooldownSeconds());

            if (!cooldownStatus.isAllowed()) {
                log.warn("邮箱验证码发送过于频繁: email={}, scene={}, 剩余冷却时间={}s", email, scene, cooldownStatus.getRemainingSeconds());
                return ResponseBuilder.error("发送过于频繁，请 " + cooldownStatus.getRemainingSeconds() + " 秒后重试");
            }

            // 每日发送次数限制检查
            String dailyLimitKey = email;
            RateLimitUtil.DailyLimitStatus dailyStatus = rateLimitUtil.checkDailyLimit(dailyLimitKey, emailValidateUtil.getMaxDailySendCount());

            if (!dailyStatus.isAllowed()) {
                log.warn("邮箱验证码达到每日发送上限: email={}, scene={}, 今日已用={}次, 上限={}次", email, scene, dailyStatus.getUsedCount(), emailValidateUtil.getMaxDailySendCount());
                return ResponseBuilder.error("今日验证码发送已达上限（" + emailValidateUtil.getMaxDailySendCount() + "次），请明天再试。" + "今日已使用: " + dailyStatus.getUsedCount() + " 次");
            }

            // 生成6位验证码
            String code = emailValidateUtil.generateVerificationCodeLetter();

            Integer codeExpireMinutes = emailValidateUtil.getCodeExpireMinutes();
            Integer cooldownSeconds = emailValidateUtil.getCooldownSeconds();

            // 存储到Redis, 带过期时间
            String codeKey = CacheConstant.REDIS_PREFIX_EMAIL_CODE + email + ":" + scene.toLowerCase();
            stringRedisTemplate.opsForValue().set(codeKey, code, codeExpireMinutes, TimeUnit.MINUTES);

            log.info("生成邮箱验证码: email={}, scene={}, code={}", email, scene, code);

            // 发送邮件
            emailSendUtil.sendCodeEmail(email, code, scene);

            // 设置冷却时间
            // stringRedisTemplate.opsForValue().set(cooldownKey, String.valueOf(cooldownSeconds), cooldownSeconds, TimeUnit.SECONDS);
            rateLimitUtil.setCooldown(cooldownKey, emailValidateUtil.getCooldownSeconds());

            // 每日发送计数
            int dailyUsedCount = rateLimitUtil.incrementDailyCount(dailyLimitKey);
            log.info("邮箱验证码每日计数: email={}, 今日已用={}/{}次", email, dailyUsedCount, emailValidateUtil.getMaxDailySendCount());

            // 记录历史
            saveHistory(null, email, Action.SEND, null, true, null, null);

            String maskedEmail = emailValidateUtil.maskEmail(email);

            log.info("邮箱验证码发送成功: email={}, scene={}", maskedEmail, scene);

            return ResponseResult.successWithMessage(
                    "验证码已发送至 " + maskedEmail + "，请查收（包括垃圾邮件箱）",
                    new EmailCodeResponseDTO(
                            maskedEmail,
                            codeExpireMinutes,
                            LocalDateTime.now().plus(codeExpireMinutes, ChronoUnit.MINUTES)
                    )
            );
        } catch (Exception e) {
            log.error("发送邮箱验证码失败: email={}, scene={}", email, scene, e);
            saveHistory(null, email, Action.SEND, null, false, e.getMessage(), null);
            return ResponseBuilder.error("发送验证码失败: " + e.getMessage());
        }
    }

    /**
     * 校验邮箱验证码
     *
     * @param email 邮箱地址
     * @param code  用户输入的验证码
     * @param ip    客户端IP地址 (用于日志记录)
     * @return 校验结果
     */
    @Override
    public ResponseResult<?> verifyCode(String email, String code, String ip) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseBuilder.error("邮箱地址不能为空");
            }
            if (code == null || code.isEmpty()) {
                return ResponseBuilder.error("验证码不能为空");
            }
            if (code.length() != 6) {
                return ResponseBuilder.error("验证码格式错误（应为6位数字）");
            }

            log.info("校验邮箱验证码: email={}, ip={}", email, ip);

            // 从Redis获取存储的验证码
            String scene = inferSceneFromIp(ip);
            String codeKey = CacheConstant.REDIS_PREFIX_EMAIL_CODE + email + ":" + scene.toLowerCase();
            String storedCode = stringRedisTemplate.opsForValue().getAndDelete(codeKey);

            if (storedCode == null) {
                saveHistory(null, email, Action.VALIDATE, null, false, "验证码不存在或已过期", ip);
                return ResponseBuilder.error("验证码不存在或已过期，请重新获取");
            }
            if (!storedCode.equals(code)) {
                saveHistory(null, email, Action.VALIDATE, null, false, "验证码错误", ip);
                return ResponseBuilder.error("验证码错误，请重新输入");
            }

            // 校验成功 - 删除冷却时间限制, 允许立即重发
            String cooldownKey = CacheConstant.REDIS_PREFIX_COOLDOWN + email + ":" + scene.toLowerCase();
            stringRedisTemplate.delete(cooldownKey);

            saveHistory(null, email, Action.VALIDATE, null, true, null, ip);

            log.info("邮箱验证码校验成功: email={}", email);

            return ResponseResult.ok("验证码校验成功");
        } catch (Exception e) {
            log.error("校验邮箱验证码异常: email={}", email, e);
            return ResponseBuilder.error("校验过程出现异常: " + e.getMessage());
        }
    }

    /**
     * 查询验证码发送状态, 是否可以重新发送
     *
     * @param email 邮箱地址
     * @param scene 使用场景
     * @return 是否可重发及剩余冷却时间 (秒)、每日使用情况
     */
    @Override
    public ResponseResult<?> checkSendStatus(String email, String scene) {
        try {
            if (scene == null || scene.isEmpty()) {
                scene = "LOGIN";
            }

            // String cooldownKey = CacheConstant.REDIS_PREFIX_COOLDOWN + email + ":" + scene.toLowerCase();
            // String remainingCooldown = stringRedisTemplate.opsForValue().get(cooldownKey);
            // if (remainingCooldown != null) {
            //     long remainingSeconds = Long.parseLong(remainingCooldown);
            //     return ResponseResult.successWithMessage("冷却中，请稍后重试", new CooldownStatusDTO(false, remainingSeconds));
            // }
            // return ResponseResult.successWithMessage("可以发送验证码", new CooldownStatusDTO(true, 0L));

            // 检查冷却时间状态
            String cooldownKey = email + ":" + scene.toLowerCase();
            RateLimitUtil.CooldownStatus cooldownStatus = rateLimitUtil.checkCooldown(cooldownKey, emailValidateUtil.getCooldownSeconds());
            if (!cooldownStatus.isAllowed()) {
                return ResponseResult.successWithMessage("冷却中，请稍后重试", new CooldownStatusDTO(false, cooldownStatus.getRemainingSeconds()));
            }

            // 检查每日使用次数限制
            String dailyLimitKey = email;
            RateLimitUtil.DailyLimitStatus dailyStatus = rateLimitUtil.checkDailyLimit(dailyLimitKey, emailValidateUtil.getMaxDailySendCount());
            CooldownStatusDTO statusDTO = new CooldownStatusDTO(
                    dailyStatus.isAllowed(),
                    0L,
                    dailyStatus.getUsedCount(),
                    emailValidateUtil.getMaxDailySendCount(),
                    dailyStatus.getRemainingCount()
            );
            if (dailyStatus.isAllowed()) {
                return ResponseResult.successWithMessage("可以发送验证码", statusDTO);
            } else {
                return ResponseResult.successWithMessage("今日已达发送上限", statusDTO);
            }
        } catch (Exception e) {
            log.error("查询发送状态失败: email={}", email, e);
            return ResponseBuilder.error("查询状态失败: " + e.getMessage());
        }
    }

    /**
     * 从请求参数或上下文获取使用场景
     */
    private String inferSceneFromIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "LOGIN";
        }
        if (ipRegionUtil.isPrivateIp(ip)) {
            return "LOGIN";
        }
        return "LOGIN";
    }

    /**
     * 保存历史记录
     */
    private void saveHistory(Long userId, String email, Action actionType,
                             Object tokenType, boolean result,
                             String errorMessage, String clientIp) {
        try {
            emailValidateHistoryService.saveHistory(userId, email, actionType, null, result, errorMessage, clientIp);
        } catch (Exception e) {
            log.warn("保存历史记录失败 (不影响主流程): {}", e.getMessage());
        }
    }

}
