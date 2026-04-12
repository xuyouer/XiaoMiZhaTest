package ltd.xiaomizha.xuyou.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    private String username;
    @JsonProperty("passwordHash")
    private String passwordHash;
    /**
     * 认证模式（可选，默认使用系统配置）
     * <p>
     * jwt: JWT 单Token 模式
     * <p>
     * satoken: Sa-Token 双Token 模式
     */
    @JsonProperty("authMode")
    private String authMode;

}
