package ltd.xiaomizha.xuyou.common.config;

import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * Druid配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties.class)
public class DruidAdConfig {
    /**
     * 去掉Druid界面底部的广告内容
     */
    @Bean
    public FilterRegistrationBean<Filter> removeAdFilter(DruidStatProperties properties) {
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        // 提取 common.js 的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String path = pattern.replaceAll("\\*", "js/common.js");
        log.info("Druid广告过滤器配置 - URL Pattern: {}, Common.js Path: {}", pattern, path);
        // 创建过滤器链对象, 添加去除广告的过滤器
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        // 创建过滤器对象
        registrationBean.setFilter(getFilter(path));
        // 指定当前过滤器拦截的路径, 支持上下文路径
        registrationBean.addUrlPatterns(path);
        registrationBean.addUrlPatterns("/*" + path);
        // log.info("Druid广告过滤器已注册, 拦截路径: {}", path);
        log.info("Druid广告过滤器已注册, 拦截路径: {}, /*{}", path, path);
        return registrationBean;
    }

    /**
     * 去除广告过滤器
     */
    private static Filter getFilter(String path) {
        return (request, response, chain) -> {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String requestURI = httpRequest.getRequestURI();
            log.info("过滤器收到请求: {}", requestURI);
            // 只处理包含 druid/js/common.js 的请求, 支持上下文路径
            // if (requestURI.endsWith("/druid/js/common.js")) {
            if (httpRequest.getRequestURI().endsWith(path)) {
                log.info("开始处理common.js请求: {}", requestURI);
                CharArrayWriter charArrayWriter = new CharArrayWriter();
                HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(httpResponse) {
                    @Override
                    public PrintWriter getWriter() {
                        return new PrintWriter(charArrayWriter);
                    }
                };
                chain.doFilter(request, responseWrapper);
                String text = charArrayWriter.toString();

                // 记录原始文本长度
                log.info("原始common.js内容长度: {}", text.length());
                // 检查是否包含广告关键词
                boolean hasBanner = text.contains("banner");
                boolean hasPoweredBy = text.contains("powered");
                boolean hasShrekWang = text.contains("shrek.wang");
                log.info("广告检测 - banner: {}, powered: {}, shrek.wang: {}",
                        hasBanner, hasPoweredBy, hasShrekWang);

                // 正则替换banner, 除去底部的广告信息
                String originalText = text;
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");

                // 检查是否有替换发生
                if (!originalText.equals(text)) {
                    log.info("广告内容已移除, 新长度: {}", text.length());
                } else {
                    log.info("未检测到广告内容或替换未生效");
                }

                // 将处理后的内容写回响应
                // httpResponse.setContentLength(text.length());
                httpResponse.setContentType("text/javascript;charset=utf-8");
                httpResponse.getWriter().write(text);

                log.info("common.js处理完成");
            } else {
                log.debug("非common.js请求, 直接放行: {}", requestURI);
                chain.doFilter(request, response);
            }
        };
    }
}
