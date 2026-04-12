package ltd.xiaomizha.xuyou.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.auth.dto.AuthResponseDTO;
import ltd.xiaomizha.xuyou.auth.dto.LoginRequestDTO;
import ltd.xiaomizha.xuyou.auth.service.TokenService;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.user.UserUtils;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 统一认证控制器
 */
@Slf4j
@RestController
@RequestMapping("auth")
@Tag(name = "统一认证管理", description = "用户认证API（JWT + Sa-Token 双模式）")
public class AuthController {

    @Resource
    private UsersService usersService;
    @Resource
    private TokenService tokenService;

    /**
     * 统一登录接口
     * <p>
     * 根据 authMode 参数自动选择认证方式：
     * <p>
     * authMode=jwt 或为空：使用 JWT 单 Token 认证
     * <p>
     * authMode=satoken：使用 Sa-Token 双Token 认证
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "支持 JWT/Sa-Token 双模式登录")
    public ResponseResult<?> login(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        try {
            // 获取客户端信息
            String ipAddress = UserUtils.getClientIp(httpRequest);
            String userAgent = UserUtils.getUserAgent(httpRequest);
            String deviceInfo = UserConstants.DEFAULT_DEVICE_INFO;

            // 验证用户名密码
            boolean loginSuccess = usersService.loginUser(
                    request.getUsername(),
                    request.getPasswordHash(),
                    ipAddress,
                    userAgent,
                    deviceInfo,
                    LoginType.LOGIN
            );

            if (!loginSuccess) {
                log.error("用户登录失败: 用户名密码错误或用户已被禁用");
                return ResponseResult.error(ResultEnum.PASSWORD_ERROR.getCode(), "用户名或密码错误");
            }

            // 获取用户信息
            Users user = usersService.lambdaQuery()
                    .eq(Users::getUsername, request.getUsername())
                    .one();

            if (user == null) {
                return ResponseResult.error(ResultEnum.USER_NOT_FOUND.getCode(), "用户不存在");
            }

            UserDetailDTO userDetail = usersService.getUserDetailById(user.getUserId());

            // 根据认证模式返回不同的 Token
            String authMode = request.getAuthMode();
            AuthResponseDTO response = tokenService.login(user.getUserId(), user.getUsername(), userDetail, authMode);
            return ResponseResult.ok(response);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "根据 Token 获取当前登录用户信息")
    public ResponseResult<UserDetailDTO> getCurrentUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                // 尝试从 Sa-Token 获取用户信息
                if (StpUtil.isLogin()) {
                    Object loginId = StpUtil.getLoginId();
                    Integer userId = Integer.parseInt(loginId.toString());
                    UserDetailDTO userDetail = usersService.getUserDetailById(userId);
                    return ResponseResult.ok(userDetail);
                }
                return ResponseResult.error(ResultEnum.UNAUTHORIZED.getCode(), "未登录");
            }

            // 判断是 JWT 还是 Sa-Token
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);

                // 从 Token 解析用户ID
                Integer userId = tokenService.getUserIdFromToken(token);
                if (userId == null) {
                    return ResponseResult.error(ResultEnum.INVALID_TOKEN.getCode(), "token无效");
                }

                UserDetailDTO userDetail = usersService.getUserDetailById(userId);
                if (userDetail != null) {
                    return ResponseResult.ok(userDetail);
                } else {
                    return ResponseResult.error(ResultEnum.USER_NOT_FOUND.getCode(), "用户不存在");
                }
            }

            return ResponseResult.error(ResultEnum.INVALID_TOKEN.getCode(), "不支持的token格式");

        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "JWT/Sa-Token 统一登出")
    public ResponseResult<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token != null && !token.isEmpty() && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            tokenService.logout(token);
            return ResponseResult.ok("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return ResponseResult.error("登出失败: " + e.getMessage());
        }
    }

    /**
     * 刷新 AccessToken
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新AccessToken", description = "使用 RefreshToken 刷新 AccessToken")
    public ResponseResult<?> refreshToken(@RequestParam String refreshToken) {
        try {
            Map<String, Object> data = tokenService.refreshToken(refreshToken);
            return ResponseResult.ok(data);
        } catch (Exception e) {
            log.error("刷新 Token 失败", e);
            return ResponseResult.error("刷新Token失败: " + e.getMessage());
        }
    }

}
