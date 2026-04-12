package ltd.xiaomizha.xuyou.common.utils.email;

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 邮箱验证Token工具类
 */
@Slf4j
@Data
@Component
public class EmailValidateUtil {

    /**
     * 发送方邮箱
     */
    @Value("${spring.mail.username:}")
    private String senderEmail;

    /**
     * 发件人名称
     */
    @Value("${email.sender.name:小咪楂系统通知}")
    private String senderName;

    /**
     * 前端回调URL
     */
    @Value("${email.front.redirect.url:}")
    private String frontRedirectUrl;

    /**
     * Token有效期 (分钟)
     * <p>
     * 默认1440min (24h)
     */
    @Value("${email.validate.expire.minutes:1440}")
    private Integer validateExpireMinutes;

    /**
     * 验证码有效期 (分钟)
     * <p>
     * 默认5min
     */
    @Value("${email.code.expire.minutes:5}")
    private Integer codeExpireMinutes;

    /**
     * 发送冷却时间 (秒)
     * <p>
     * 默认60秒内不能重复发送
     */
    @Value("${email.code.cooldown.seconds:60}")
    private Integer cooldownSeconds;

    /**
     * 每日最大发送次数限制
     * <p>
     * 默认20次
     */
    @Value("${email.code.rate-limit.daily-max:20}")
    private Integer maxDailySendCount;

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getFrontRedirectUrl() {
        return frontRedirectUrl;
    }

    public Integer getCooldownSeconds() {
        return cooldownSeconds;
    }

    public Integer getMaxDailySendCount() {
        return maxDailySendCount;
    }

    /**
     * 生成唯一的邮箱验证Token (无横线)
     *
     * @return 32位纯字符串Token
     */
    public String generateValidateToken() {
        return IdUtil.simpleUUID();
    }

    /**
     * 生成6位数字验证码
     *
     * @return 6位数字验证码字符串
     */
    public String generateVerificationCode() {
        return String.format("%06d", IdUtil.fastSimpleUUID().hashCode() % 1000000);
    }

    /**
     * 生成6位验证码 (大写字母+数字)
     *
     * @return 6位大写字母+数字混合验证码字符串
     */
    public String generateVerificationCodeLetter() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(6);
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    /**
     * 生成指定长度的验证码 (大写字母+数字)
     *
     * @param length 验证码长度, 推荐4-8位
     * @return 指定长度的大写字母+数字混合验证码字符串
     */
    public String generateVerificationCodeLetter(int length) {
        if (length <= 0) {
            length = 6;
        }
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(length);
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    /**
     * 计算 Token 过期时间
     *
     * @return 过期时间点
     */
    public LocalDateTime calculateExpireTime() {
        return LocalDateTime.now().plus(validateExpireMinutes, ChronoUnit.MINUTES);
    }

    /**
     * 计算验证码过期时间
     *
     * @return 过期时间点
     */
    public LocalDateTime calculateCodeExpireTime() {
        return LocalDateTime.now().plus(codeExpireMinutes, ChronoUnit.MINUTES);
    }

    /**
     * 校验 Token 是否已过期
     *
     * @param expireTime Token 的过期时间
     * @return true-已过期, false-未过期
     */
    public boolean isTokenExpired(LocalDateTime expireTime) {
        if (expireTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 校验 Token 是否即将过期 (剩余时间不足10%)
     *
     * @param expireTime Token 过期时间
     * @return true-即将过期, false-未即将过期
     */
    public boolean isTokenExpiringSoon(LocalDateTime expireTime) {
        long totalMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expireTime);
        long threshold = (long) (validateExpireMinutes * 0.1);
        return totalMinutes > 0 && totalMinutes < threshold;
    }

    /**
     * 获取 Token 剩余有效时间 (分钟)
     *
     * @param expireTime Token 过期时间
     * @return 剩余分钟数, 已过期返回0或负数
     */
    public long getRemainingMinutes(LocalDateTime expireTime) {
        if (expireTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), expireTime);
    }

    /**
     * 对邮箱地址进行脱敏处理
     *
     * @param email 原始邮箱地址
     * @return 脱敏后的邮箱地址
     */
    public String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return username.charAt(0) + "***" + domain;
        } else {
            return username.charAt(0) + "***" + username.charAt(username.length() - 1) + domain;
        }
    }

    /**
     * 构建完整的验证链接URL
     *
     * @param baseUrl 前端回调地址
     * @param token   验证Token
     * @return 完整的验证链接
     */
    public String buildValidateUrl(String baseUrl, String token) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            log.warn("前端回调地址为空, 请检查配置: email.front.redirect.url");
            return "";
        }

        // 确保baseUrl以/结尾
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        return baseUrl + "?token=" + token;
    }

    /**
     * 校验邮箱格式是否合法
     *
     * @param email 邮箱地址
     * @return true-合法, false-非法
     */
    public boolean isValidEmailFormat(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    /**
     * 获取当前配置的验证Token有效期 (分钟)
     *
     * @return 有效期分钟数
     */
    public Integer getValidateExpireMinutes() {
        return validateExpireMinutes;
    }

    /**
     * 获取当前配置的验证码有效期 (分钟)
     *
     * @return 有效期分钟数
     */
    public Integer getCodeExpireMinutes() {
        return codeExpireMinutes;
    }

    /**
     * 构建邮箱验证邮件的HTML内容
     *
     * @param validateUrl 验证链接
     * @return HTML格式邮件内容
     */
    public String buildValidateEmailContent(String validateUrl) {
        return "<html><body>" +
                "<div style='max-width:600px;margin:0 auto;padding:30px;font-family:Arial,sans-serif;border-radius:8px;'>" +
                "<h2 style='color:#333;text-align:center;margin-bottom:20px;'>邮箱验证</h2>" +
                "<p style='font-size:15px;color:#555;line-height:1.8;text-align:center;'>" +
                "请点击下方按钮完成验证：" +
                "</p>" +
                "<div style='text-align:center;margin:25px 0;'>" +
                "<a href='" + validateUrl + "'" +
                "style='display:inline-block;padding:12px 35px;" +
                "background-color:#1890ff;color:#fff;" +
                "text-decoration:none;border-radius:5px;" +
                "font-size:15px;font-weight:bold;'>" +
                "验证邮箱" +
                "</a>" +
                "</div>" +
                "<div style='background:#fff;padding:18px;border-radius:0 6px 6px 0;margin-top:20px;border-left:3px solid #1890ff;'>" +
                "<p style='margin:0;color:#666;font-size:13px;line-height:1.6;'>" +
                "链接有效期 " + validateExpireMinutes / 60 + " 小时<br/>" +
                "若无法点击：<br/><span style='word-break:break-all;color:#1890ff;font-size:12px;'>" + validateUrl + "</span>" +
                "</p>" +
                "</div>" +
                "<hr style='border:none;border-top:1px solid #eee;margin:25px 0;'/>" +
                "<p style='text-align:center;color:#999;font-size:11px;margin:0;'>" +
                senderName + " · 请勿回复此邮件" +
                "</p>" +
                "</div></body></html>";
    }

    /**
     * 构建验证码邮件的HTML内容
     *
     * @param code          验证码
     * @param usageScenario 使用场景 (如: 登录验证、注册验证、重置密码等)
     * @param expireMinutes 有效期(分钟)
     * @return HTML格式邮件内容
     */
    public String buildCodeEmailContent(String code, String usageScenario, int expireMinutes) {
        return "<html><body>" +
                "<div style='max-width:500px;margin:0 auto;padding:30px;font-family:Arial,sans-serif;border-radius:8px;'>" +
                "<h2 style='color:#333;text-align:center;margin-bottom:20px;'>" + usageScenario + "</h2>" +
                "<p style='font-size:14px;color:#555;text-align:center;margin-bottom:25px;'>" +
                "您的验证码如下：" +
                "</p>" +
                "<div style='text-align:center;margin:20px 0;'>" +
                "<span style='display:inline-block;padding:15px 40px;" +
                "background:#f0f0f0;color:#333;" +
                "border-radius:6px;font-size:28px;" +
                "font-weight:bold;letter-spacing:8px;" +
                "font-family:'Courier New',monospace;'>" +
                code +
                "</span>" +
                "</div>" +
                "<div style='background:#fff;padding:16px;border-radius:0 6px 6px 0;margin-top:20px;border-left:3px solid #faad14;'>" +
                "<p style='margin:0;color:#666;font-size:13px;line-height:1.6;'>" +
                "验证码 " + expireMinutes + " 分钟内有效<br/>" +
                "请勿将验证码告知他人" +
                "</p>" +
                "</div>" +
                "<hr style='border:none;border-top:1px solid #eee;margin:25px 0;'/>" +
                "<p style='text-align:center;color:#999;font-size:11px;margin:0;'>" +
                senderName + " · 请勿回复此邮件" +
                "</p>" +
                "</div></body></html>";
    }

}
