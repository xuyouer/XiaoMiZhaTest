package ltd.xiaomizha.xuyou.common.utils.http;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.ServletConstants;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Servlet请求响应工具类
 */
@Slf4j
public class ServletUtil {

    /**
     * 获取请求对象
     *
     * @return HttpServletRequest 对象
     * @throws IllegalStateException 如果当前不是Web上下文
     */
    public static HttpServletRequest getRequest() {
        return getServletRequestAttributes()
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow(() -> new IllegalStateException(ServletConstants.NON_WEB_CONTEXT_MSG));
    }

    /**
     * 获取响应对象
     *
     * @return HttpServletResponse 对象
     * @throws IllegalStateException 如果当前不是Web上下文
     */
    public static HttpServletResponse getResponse() {
        return getServletRequestAttributes()
                .map(ServletRequestAttributes::getResponse)
                .orElseThrow(() -> new IllegalStateException(ServletConstants.NON_WEB_CONTEXT_MSG));
    }

    /**
     * 安全获取请求对象
     *
     * @return Optional包装的HttpServletRequest, 为空表示不在Web上下文
     */
    public static Optional<HttpServletRequest> getRequestOptional() {
        return getServletRequestAttributes()
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * 安全获取响应对象
     *
     * @return Optional包装的HttpServletResponse, 为空表示不在Web上下文
     */
    public static Optional<HttpServletResponse> getResponseOptional() {
        return getServletRequestAttributes()
                .map(ServletRequestAttributes::getResponse);
    }

    /**
     * 获取请求头参数
     *
     * @param headerName 请求头名称
     * @return 请求头参数值, 不存在返回null
     */
    @Nullable
    public static String getHeader(String headerName) {
        if (StrUtil.isBlank(headerName)) {
            return null;
        }

        return getRequestOptional()
                .map(request -> request.getHeader(headerName))
                .orElse(null);
    }

    /**
     * 安全获取请求头参数
     *
     * @param headerName 请求头名称
     * @return Optional包装的请求头参数值
     */
    public static Optional<String> getHeaderOptional(String headerName) {
        if (StrUtil.isBlank(headerName)) {
            return Optional.empty();
        }

        return getRequestOptional()
                .map(request -> request.getHeader(headerName))
                .filter(StrUtil::isNotBlank);
    }

    /**
     * 获取请求参数
     *
     * @param parameterName 参数名称
     * @return 参数值, 不存在返回null
     */
    @Nullable
    public static String getParameter(String parameterName) {
        if (StrUtil.isBlank(parameterName)) {
            return null;
        }

        return getRequestOptional()
                .map(request -> request.getParameter(parameterName))
                .orElse(null);
    }

    /**
     * 安全获取请求参数
     *
     * @param parameterName 参数名称
     * @return Optional包装的参数值
     */
    public static Optional<String> getParameterOptional(String parameterName) {
        if (StrUtil.isBlank(parameterName)) {
            return Optional.empty();
        }

        return getRequestOptional()
                .map(request -> request.getParameter(parameterName))
                .filter(StrUtil::isNotBlank);
    }

    /**
     * 从请求中获取参数
     * <p>
     * 优先级: 参数 > 请求头 > Cookie
     *
     * @param paramName 参数名称
     * @return 参数值, 不存在返回null
     */
    @Nullable
    public static String getParameterFromRequest(String paramName) {
        if (StrUtil.isBlank(paramName)) {
            return null;
        }

        return getRequestOptional()
                .map(request -> getParameterFromRequest(request, paramName))
                .orElse(null);
    }

    /**
     * 从指定请求中获取参数
     * <p>
     * 优先级: 参数 > 请求头 > Cookie
     *
     * @param request   请求对象
     * @param paramName 参数名称
     * @return 参数值, 不存在返回null
     */
    @Nullable
    public static String getParameterFromRequest(HttpServletRequest request, String paramName) {
        if (request == null || StrUtil.isBlank(paramName)) {
            return null;
        }

        // 1. 从请求参数获取
        String paramValue = request.getParameter(paramName);
        if (StrUtil.isNotBlank(paramValue)) {
            return paramValue;
        }

        // 2. 从请求头获取
        paramValue = request.getHeader(paramName);
        if (StrUtil.isNotBlank(paramValue)) {
            return paramValue;
        }

        // 3. 从Cookie获取
        Cookie[] cookies = request.getCookies();
        if (ObjectUtil.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (paramName.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    return StrUtil.isNotBlank(value) ? value : null;
                }
            }
        }

        return null;
    }

    /**
     * 判断当前是否处于Web上下文
     *
     * @return true: 在Web上下文, false: 不在Web上下文
     */
    public static boolean isWebContext() {
        return getServletRequestAttributes().isPresent();
    }

    /**
     * 获取当前请求的IP地址
     *
     * @return IP地址, 获取失败返回空字符串
     */
    public static String getClientIp() {
        return getRequestOptional()
                .map(ServletUtil::getClientIp)
                .orElse("");
    }

    /**
     * 获取请求的IP地址
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        // 使用常量类中的IP头信息
        for (String header : ServletConstants.IP_HEADERS) {
            String ip = request.getHeader(header);
            if (StrUtil.isNotBlank(ip) && !ServletConstants.UNKNOWN_IP.equalsIgnoreCase(ip)) {
                // 对于X-Forwarded-For, 取第一个IP
                if ("X-Forwarded-For".equals(header)) {
                    int index = ip.indexOf(',');
                    return (index != -1) ? ip.substring(0, index).trim() : ip.trim();
                }
                return ip.trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 获取请求的完整URL
     *
     * @return 完整URL
     */
    public static String getFullUrl() {
        return getRequestOptional()
                .map(ServletUtil::getFullUrl)
                .orElse("");
    }

    /**
     * 获取请求的完整URL
     *
     * @param request 请求对象
     * @return 完整URL
     */
    public static String getFullUrl(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String queryString = request.getQueryString();
        String requestURI = request.getRequestURI();

        if (StrUtil.isNotBlank(queryString)) {
            return requestURI + "?" + queryString;
        }
        return requestURI;
    }

    /**
     * 获取当前请求的User-Agent
     *
     * @return User-Agent
     */
    public static String getUserAgent() {
        return getHeaderOptional(ServletConstants.Headers.USER_AGENT).orElse("");
    }

    /**
     * 获取当前请求的Referer
     *
     * @return Referer
     */
    public static String getReferer() {
        return getHeaderOptional(ServletConstants.Headers.REFERER).orElse("");
    }

    /**
     * 获取当前请求的Content-Type
     *
     * @return Content-Type
     */
    public static String getContentType() {
        return getHeaderOptional(ServletConstants.Headers.CONTENT_TYPE).orElse("");
    }

    /**
     * 判断是否为JSON请求
     *
     * @return
     */
    public static boolean isJsonRequest() {
        String contentType = getContentType();
        return contentType.startsWith(ServletConstants.Headers.CONTENT_TYPE_JSON);
    }

    /**
     * 获取当前请求的方法
     *
     * @return 请求方法
     */
    public static String getMethod() {
        return getRequestOptional()
                .map(HttpServletRequest::getMethod)
                .orElse("");
    }

    /**
     * 获取ServletRequestAttributes
     *
     * @return Optional包装的ServletRequestAttributes
     */
    private static Optional<ServletRequestAttributes> getServletRequestAttributes() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            return Optional.ofNullable(attributes);
        } catch (Exception e) {
            log.debug("获取ServletRequestAttributes失败: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
