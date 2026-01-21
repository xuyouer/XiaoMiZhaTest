package ltd.xiaomizha.xuyou.common.response;

import lombok.Getter;
import lombok.Setter;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;

import java.io.Serializable;
import java.util.Locale;

@Getter
@Setter
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 状态码
     */
    private int code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 时间戳
     */
    private long timestamp;

    public ResponseResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public static ResponseResult<Void> ok() {
        return build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }

    public static ResponseResult<Void> ok(String message) {
        return build(ResultEnum.SUCCESS.getCode(), message, null);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    public static ResponseResult<Void> error() {
        return build(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), ResultEnum.INTERNAL_SERVER_ERROR.getMessage(), null);
    }

    public static ResponseResult<Void> error(String message) {
        return build(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    /**
     * 构建成功响应
     */
    public static <T> ResponseResult<T> success() {
        return success((T) null);
    }

    /**
     * 构建成功响应
     */
    public static <T> ResponseResult<T> success(T data) {
        return success(data, Locale.getDefault());
    }

    /**
     * 构建成功响应
     *
     * @param data   响应数据
     * @param locale 语言环境
     */
    public static <T> ResponseResult<T> success(T data, Locale locale) {
        return of(ResultEnum.SUCCESS, data, locale);
    }

    /**
     * 构建失败响应
     */
    public static <T> ResponseResult<T> errorOrEnum() {
        return error(ResultEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 构建失败响应
     *
     * @param resultEnum 结果枚举
     */
    public static <T> ResponseResult<T> error(ResultEnum resultEnum) {
        return of(resultEnum, null, Locale.getDefault());
    }

    /**
     * 构建失败响应
     *
     * @param resultEnum 结果枚举
     * @param locale     语言环境
     */
    public static <T> ResponseResult<T> error(ResultEnum resultEnum, Locale locale) {
        return of(resultEnum, null, locale);
    }

    /**
     * 构建失败响应
     *
     * @param code    状态码
     * @param message 错误信息
     */
    public static <T> ResponseResult<T> error(int code, String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.code = code;
        result.message = message;
        return result;
    }

    /**
     * 链式设置状态码
     */
    public ResponseResult<T> code(int code) {
        this.code = code;
        return this;
    }

    /**
     * 链式设置消息
     */
    public ResponseResult<T> message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 链式设置数据
     */
    public ResponseResult<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * 根据ResultEnum构建响应
     *
     * @param resultEnum 结果枚举
     * @param data       响应数据
     * @param locale     语言环境
     */
    public static <T> ResponseResult<T> of(ResultEnum resultEnum, T data, Locale locale) {
        ResponseResult<T> result = new ResponseResult<>();
        result.code = resultEnum.getCode();
        result.message = resultEnum.getMessage(locale);
        result.data = data;
        return result;
    }

    /**
     * 根据状态码构建响应
     *
     * @param code   状态码
     * @param locale 语言环境
     */
    public static <T> ResponseResult<T> ofCode(int code, Locale locale) {
        ResultEnum resultEnum = ResultEnum.getByCode(code);
        if (resultEnum == null) {
            return error(code, "未知错误");
        }
        return of(resultEnum, null, locale);
    }

    /**
     * 根据状态码构建响应
     *
     * @param code   状态码
     * @param data   响应数据
     * @param locale 语言环境
     */
    public static <T> ResponseResult<T> ofCode(int code, T data, Locale locale) {
        ResultEnum resultEnum = ResultEnum.getByCode(code);
        if (resultEnum == null) {
            return error(code, "未知错误");
        }
        return of(resultEnum, data, locale);
    }

    /**
     * 快速创建响应对象(使用默认语言)
     *
     * @param resultEnum 结果枚举
     */
    public static <T> ResponseResult<T> of(ResultEnum resultEnum) {
        return of(resultEnum, null, Locale.getDefault());
    }

    /**
     * 快速创建响应对象
     *
     * @param resultEnum 结果枚举
     * @param data       响应数据
     */
    public static <T> ResponseResult<T> of(ResultEnum resultEnum, T data) {
        return of(resultEnum, data, Locale.getDefault());
    }

    private static <T> ResponseResult<T> build(int code, String message, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public void build(ResultEnum resultEnum) {
        this.code(resultEnum.getCode());
        this.message(resultEnum.getMessage());
    }

    /**
     * 判断响应是否成功
     */
    public boolean isSuccess() {
        return this.code == ResultEnum.SUCCESS.getCode();
    }

    /**
     * 根据ResultEnum设置响应信息
     */
    public ResponseResult<T> applyResultEnum(ResultEnum resultEnum) {
        return applyResultEnum(resultEnum, Locale.getDefault());
    }

    /**
     * 根据ResultEnum设置响应信息
     */
    public ResponseResult<T> applyResultEnum(ResultEnum resultEnum, Locale locale) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage(locale);
        return this;
    }

    /**
     * 创建一个带自定义消息的成功响应
     */
    public static <T> ResponseResult<T> successWithMessage(String customMessage, T data) {
        ResponseResult<T> result = success(data);
        result.message = customMessage;
        return result;
    }

    /**
     * 创建一个带自定义消息的失败响应
     */
    public static <T> ResponseResult<T> errorWithMessage(ResultEnum resultEnum, String customMessage) {
        ResponseResult<T> result = error(resultEnum);
        result.message = customMessage;
        return result;
    }

    /**
     * 创建分页成功响应
     */
    public static <T> ResponseResult<T> pageSuccess(T pageData) {
        return success(pageData);
    }

    /**
     * 转换为字符串表示
     */
    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", success=" + isSuccess() +
                '}';
    }
}
