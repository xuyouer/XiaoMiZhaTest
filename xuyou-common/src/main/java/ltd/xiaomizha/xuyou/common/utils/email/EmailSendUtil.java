package ltd.xiaomizha.xuyou.common.utils.email;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.mail.host")
public class EmailSendUtil {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private EmailValidateUtil emailValidateUtil;

    /**
     * 发送 HTML 格式邮件
     *
     * @param recipientEmail 收件人邮箱
     * @param subject        邮件主题
     * @param htmlContent    HTML格式邮件内容
     * @throws Exception 邮件发送异常
     */
    public void sendHtmlEmail(String recipientEmail, String subject, String htmlContent) throws Exception {
        log.info("准备发送邮件至: {}, 主题: {}", recipientEmail, subject);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String senderEmail = emailValidateUtil.getSenderEmail();
            String senderName = emailValidateUtil.getSenderName();

            helper.setFrom(senderEmail, senderName);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

            log.info("邮件发送成功: 收件人={}, 主题={}", recipientEmail, subject);
        } catch (Exception e) {
            log.error("邮件发送失败: 收件人={}, 主题={}, 错误: {}", recipientEmail, subject, e.getMessage(), e);
            throw new Exception("邮件发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送邮箱验证邮件, 带验证链接
     *
     * @param recipientEmail 收件人邮箱
     * @param validateToken  验证Token
     * @throws Exception 发送异常
     */
    public void sendValidationEmail(String recipientEmail, String validateToken) throws Exception {
        String frontRedirectUrl = emailValidateUtil.getFrontRedirectUrl();
        String validateUrl = emailValidateUtil.buildValidateUrl(frontRedirectUrl, validateToken);
        String emailContent = emailValidateUtil.buildValidateEmailContent(validateUrl);

        sendHtmlEmail(recipientEmail, "账号邮箱验证通知", emailContent);
    }

    /**
     * 发送验证码邮件, 带数字验证码
     *
     * @param recipientEmail 收件人邮箱
     * @param code           验证码
     * @param scene          使用场景: 登录验证、注册验证……
     * @throws Exception 发送异常
     */
    public void sendCodeEmail(String recipientEmail, String code, String scene) throws Exception {
        Integer codeExpireMinutes = emailValidateUtil.getCodeExpireMinutes();
        String emailContent = emailValidateUtil.buildCodeEmailContent(code, scene, codeExpireMinutes);

        sendHtmlEmail(recipientEmail, scene + "验证码", emailContent);
    }

}
