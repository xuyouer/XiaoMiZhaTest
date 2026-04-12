package ltd.xiaomizha.xuyou.common.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice(value = "ltd.xiaomizha.xuyou")
public class AuthExceptionHandler {

    /**
     * 处理未登录异常（Sa-Token）
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseResult<Void> handleNotLogin(NotLoginException e) {
        log.warn("未登录异常: {}", e.getMessage());
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供 Token";
            case NotLoginException.INVALID_TOKEN -> "Token 无效";
            case NotLoginException.TOKEN_TIMEOUT -> "Token 已过期";
            case NotLoginException.BE_REPLACED -> "账号已被顶替";
            case NotLoginException.KICK_OUT -> "账号已被踢下线";
            default -> "当前未登录";
        };
        return ResponseResult.error(ResultEnum.UNAUTHORIZED.getCode(), message);
    }

    /**
     * 处理无权限异常（Sa-Token）
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseResult<Void> handleNotPermission(NotPermissionException e) {
        log.warn("无权限异常: {}", e.getPermission());
        return ResponseResult.error(ResultEnum.FORBIDDEN.getCode(), e.getPermission());
    }

    /**
     * 处理无角色异常（Sa-Token）
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseResult<Void> handleNotRole(NotRoleException e) {
        log.warn("无角色异常: {}", e.getRole());
        return ResponseResult.error(ResultEnum.FORBIDDEN.getCode(), e.getRole());
    }

    /**
     * 处理 Sa-Token 其他异常
     */
    @ExceptionHandler(SaTokenException.class)
    public ResponseResult<Void> handleSaToken(SaTokenException e) {
        log.error("Sa-Token 异常: {}", e.getMessage(), e);
        return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), ResultEnum.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage());
    }
}
