package ltd.xiaomizha.xuyou.common.response;

import ltd.xiaomizha.xuyou.common.enums.ResultEnum;

/**
 * 统一响应构建工具类
 */
public class ResponseBuilder {

    private ResponseBuilder() {
    }

    /**
     * 构建带数据的成功响应
     *
     * @param data 数据对象
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(ResultEnum.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 构建无数据的成功响应
     *
     * @return 成功响应 (无数据)
     */
    public static ResponseResult<Void> success() {
        ResponseResult<Void> result = new ResponseResult<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(ResultEnum.SUCCESS.getMessage());
        return result;
    }

    /**
     * 构建自定义消息的成功响应
     *
     * @param message 成功消息
     * @return 成功响应
     */
    public static ResponseResult<Void> success(String message) {
        ResponseResult<Void> result = new ResponseResult<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 构建错误响应 (使用 ResultEnum)
     *
     * @param resultEnum 结果枚举
     * @param message    错误消息
     * @param <T>        数据类型
     * @return 错误响应
     */
    public static <T> ResponseResult<T> error(ResultEnum resultEnum, String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(resultEnum.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 构建错误响应 (使用状态码)
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误响应
     */
    public static <T> ResponseResult<T> error(int code, String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 构建错误响应 (仅消息, 使用默认错误码)
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误响应
     */
    public static <T> ResponseResult<T> error(String message) {
        return error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), message);
    }

}
