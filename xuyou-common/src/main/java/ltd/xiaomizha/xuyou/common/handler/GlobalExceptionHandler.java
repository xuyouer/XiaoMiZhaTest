package ltd.xiaomizha.xuyou.common.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.exception.CommonException;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.app.AppConfigUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice(value = "ltd.xiaomizha.xuyou")
public class GlobalExceptionHandler {

    // /**
    //  * 拦截全局异常
    //  */
    // @ExceptionHandler(Exception.class)
    // public ResponseResult<Void> handler(Exception e) {
    //     log.error("全局异常拦截: ", e);
    //     return build(e);
    // }
    //
    // /**
    //  * 针对不同的异常类型构建不同的状态码、错误信息
    //  */
    // private ResponseResult<Void> build(Exception e) {
    //     ResponseResult<Void> result = ResponseResult.error();
    //     if (e instanceof NoResourceFoundException) {
    //         // 访问资源不存在
    //         result.build(ResultEnum.NOT_FOUND);
    //     } else if (e instanceof CommonException com) {
    //         // 通用业务异常
    //         result.code(com.getCode()).message(com.getMessage());
    //     }
    //     return result;
    // }

    /**
     * 处理通用业务异常
     */
    @ExceptionHandler(CommonException.class)
    public ResponseResult<Void> handleCommonException(CommonException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseResult.error()
                .code(e.getCode())
                .message(e.getMessage());
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseResult<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return ResponseResult.error()
                .code(ResultEnum.NOT_FOUND.getCode())
                .message(ResultEnum.NOT_FOUND.getMessage());
    }

    /**
     * 处理参数校验异常(@Validated 方法参数校验)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));

        log.warn("参数校验异常: {}", message);
        return ResponseResult.error()
                .code(ResultEnum.PARAM_VALIDATION_ERROR.getCode())
                .message(message.isEmpty() ? ResultEnum.PARAM_VALIDATION_ERROR.getMessage() : message);
    }

    /**
     * 处理参数绑定异常(@Valid 对象校验)
     */
    @ExceptionHandler(BindException.class)
    public ResponseResult<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));

        log.warn("参数绑定异常: {}", message);
        return ResponseResult.error()
                .code(ResultEnum.PARAM_BIND_ERROR.getCode())
                .message(message.isEmpty() ? ResultEnum.PARAM_BIND_ERROR.getMessage() : message);
    }

    /**
     * 处理方法参数校验异常(@Valid 请求体校验)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));

        log.warn("请求参数校验异常: {}", message);
        return ResponseResult.error()
                .code(ResultEnum.PARAM_VALIDATION_ERROR.getCode())
                .message(message.isEmpty() ? ResultEnum.PARAM_VALIDATION_ERROR.getMessage() : message);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseResult<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: 参数名={}, 期望类型={}, 实际值={}",
                e.getName(), e.getRequiredType(), e.getValue());

        String message = String.format("参数'%s'类型错误，期望类型为%s",
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型");

        return ResponseResult.error()
                .code(ResultEnum.PARAM_TYPE_ERROR.getCode())
                .message(message);
    }

    /**
     * 处理所有未明确捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Void> handleAllUncaughtException(Exception e) {
        log.error("系统异常: ", e);

        // 生产环境可以隐藏详细错误信息
        String errorMessage = AppConfigUtils.isProduction()
                ? ResultEnum.INTERNAL_SERVER_ERROR.getMessage()
                : buildErrorMessage(e);

        return ResponseResult.error()
                .code(ResultEnum.INTERNAL_SERVER_ERROR.getCode())
                .message(errorMessage);
    }

    /**
     * 构建错误信息
     */
    private String buildErrorMessage(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getSimpleName());

        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            sb.append(": ").append(e.getMessage());
        }

        // 更多调试信息
        if (AppConfigUtils.isDevelopment()) {
            sb.append(" [开发环境]");
        }

        return sb.toString();
    }

    /**
     * 异常转换工具
     */
    public static String convertToUserFriendlyMessage(Exception e) {
        if (e instanceof ConstraintViolationException violationException) {
            return violationException.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("; "));
        }

        if (e instanceof BindException bindException) {
            return bindException.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("; "));
        }

        if (e instanceof MethodArgumentNotValidException validException) {
            return validException.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("; "));
        }

        return e.getMessage();
    }

    /**
     * 批量处理异常信息
     * <p>
     * 适用于批量操作场景
     */
    public static String batchErrorMessage(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return "操作失败";
        }

        if (errors.size() == 1) {
            return errors.get(0);
        }

        return String.format("共%d处错误: %s", errors.size(),
                errors.stream().limit(5).collect(Collectors.joining("; ")));
    }

}
