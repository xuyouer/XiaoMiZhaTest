package ltd.xiaomizha.xuyou.common.constant;

/**
 * Servlet相关常量
 * <p>
 * 提供Servlet相关的常量定义
 */
public final class ServletConstants {

    private ServletConstants() {
        throw new AssertionError("工具类不允许实例化");
    }

    /**
     * 非Web上下文错误消息
     */
    public static final String NON_WEB_CONTEXT_MSG = "非Web上下文, 无法获取Servlet对象";

    /**
     * 未知IP标识
     */
    public static final String UNKNOWN_IP = "unknown";

    /**
     * 常见的IP请求头
     */
    public static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    /**
     * 常见的HTTP请求头常量
     */
    public static final class Headers {
        private Headers() {
        }

        public static final String USER_AGENT = "User-Agent";
        public static final String REFERER = "Referer";
        public static final String AUTHORIZATION = "Authorization";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";
        public static final String CONTENT_LENGTH = "Content-Length";

        // 常用Content-Type
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
        public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
        public static final String CONTENT_TYPE_XML = "application/xml";
        public static final String CONTENT_TYPE_TEXT = "text/plain";
    }

    /**
     * HTTP方法常量
     */
    public static final class Methods {
        private Methods() {
        }

        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
        public static final String PATCH = "PATCH";
        public static final String HEAD = "HEAD";
        public static final String OPTIONS = "OPTIONS";
    }

    /**
     * Cookie相关常量
     */
    public static final class Cookies {
        private Cookies() {
        }

        public static final int MAX_AGE_SESSION = -1;  // 会话级cookie
        public static final int MAX_AGE_DELETE = 0;    // 立即删除

        // 常用cookie名称
        public static final String SESSION_ID = "JSESSIONID";
        public static final String TOKEN = "token";
        public static final String REMEMBER_ME = "remember-me";
    }

    /**
     * 字符编码常量
     */
    public static final class Charsets {
        private Charsets() {
        }

        public static final String UTF8 = "UTF-8";
        public static final String GBK = "GBK";
        public static final String ISO88591 = "ISO-8859-1";
    }
}