package ltd.xiaomizha.xuyou.auth.service;

import ltd.xiaomizha.xuyou.auth.dto.AuthResponseDTO;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;

import java.util.Map;

/**
 * 统一令牌服务
 * <p>
 * 管理 JWT 和 Sa-Token 双轨认证机制
 */
public interface TokenService {

    /**
     * 用户登录 - 生成令牌（自动根据配置选择认证模式）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户详细信息
     * @return 统一认证响应 DTO
     */
    AuthResponseDTO login(Integer userId, String username, UserDetailDTO userInfo);

    /**
     * 用户登录 - 生成令牌（指定认证模式）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户详细信息
     * @param authMode 认证模式 (jwt/satoken/sso)
     * @return 统一认证响应 DTO
     */
    AuthResponseDTO login(Integer userId, String username, UserDetailDTO userInfo, String authMode);

    /**
     * JWT 单Token 登录
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户信息
     * @return JWT 认证响应
     */
    AuthResponseDTO loginWithJwt(Integer userId, String username, UserDetailDTO userInfo);

    /**
     * Sa-Token 双Token 模式登录
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户信息
     * @return Sa-Token 双Token 认证响应
     */
    AuthResponseDTO loginWithSaToken(Integer userId, String username, UserDetailDTO userInfo);

    /**
     * SSO 单点登录
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户信息
     * @return SSO 认证响应（包含 Ticket 或 Token）
     */
    AuthResponseDTO loginWithSso(Integer userId, String username, UserDetailDTO userInfo);

    /**
     * 刷新令牌（仅 Sa-Token/SSO 模式支持）
     *
     * @param refreshToken 刷新令牌
     * @return 新的 AccessToken
     */
    Map<String, Object> refreshToken(String refreshToken);

    /**
     * 验证令牌有效性
     *
     * @param token 访问令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 用户登出
     *
     * @param token 访问令牌
     */
    void logout(String token);

    /**
     * 从令牌中获取用户ID
     *
     * @param token 访问令牌
     * @return 用户ID
     */
    Integer getUserIdFromToken(String token);

    /**
     * 获取当前认证模式
     *
     * @return "jwt"、"satoken" 或 "sso"
     */
    String getAuthMode();

    /**
     * 当前模式是否支持令牌刷新
     *
     * @return 是否支持
     */
    boolean isSupportRefresh();
}
