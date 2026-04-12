package ltd.xiaomizha.xuyou.mail.service.impl;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.Action;
import ltd.xiaomizha.xuyou.common.utils.email.EmailValidateUtil;
import ltd.xiaomizha.xuyou.mail.service.EmailValidateHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Slf4j
public abstract class AbstractEmailService {

    @Resource
    protected JavaMailSender javaMailSender;

    @Resource
    protected EmailValidateUtil emailValidateUtil;

    @Resource
    protected EmailValidateHistoryService emailValidateHistoryService;

    @Value("${spring.mail.username:}")
    protected String senderEmail;

    @Value("${email.sender.name:小咪楂系统通知}")
    protected String senderName;

    protected void sendEmail(String recipientEmail, String subject, String htmlContent) throws Exception {
        log.info("准备发送邮件至: {}", recipientEmail);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(senderEmail, senderName);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

            log.info("邮件发送成功, 收件人: {}", recipientEmail);
        } catch (Exception e) {
            log.error("发送邮件失败, 收件人: {}", recipientEmail, e);
            throw new Exception("邮件发送失败: " + e.getMessage());
        }
    }

    protected void saveHistory(Long userId, String email, Action actionType,
                               Object tokenType, boolean result,
                               String errorMessage, String clientIp) {
        try {
            emailValidateHistoryService.saveHistory(userId, email, actionType, null, result, errorMessage, clientIp);
        } catch (Exception e) {
            log.warn("保存历史记录失败 (不影响主流程): {}", e.getMessage());
        }
    }

    protected boolean validateEmailFormat(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return emailValidateUtil.isValidEmailFormat(email);
    }

}
