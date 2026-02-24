package ltd.xiaomizha.xuyou.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.jwt.JwtUtils;
import ltd.xiaomizha.xuyou.common.utils.user.UserUtils;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("auth")
@Tag(name = "认证管理", description = "用户认证API")
public class AuthController {

    @Resource
    private UsersService usersService;
    @Resource
    private JwtUtils jwtUtils;

    @Data
    public static class LoginRequest {
        private String username;
        private String passwordHash;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private UserDetailDTO userInfo;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并返回token")
    public ResponseResult<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // 获取客户端信息
            String ipAddress = UserUtils.getClientIp(httpRequest);
            String userAgent = UserUtils.getUserAgent(httpRequest);
            String deviceInfo = UserConstants.DEFAULT_DEVICE_INFO;
            // String passwordHash = DigestUtils.md5DigestAsHex(request.getPassword().getBytes(StandardCharsets.UTF_8));
            // 验证登录
            boolean loginSuccess = usersService.loginUser(
                    request.getUsername(),
                    request.getPasswordHash(),
                    ipAddress,
                    userAgent,
                    deviceInfo,
                    LoginType.LOGIN
            );
            if (loginSuccess) {
                // 获取用户详细信息
                Users user = usersService.lambdaQuery()
                        .eq(Users::getUsername, request.getUsername())
                        .one();

                if (user != null) {
                    UserDetailDTO userDetail = usersService.getUserDetailById(user.getUserId());

                    // 使用JWT生成token
                    String token = jwtUtils.generateToken(user.getUserId(), user.getUsername());

                    LoginResponse response = new LoginResponse();
                    response.setToken(token);
                    response.setUserInfo(userDetail);

                    log.info("用户登录成功: username={}, userId={}", request.getUsername(), user.getUserId());
                    return ResponseResult.ok(response);
                } else {
                    return ResponseResult.<LoginResponse>error(ResultEnum.USER_NOT_FOUND.getCode(), "用户不存在");
                }
            } else {
                log.error("用户登录失败: 用户名密码错误或用户已被禁用");
                return ResponseResult.<LoginResponse>error(ResultEnum.PASSWORD_ERROR.getCode(), "用户名或密码错误");
            }
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ResponseResult.<LoginResponse>error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "登录失败: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "根据token获取当前登录用户信息")
    public ResponseResult<UserDetailDTO> getCurrentUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseResult.<UserDetailDTO>error(ResultEnum.UNAUTHORIZED.getCode(), "未登录");
            }
            // 验证token是否有效
            if (!jwtUtils.validateToken(token)) {
                return ResponseResult.<UserDetailDTO>error(ResultEnum.INVALID_TOKEN.getCode(), "token无效或已过期");
            }
            // 从token中解析用户ID
            Integer userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return ResponseResult.<UserDetailDTO>error(ResultEnum.INVALID_TOKEN.getCode(), "token无效");
            }
            UserDetailDTO userDetail = usersService.getUserDetailById(userId);
            if (userDetail != null) {
                return ResponseResult.ok(userDetail);
            } else {
                return ResponseResult.<UserDetailDTO>error(ResultEnum.USER_NOT_FOUND.getCode(), "用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseResult.<UserDetailDTO>error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "获取用户信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出")
    public ResponseResult<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 将token加入黑名单或删除
            if (token != null && !token.isEmpty()) {
                Integer userId = jwtUtils.getUserIdFromToken(token);
                log.info("用户登出成功: userId={}", userId);
            }
            return ResponseResult.ok("登出成功");
        } catch (Exception e) {
            log.error("用户登出失败", e);
            return ResponseResult.error("登出失败: " + e.getMessage());
        }
    }
}
