package ltd.xiaomizha.xuyou.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;

/**
 * 邮箱验证请求DTO
 * <p>
 * 接收前端发送的邮箱验证相关请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailValidateRequestDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收件人邮箱地址
     */
    private String email;

    /**
     * 验证类型
     * <p>
     * register-注册验证、reset-重置密码、bind-绑定邮箱
     */
    private TokenType validateType;

}
