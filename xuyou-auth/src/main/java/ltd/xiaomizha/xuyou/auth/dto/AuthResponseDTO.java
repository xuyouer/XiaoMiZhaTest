package ltd.xiaomizha.xuyou.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;

/**
 * 统一认证响应 DTO
 * <p>
 * 支持双Token模式 (Sa-Token) 和单Token模式 (JWT)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    /**
     * 访问令牌 (AccessToken)
     * <p>
     * JWT 模式: JWT Token 字符串
     * <p>
     * Sa-Token 模式: AccessToken (短期有效，30分钟)
     */
    @JsonProperty("accessToken")
    private String accessToken;

    /**
     * 刷新令牌 (RefreshToken)
     * <p>
     * JWT 模式: null (JWT 不支持刷新)
     * <p>
     * Sa-Token 模式: RefreshToken (长期有效，7天)
     */
    @JsonProperty("refreshToken")
    private String refreshToken;

    /**
     * 令牌类型
     */
    @JsonProperty("tokenType")
    private String tokenType;

    /**
     * 访问令牌过期时间 (秒)
     */
    @JsonProperty("expiresIn")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @JsonProperty("userInfo")
    private UserDetailDTO userInfo;

    /**
     * 认证模式标识
     * <p>
     * "jwt": 传统 JWT 单Token 模式
     * <p>
     * "satoken": Sa-Token 双Token 模式
     */
    @JsonProperty("authMode")
    private String authMode;

    /**
     * 创建 JWT 模式响应
     */
    public static AuthResponseDTO jwt(String accessToken, UserDetailDTO userInfo, Long expiresIn) {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(null);
        dto.setTokenType("Bearer");
        dto.setExpiresIn(expiresIn);
        dto.setUserInfo(userInfo);
        dto.setAuthMode("jwt");
        return dto;
    }

    /**
     * 创建 Sa-Token 双Token 模式响应
     */
    public static AuthResponseDTO saToken(String accessToken, String refreshToken, UserDetailDTO userInfo, Long expiresIn) {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        dto.setTokenType("Bearer");
        dto.setExpiresIn(expiresIn);
        dto.setUserInfo(userInfo);
        dto.setAuthMode("satoken");
        return dto;
    }
}
