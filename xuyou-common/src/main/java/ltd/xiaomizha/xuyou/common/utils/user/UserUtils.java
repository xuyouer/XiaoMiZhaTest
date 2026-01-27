package ltd.xiaomizha.xuyou.common.utils.user;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;

/**
 * 用户相关工具类
 */
@Slf4j
public class UserUtils {

    /**
     * 密码加密
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * 验证密码
     *
     * @param password       原始密码
     * @param hashedPassword 加密后的密码
     * @return 验证结果
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * 生成唯一的创建用户名
     *
     * @return 唯一的创建用户名
     */
    public static String generateCreateName() {
        return UserConstants.CREATE_NAME_PREFIX + IdUtil.fastSimpleUUID().substring(0, 16);
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || UserConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UserConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UserConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取用户代理信息
     *
     * @param request HttpServletRequest对象
     * @return 用户代理信息
     */
    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : UserConstants.UNKNOWN;
    }

    /**
     * 从请求中获取客户端信息
     *
     * @param request HttpServletRequest对象
     * @return 包含IP、UserAgent等信息的数组
     */
    public static String[] getClientInfo(HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = getUserAgent(request);
        return new String[]{ipAddress, userAgent};
    }
}