package ltd.xiaomizha.xuyou.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;

@Slf4j
@Getter
public class CommonException extends RuntimeException {

    private final int code;
    private final String message;

    private String formattedMessage;

    public CommonException(String message) {
        this(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), message, new Object[0]);
    }

    public CommonException(String message, Object... args) {
        this(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), message, args);
    }

    public CommonException(int code, String message, Object... args) {
        super(formatMessage(message, args));
        this.code = code;
        this.message = message;
        this.formattedMessage = formatMessage(message, args);
    }

    public CommonException(ResultEnum resultEnum) {
        this(resultEnum.getCode(), resultEnum.getMessage(), new Object[0]);
    }

    public CommonException(ResultEnum resultEnum, Object... args) {
        this(resultEnum.getCode(), resultEnum.getMessage(), args);
    }

    @Override
    public String getMessage() {
        return formattedMessage != null ? formattedMessage : super.getMessage();
    }

    private static String formatMessage(String message, Object... args) {
        if (args != null && args.length > 0) {
            return StrUtil.format(message, args);
        }
        return message;
    }

    /**
     * 快速构建异常
     */
    public static CommonException of(int code, String message, Object... args) {
        return new CommonException(code, message, args);
    }

    /**
     * 快速构建异常
     * <p>
     * 使用默认错误码
     */
    public static CommonException of(String message, Object... args) {
        return new CommonException(message, args);
    }

    /**
     * 根据枚举构建异常
     */
    public static CommonException of(ResultEnum resultEnum) {
        return new CommonException(resultEnum);
    }

    /**
     * 根据枚举构建异常
     * <p>
     * 带格式化参数
     */
    public static CommonException of(ResultEnum resultEnum, Object... args) {
        return new CommonException(resultEnum, args);
    }
}
