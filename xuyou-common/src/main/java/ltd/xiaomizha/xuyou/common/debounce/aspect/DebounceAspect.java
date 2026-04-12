package ltd.xiaomizha.xuyou.common.debounce.aspect;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.debounce.annotation.InterfaceDebounce;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.exception.CommonException;
import ltd.xiaomizha.xuyou.common.utils.debounce.SpELUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 接口防抖切面类
 * <p>
 * 拦截 @InterfaceDebounce 注解的方法, 执行防抖逻辑
 */
@Slf4j
@Aspect
@Component
public class DebounceAspect {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    // 初始化Lua脚本
    private final DefaultRedisScript<Long> debounceScript;

    // 加载Lua脚本
    public DebounceAspect() {
        debounceScript = new DefaultRedisScript<>();
        // 加载 resources/lua 目录下的 debounce.lua 脚本
        debounceScript.setLocation(new ClassPathResource("lua/debounce.lua"));
        // 设置脚本返回值类型, 1: 拦截, 0: 放行
        debounceScript.setResultType(Long.class);
    }

    /**
     * 环绕通知: 拦截所有标记 @InterfaceDebounce 的方法
     */
    @Around("@annotation(interfaceDebounce)")
    public Object doDebounce(ProceedingJoinPoint joinPoint, InterfaceDebounce interfaceDebounce) throws Throwable {
        try {
            // 获取当前请求信息和方法信息
            HttpServletRequest request = getCurrentRequest();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            // 生成防抖唯一标识, key: 解析key数组, 拼接为唯一字符串
            String debounceKey = generateDebounceKey(interfaceDebounce, joinPoint, request, method);
            log.debug("接口防抖唯一标识: {}", debounceKey);
            // 转换防抖时间, 统一转为秒, 适配Lua脚本
            long timeout = interfaceDebounce.timeUnit().toSeconds(interfaceDebounce.timeout());
            // 执行Lua脚本, 原子性查锁+加锁
            Long result = stringRedisTemplate.execute(
                    debounceScript,
                    Collections.singletonList(debounceKey), // KEYS参数, 唯一标识
                    String.valueOf(timeout) // ARGV参数, 过期时间
            );
            // 判断脚本返回结果: 1: 拦截, 0: 放行
            if (result != null && result == 1) {
                throw new CommonException(ResultEnum.TOO_MANY_REQUESTS, interfaceDebounce.message());
            }
            // 放行请求, 执行原方法业务逻辑
            return joinPoint.proceed();
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            log.error("接口防抖逻辑执行失败", e);
            // 防抖逻辑异常时, 放行请求
            // 避免因防抖逻辑异常导致正常请求失败
            return joinPoint.proceed();
        }
    }

    /**
     * 生成防抖唯一标识
     */
    private String generateDebounceKey(InterfaceDebounce debounce, ProceedingJoinPoint joinPoint, HttpServletRequest request, Method method) {
        String[] keyArray = debounce.key();

        if (keyArray != null && keyArray.length > 0) {
            return parseSpELArray(keyArray, joinPoint, method);
        }

        String ip = getClientIp(request);
        String methodFullName = method.getDeclaringClass().getName() + "." + method.getName();
        return StrUtil.format("debounce:{}:{}", ip, methodFullName);
    }

    /**
     * 解析SpEL表达式数组
     */
    private String parseSpELArray(String[] spELArray, ProceedingJoinPoint joinPoint, Method method) {
        return Arrays.stream(spELArray)
                .map(spEL -> {
                    try {
                        return SpELUtil.parse(spEL, joinPoint, method);
                    } catch (Exception e) {
                        log.error("SpEL表达式解析失败, 表达式: {}", spEL, e);
                        return spEL;
                    }
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining("-"));
    }

    /**
     * 获取当前请求对象
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("获取当前请求失败");
        }
        return attributes.getRequest();
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}
