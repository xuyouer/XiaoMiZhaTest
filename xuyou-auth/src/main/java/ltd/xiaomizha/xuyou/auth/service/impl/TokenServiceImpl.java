package ltd.xiaomizha.xuyou.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.auth.dto.AuthResponseDTO;
import ltd.xiaomizha.xuyou.auth.service.TokenService;
import ltd.xiaomizha.xuyou.common.utils.jwt.JwtUtils;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一令牌服务实现
 * <p>
 * 管理 JWT 和 Sa-Token 双轨认证机制
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Resource
    private JwtUtils jwtUtils;

    /**
     * 默认认证模式（可通过配置文件修改）
     * 可选值: jwt / satoken / sso
     */
    @Value("${auth.mode:satoken}")
    private String authMode;

    /**
     * 用户登录 - 生成令牌（自动根据配置选择认证模式）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户详细信息
     * @return 统一认证响应 DTO
     */
    @Override
    public AuthResponseDTO login(Integer userId, String username, UserDetailDTO userInfo) {
        log.info("用户登录: userId={}, username={}, authMode={}", userId, username, authMode);

        return switch (authMode.toLowerCase()) {
            case "jwt" -> loginWithJwt(userId, username, userInfo);
            case "sso" -> loginWithSso(userId, username, userInfo);
            default -> loginWithSaToken(userId, username, userInfo);
        };
    }

    /**
     * 用户登录 - 生成令牌（指定认证模式）
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户详细信息
     * @param authMode 认证模式 (jwt/satoken/sso)
     * @return 统一认证响应 DTO
     */
    @Override
    public AuthResponseDTO login(Integer userId, String username, UserDetailDTO userInfo, String authMode) {
        log.info("用户登录: userId={}, username={}, authMode={}", userId, username, authMode);

        return switch (authMode != null ? authMode.toLowerCase() : this.authMode.toLowerCase()) {
            case "jwt" -> loginWithJwt(userId, username, userInfo);
            case "sso" -> loginWithSso(userId, username, userInfo);
            default -> loginWithSaToken(userId, username, userInfo);
        };
    }

    /**
     * JWT 单Token 登录
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户信息
     * @return JWT 认证响应
     */
    @Override
    public AuthResponseDTO loginWithJwt(Integer userId, String username, UserDetailDTO userInfo) {
        String token = jwtUtils.generateToken(userId, username);
        Long expiresIn = jwtUtils.getExpirationInSeconds();

        log.info("JWT 登录成功: userId={}, token={}...", userId,
                token.substring(0, Math.min(20, token.length())));
        return AuthResponseDTO.jwt(token, userInfo, expiresIn);
    }

    /**
     * Sa-Token 双Token 模式登录
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户信息
     * @return Sa-Token 双Token 认证响应
     */
    @Override
    public AuthResponseDTO loginWithSaToken(Integer userId, String username, UserDetailDTO userInfo) {
        // 登录
        StpUtil.login(userId, "pc");
        // 获取 AccessToken
        String accessToken = StpUtil.getTokenValue();
        // 获取 RefreshToken
        String refreshToken = SaTempUtil.createToken(userId, 2592000);
        // 获取 AccessToken 过期时间（秒）
        Long expiresIn = StpUtil.getTokenTimeout();

        log.info("Sa-Token 登录成功: userId={}, accessToken={}", userId,
                accessToken != null ? accessToken.substring(0, Math.min(20, accessToken.length())) + "..." : "null");
        return AuthResponseDTO.saToken(accessToken, refreshToken, userInfo, expiresIn);
    }

    /**
     * SSO 单点登录
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param userInfo 用户信息
     * @return SSO 认证响应（包含 Ticket 或 Token）
     */
    @Override
    public AuthResponseDTO loginWithSso(Integer userId, String username, UserDetailDTO userInfo) {
        AuthResponseDTO response = loginWithSaToken(userId, username, userInfo);
        response.setAuthMode("sso"); // 标记为 SSO 模式

        log.info("SSO 登录成功: userId={}", userId);
        return response;
    }

    /**
     * 刷新令牌（仅 Sa-Token/SSO 模式支持）
     *
     * @param refreshToken 刷新令牌
     * @return 新的 AccessToken
     */
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        if (!isSupportRefresh()) {
            throw new UnsupportedOperationException("当前认证模式（" + authMode + "）不支持刷新令牌");
        }

        try {
            Object loginId = SaTempUtil.parseToken(refreshToken);

            if (loginId == null) {
                throw new RuntimeException("RefreshToken 无效或已过期，请重新登录");
            }

            // 生成新的 AccessToken
            String newAccessToken = StpUtil.createLoginSession(loginId);
            Long expiresIn = StpUtil.getTokenTimeout();

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", newAccessToken);
            data.put("refreshToken", refreshToken); // RefreshToken不变, 除非过期
            data.put("tokenType", "Bearer");
            data.put("expiresIn", expiresIn);

            log.info("令牌刷新成功: loginId={}", loginId);
            return data;
        } catch (Exception e) {
            log.error("令牌刷新失败", e);
            throw new RuntimeException("令牌刷新失败: " + e.getMessage());
        }
    }

    /**
     * 验证令牌有效性
     *
     * @param token 访问令牌
     * @return 是否有效
     */
    @Override
    public boolean validateToken(String token) {
        return switch (authMode.toLowerCase()) {
            case "jwt" -> jwtUtils.validateToken(token);
            default -> {
                try {
                    StpUtil.checkLogin();
                    yield true;
                } catch (Exception e) {
                    yield false;
                }
            }
        };
    }

    /**
     * 用户登出
     *
     * @param token 访问令牌
     */
    @Override
    public void logout(String token) {
        switch (authMode.toLowerCase()) {
            case "jwt" -> {
                if (token != null && !token.isEmpty()) {
                    Integer userId = jwtUtils.getUserIdFromToken(token);
                    log.info("JWT 用户登出: userId={}", userId);
                    // TODO: 将 Token 加入 Redis 黑名单，实现主动失效
                }
            }
            default -> {
                if (StpUtil.isLogin()) {
                    Object loginId = StpUtil.getLoginId();
                    StpUtil.logout();
                    log.info("{} 用户登出成功: loginId={}", authMode.toUpperCase(), loginId);
                }
            }
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token 访问令牌
     * @return 用户ID
     */
    @Override
    public Integer getUserIdFromToken(String token) {
        return switch (authMode.toLowerCase()) {
            case "jwt" -> jwtUtils.getUserIdFromToken(token);
            default -> {
                Object loginId = StpUtil.getLoginId();
                if (loginId instanceof Integer integer) {
                    yield integer;
                } else if (loginId instanceof Number number) {
                    yield number.intValue();
                } else {
                    try {
                        yield Integer.parseInt(loginId.toString());
                    } catch (NumberFormatException e) {
                        yield null;
                    }
                }
            }
        };
    }

    /**
     * 获取当前认证模式
     *
     * @return "jwt"、"satoken" 或 "sso"
     */
    @Override
    public String getAuthMode() {
        return authMode;
    }

    /**
     * 当前模式是否支持令牌刷新
     *
     * @return 是否支持
     */
    @Override
    public boolean isSupportRefresh() {
        return "satoken".equalsIgnoreCase(authMode) || "sso".equalsIgnoreCase(authMode);
    }
}
