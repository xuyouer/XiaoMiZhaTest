package ltd.xiaomizha.xuyou.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailCodeResponseDTO {

    /**
     * 脱敏后的邮箱
     */
    private String maskedEmail;

    /**
     * 有效期 (分钟)
     */
    private Integer expireMinutes;

    /**
     * 过期时间点
     */
    private LocalDateTime expireAt;

}
