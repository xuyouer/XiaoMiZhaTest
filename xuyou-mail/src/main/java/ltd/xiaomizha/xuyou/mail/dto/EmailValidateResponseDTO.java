package ltd.xiaomizha.xuyou.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮箱验证响应DTO
 * <p>
 * 返回给前端的邮箱验证结果信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailValidateResponseDTO {

    /**
     * 是否发送成功
     */
    private Boolean success;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 邮箱地址 (脱敏显示)
     */
    private String maskedEmail;

    /**
     * Token过期时间 (分钟)
     */
    private Integer expireMinutes;

    /**
     * 生成成功响应
     */
    public static EmailValidateResponseDTO success(String message, String maskedEmail, Integer expireMinutes) {
        return EmailValidateResponseDTO.builder()
                .success(true)
                .message(message)
                .maskedEmail(maskedEmail)
                .expireMinutes(expireMinutes)
                .build();
    }

    /**
     * 生成失败响应
     */
    public static EmailValidateResponseDTO error(String message) {
        return EmailValidateResponseDTO.builder()
                .success(false)
                .message(message)
                .build();
    }

}
