package ltd.xiaomizha.xuyou.common.utils.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * 请求包装类
 * <p>
 * 用于添加或修改请求头
 */
public class HeaderModifiableRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String> customHeaders;

    public HeaderModifiableRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        if (headerValue != null) {
            return headerValue;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> headerNames = new HashSet<>(Collections.list(super.getHeaderNames()));
        headerNames.addAll(customHeaders.keySet());
        return Collections.enumeration(headerNames);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> headers = new ArrayList<>(Collections.list(super.getHeaders(name)));
        if (customHeaders.containsKey(name)) {
            headers.add(customHeaders.get(name));
        }
        return Collections.enumeration(headers);
    }
}
