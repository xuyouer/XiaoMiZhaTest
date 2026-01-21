package ltd.xiaomizha.xuyou.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum ResultEnum {
    // 1xx Informational
    CONTINUE(100, "继续", "Continue"),
    SWITCHING_PROTOCOLS(101, "切换协议", "Switching Protocols"),
    PROCESSING(102, "处理中", "Processing"),

    // 2xx Success
    SUCCESS(200, "操作成功", "Success"),
    CREATED(201, "资源创建成功", "Created"),
    ACCEPTED(202, "请求已接受", "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "非授权信息", "Non-Authoritative Information"),
    NO_CONTENT(204, "操作成功, 无返回内容", "No Content"),
    RESET_CONTENT(205, "重置内容", "Reset Content"),
    PARTIAL_CONTENT(206, "部分内容", "Partial Content"),
    MULTI_STATUS(207, "多状态", "Multi-Status"),
    ALREADY_REPORTED(208, "已报告", "Already Reported"),
    IM_USED(226, "IM 已使用", "IM Used"),

    // 3xx Redirection
    MULTIPLE_CHOICES(300, "多种选择", "Multiple Choices"),
    MOVED_PERMANENTLY(301, "资源已永久重定向", "Moved Permanently"),
    FOUND(302, "资源临时重定向", "Found"),
    SEE_OTHER(303, "参见其他", "See Other"),
    NOT_MODIFIED(304, "资源未修改", "Not Modified"),
    USE_PROXY(305, "使用代理", "Use Proxy"),
    TEMPORARY_REDIRECT(307, "临时重定向", "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "永久重定向", "Permanent Redirect"),

    // 4xx Client Error
    BAD_REQUEST(400, "请求参数错误", "Bad Request"),
    UNAUTHORIZED(401, "未授权", "Unauthorized"),
    PAYMENT_REQUIRED(402, "需要支付", "Payment Required"),
    FORBIDDEN(403, "禁止访问", "Forbidden"),
    NOT_FOUND(404, "请求资源不存在", "Not Found"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许", "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "不可接受", "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "需要代理认证", "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "请求超时", "Request Timeout"),
    CONFLICT(409, "资源冲突", "Conflict"),
    GONE(410, "资源已删除", "Gone"),
    LENGTH_REQUIRED(411, "需要内容长度", "Length Required"),
    PRECONDITION_FAILED(412, "先决条件失败", "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, "请求体过大", "Payload Too Large"),
    URI_TOO_LONG(414, "URI过长", "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型", "Unsupported Media Type"),
    RANGE_NOT_SATISFIABLE(416, "请求范围不满足", "Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "期望失败", "Expectation Failed"),
    IM_A_TEAPOT(418, "我是茶壶", "I'm a teapot"),
    MISDIRECTED_REQUEST(421, "错误的请求", "Misdirected Request"),
    UNPROCESSABLE_ENTITY(422, "不可处理的实体", "Unprocessable Entity"),
    LOCKED(423, "资源已锁定", "Locked"),
    FAILED_DEPENDENCY(424, "失败的依赖", "Failed Dependency"),
    TOO_EARLY(425, "过早", "Too Early"),
    UPGRADE_REQUIRED(426, "需要升级", "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "需要先决条件", "Precondition Required"),
    TOO_MANY_REQUESTS(429, "请求过于频繁", "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "请求头字段过大", "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "因法律原因不可用", "Unavailable For Legal Reasons"),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(500, "服务器异常", "Internal Server Error"),
    NOT_IMPLEMENTED(501, "功能未实现", "Not Implemented"),
    BAD_GATEWAY(502, "网关错误", "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "服务不可用", "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "网关超时", "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP版本不支持", "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "变体协商", "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "存储空间不足", "Insufficient Storage"),
    LOOP_DETECTED(508, "检测到循环", "Loop Detected"),
    NOT_EXTENDED(510, "未扩展", "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "需要网络认证", "Network Authentication Required"),

    // 业务状态码 (1000-1999: 通用业务错误)
    PARAM_VALIDATION_ERROR(1000, "参数校验失败", "Param validation error"),
    PARAM_BIND_ERROR(1001, "参数绑定失败", "Param bind error"),
    PARAM_TYPE_ERROR(1002, "参数类型失败", "Param bind error"),
    BUSINESS_ERROR(1003, "业务异常", "Business error"),
    DATA_NOT_EXIST(1004, "数据不存在", "Data not exist"),
    DATA_DUPLICATE(1005, "数据重复", "Data duplicate"),
    OPERATION_TOO_FREQUENT(1006, "操作过于频繁", "Operation too frequent"),
    RESOURCE_LOCKED(1007, "资源已被锁定", "Resource locked"),
    INSUFFICIENT_BALANCE(1008, "余额不足", "Insufficient balance"),
    PERMISSION_DENIED(1009, "权限不足", "Permission denied"),
    INVALID_TOKEN(1010, "无效令牌", "Invalid token"),
    TOKEN_EXPIRED(1011, "令牌已过期", "Token expired"),
    CAPTCHA_ERROR(1012, "验证码错误", "Captcha error"),
    FILE_UPLOAD_ERROR(1013, "文件上传失败", "File upload error"),
    FILE_SIZE_EXCEEDED(1014, "文件大小超过限制", "File size exceeded"),
    FILE_TYPE_NOT_ALLOWED(1015, "文件类型不允许", "File type not allowed"),
    THIRD_PARTY_SERVICE_ERROR(1016, "第三方服务异常", "Third party service error"),
    DATABASE_ERROR(1017, "数据库异常", "Database error"),
    CACHE_ERROR(1018, "缓存异常", "Cache error"),
    NETWORK_ERROR(1019, "网络异常", "Network error"),
    CONFIGURATION_ERROR(1020, "配置错误", "Configuration error"),

    // 2000-2999: 用户相关
    USER_NOT_FOUND(2000, "用户不存在", "User not found"),
    USER_DISABLED(2001, "用户已被禁用", "User disabled"),
    USER_LOCKED(2002, "用户已被锁定", "User locked"),
    PASSWORD_ERROR(2003, "密码错误", "Password error"),
    OLD_PASSWORD_ERROR(2004, "原密码错误", "Old password error"),
    NEW_PASSWORD_SAME(2005, "新密码不能与原密码相同", "New password cannot be same as old"),
    USER_EXISTS(2006, "用户已存在", "User already exists"),
    PHONE_EXISTS(2007, "手机号已存在", "Phone number already exists"),
    EMAIL_EXISTS(2008, "邮箱已存在", "Email already exists"),

    // 3000-3999: 订单/交易相关
    ORDER_NOT_FOUND(3000, "订单不存在", "Order not found"),
    ORDER_STATUS_ERROR(3001, "订单状态异常", "Order status error"),
    ORDER_CANCELED(3002, "订单已取消", "Order canceled"),
    ORDER_EXPIRED(3003, "订单已过期", "Order expired"),
    PAYMENT_FAILED(3004, "支付失败", "Payment failed"),
    PAYMENT_AMOUNT_MISMATCH(3005, "支付金额不匹配", "Payment amount mismatch"),
    REFUND_FAILED(3006, "退款失败", "Refund failed"),
    INSUFFICIENT_INVENTORY(3007, "库存不足", "Insufficient inventory"),

    // 4000-4999: 系统/配置相关
    SYSTEM_BUSY(4000, "系统繁忙", "System busy"),
    SYSTEM_MAINTENANCE(4001, "系统维护中", "System maintenance"),
    FEATURE_DISABLED(4002, "功能已禁用", "Feature disabled"),
    LICENSE_EXPIRED(4003, "许可证已过期", "License expired"),
    LICENSE_INVALID(4004, "许可证无效", "License invalid");

    private final int code;
    private final String zhMsg;
    private final String enMsg;

    ResultEnum(int code, String zhMsg, String enMsg) {
        this.code = code;
        this.zhMsg = zhMsg;
        this.enMsg = enMsg;
    }

    /**
     * 获取默认消息(中文)
     */
    public String getMessage() {
        return zhMsg;
    }

    /**
     * 根据Locale获取消息
     */
    public String getMessage(Locale locale) {
        if (Locale.ENGLISH.equals(locale)) {
            return enMsg;
        }
        return zhMsg;
    }

    /**
     * 通过code查找枚举
     *
     * @param code 状态码
     * @return 对应的枚举, 未找到则返回null
     */
    public static ResultEnum getByCode(int code) {
        for (ResultEnum result : values()) {
            if (result.code == code) {
                return result;
            }
        }
        return null; // 或抛出 IllegalArgumentException
    }

    /**
     * 通过code查找枚举(使用缓存)
     *
     * @param code 状态码
     * @return 对应的枚举, 未找到则返回null
     */
    public static ResultEnum getByCodeCache(int code) {
        return CACHE.get(code);
    }

    /**
     * 通过code查找枚举(使用缓存), 严格模式
     *
     * @param code 状态码
     * @return 对应的枚举
     * @throws IllegalArgumentException 如果未找到对应的枚举
     */
    public static ResultEnum getByCodeCacheOrThrow(int code) {
        ResultEnum result = CACHE.get(code);
        if (result == null) {
            throw new IllegalArgumentException("Invalid result code: " + code);
        }
        return result;
    }

    /**
     * 通过code获取消息(自动识别语言)
     *
     * @param code 状态码
     * @return 对应的消息, 未找到则返回null
     */
    public static String getMessageByCode(int code) {
        return getMessageByCode(code, Locale.getDefault());
    }

    /**
     * 通过code获取指定语言消息
     *
     * @param code   状态码
     * @param locale 语言环境
     * @return 对应的消息, 未找到则返回null
     */
    public static String getMessageByCode(int code, Locale locale) {
        ResultEnum result = getByCode(code);
        return result != null ? result.getMessage(locale) : null;
    }

    /**
     * 通过code获取zhMsg
     *
     * @param code 状态码
     * @return 对应的消息, 未找到则返回null
     */
    public static String getZhMsgByCode(int code) {
        ResultEnum result = getByCodeCache(code);
        return result != null ? result.getZhMsg() : null;
    }

    /**
     * 通过code获取enMsg
     *
     * @param code 状态码
     * @return 对应的消息, 未找到则返回null
     */
    public static String getEnMsgByCode(int code) {
        ResultEnum result = getByCodeCache(code);
        return result != null ? result.getEnMsg() : null;
    }

    /**
     * 判断是否为成功状态码
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    /**
     * 判断是否为客户端错误
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    /**
     * 判断是否为服务器错误
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }

    /**
     * 判断是否为业务状态码
     */
    public boolean isBusinessCode() {
        return code >= 1000;
    }

    /**
     * 判断是否为信息性状态码
     */
    public boolean isInformational() {
        return code >= 100 && code < 200;
    }

    /**
     * 判断是否为重定向状态码
     */
    public boolean isRedirection() {
        return code >= 300 && code < 400;
    }

    /**
     * 获取状态码分类
     *
     * @return 状态码对应的分类枚举
     */
    public StatusCategory getCategory() {
        if (code >= 100 && code < 200) return StatusCategory.INFORMATIONAL;
        if (code >= 200 && code < 300) return StatusCategory.SUCCESS;
        if (code >= 300 && code < 400) return StatusCategory.REDIRECTION;
        if (code >= 400 && code < 500) return StatusCategory.CLIENT_ERROR;
        if (code >= 500 && code < 600) return StatusCategory.SERVER_ERROR;
        if (code >= 1000 && code < 2000) return StatusCategory.BUSINESS_ERROR;
        if (code >= 2000 && code < 3000) return StatusCategory.USER_ERROR;
        if (code >= 3000 && code < 4000) return StatusCategory.ORDER_ERROR;
        if (code >= 4000 && code < 5000) return StatusCategory.SYSTEM_ERROR;
        return StatusCategory.UNKNOWN;
    }

    /**
     * 状态码分类枚举
     */
    public enum StatusCategory {
        INFORMATIONAL,      // 1xx
        SUCCESS,           // 2xx
        REDIRECTION,       // 3xx
        CLIENT_ERROR,      // 4xx
        SERVER_ERROR,      // 5xx
        BUSINESS_ERROR,    // >=1xxx
        USER_ERROR,    // >=2xxx
        ORDER_ERROR,    // >=3xxx
        SYSTEM_ERROR,    // >=4xxx
        UNKNOWN
    }

    /**
     * 缓存提升查找性能
     */
    private static final Map<Integer, ResultEnum> CACHE = Collections.unmodifiableMap(
            Arrays.stream(values()).collect(Collectors.toMap(ResultEnum::getCode, e -> e))
    );

}