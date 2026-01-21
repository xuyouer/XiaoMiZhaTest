package ltd.xiaomizha.xuyou.common.utils.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

public class LocaleUtils {

    /**
     * 获取当前请求的Locale
     */
    public static Locale getCurrentLocale() {
        // 首先尝试从LocaleContextHolder获取
        Locale locale = LocaleContextHolder.getLocale();
        if (locale != null) {
            return locale;
        }

        // 如果没有, 尝试从ServletRequest获取
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 从请求头获取
            String acceptLanguage = request.getHeader("Accept-Language");
            if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
                return parseAcceptLanguage(acceptLanguage);
            }
        }

        // 返回系统默认
        return Locale.getDefault();
    }

    /**
     * 解析Accept-Language
     */
    public static Locale parseAcceptLanguage(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isEmpty()) {
            return Locale.getDefault();
        }

        try {
            // 取第一个语言
            // "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7"
            // String[] languages = acceptLanguage.split(",");
            // if (languages.length > 0) {
            //     String language = languages[0].trim().split(";")[0];
            //
            //     // 解析语言和国家/地区
            //     if (language.contains("-")) {
            //         String[] parts = language.split("-");
            //         return new Locale(parts[0], parts[1]);
            //     } else {
            //         return new Locale(language);
            //     }
            // }
            String firstLanguage = acceptLanguage.split(",")[0].trim().split(";")[0];
            String[] parts = firstLanguage.split("-|_");

            if (parts.length == 1) {
                return new Locale(parts[0]);
            } else if (parts.length >= 2) {
                return new Locale(parts[0], parts[1]);
            }
        } catch (Exception e) {
            // 解析失败, 使用默认
        }

        return Locale.getDefault();
    }

    /**
     * 判断是否是英文环境
     */
    public static boolean isEnglish() {
        Locale locale = getCurrentLocale();
        return Locale.ENGLISH.equals(locale) ||
                locale.getLanguage().startsWith("en");
    }

    /**
     * 判断是否是中文环境
     */
    public static boolean isChinese() {
        Locale locale = getCurrentLocale();
        return Locale.CHINESE.equals(locale) ||
                locale.getLanguage().startsWith("zh");
    }
}