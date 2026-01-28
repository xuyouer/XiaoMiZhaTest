package ltd.xiaomizha.xuyou.common.controller;

import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;

import java.util.function.Supplier;

/**
 * 基础Controller类
 * <p>
 * 提供统一的异常处理和响应构建方法
 */
@Slf4j
public abstract class BaseController {

    /**
     * 执行操作并返回结果, 统一异常处理
     *
     * @param operation 操作描述
     * @param supplier  业务逻辑
     * @param <T>       返回类型
     * @return 响应结果
     */
    protected <T> ResponseResult<T> execute(String operation, Supplier<T> supplier) {
        try {
            T result = supplier.get();
            log.info("{}成功", operation);
            return ResponseResult.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("{}失败: 参数错误 - {}", operation, e.getMessage());
            return ResponseResult.error(ResultEnum.PARAM_VALIDATION_ERROR.getCode(), e.getMessage());
        } catch (RuntimeException e) {
            log.warn("{}失败: 业务异常 - {}", operation, e.getMessage());
            return ResponseResult.error(ResultEnum.BUSINESS_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("{}失败", operation, e);
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(),
                    "操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行无返回值的操作, 统一异常处理
     *
     * @param operation 操作描述
     * @param runnable  业务逻辑
     * @return 响应结果
     */
    protected ResponseResult<Void> execute(String operation, Runnable runnable) {
        try {
            runnable.run();
            log.info("{}成功", operation);
            return ResponseResult.ok(operation + "成功");
        } catch (IllegalArgumentException e) {
            log.warn("{}失败: 参数错误 - {}", operation, e.getMessage());
            return ResponseResult.error(ResultEnum.PARAM_VALIDATION_ERROR.getCode(), e.getMessage());
        } catch (RuntimeException e) {
            log.warn("{}失败: 业务异常 - {}", operation, e.getMessage());
            return ResponseResult.error(ResultEnum.BUSINESS_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("{}失败", operation, e);
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(),
                    "操作失败: " + e.getMessage());
        }
    }

    /**
     * 执行操作并返回结果, 带自定义成功消息
     *
     * @param operation      操作描述
     * @param successMessage 成功消息
     * @param supplier       业务逻辑
     * @param <T>            返回类型
     * @return 响应结果
     */
    protected <T> ResponseResult<T> executeWithMessage(String operation, String successMessage, Supplier<T> supplier) {
        try {
            T result = supplier.get();
            log.info("{}成功", operation);
            ResponseResult<T> response = ResponseResult.ok(result);
            response.setMessage(successMessage);
            return response;
        } catch (IllegalArgumentException e) {
            log.warn("{}失败: 参数错误 - {}", operation, e.getMessage());
            return ResponseResult.error(ResultEnum.PARAM_VALIDATION_ERROR.getCode(), e.getMessage());
        } catch (RuntimeException e) {
            log.warn("{}失败: 业务异常 - {}", operation, e.getMessage());
            return ResponseResult.error(ResultEnum.BUSINESS_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("{}失败", operation, e);
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(),
                    "操作失败: " + e.getMessage());
        }
    }

}
