package ltd.xiaomizha.xuyou.common.utils.geo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 离线IP归属地查询工具类
 * <p>
 * 基于ip2region 3.3.6版本实现, 支持IPv4和IPv6双协议查询
 */
@Slf4j
@Component
public class IpRegionUtil {

    /**
     * IPv4数据库文件路径（相对于classpath）
     */
    private static final String IPV4_XDB_PATH = "ipdb/ip2region_v4.xdb";

    /**
     * IPv6数据库文件路径（相对于classpath）
     */
    private static final String IPV6_XDB_PATH = "ipdb/ip2region_v6.xdb";

    /**
     * ip2region统一查询对象, 托管双库
     */
    private Ip2Region ip2Region;

    /**
     * 项目启动初始化, 加载ip2region双库文件
     */
    @PostConstruct
    public void init() {
        try {
            log.info("IpRegionUtil: 开始加载ip2region数据库文件...");

            // 加载IPv4数据库配置
            ClassPathResource v4Resource = new ClassPathResource(IPV4_XDB_PATH);
            InputStream v4Is = v4Resource.getInputStream();
            Config v4Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(10)
                    .setXdbInputStream(v4Is)
                    .asV4();

            // 加载IPv6数据库配置
            ClassPathResource v6Resource = new ClassPathResource(IPV6_XDB_PATH);
            InputStream v6Is = v6Resource.getInputStream();
            Config v6Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)
                    .setSearchers(10)
                    .setXdbInputStream(v6Is)
                    .asV6();

            // 统一创建ip2region查询对象
            ip2Region = Ip2Region.create(v4Config, v6Config);

            log.info("IpRegionUtil: 初始化成功, IPv4+IPv6双库加载完成");
        } catch (Exception e) {
            log.error("IpRegionUtil: 初始化失败, 请检查字体库文件是否存在: {}/{}", IPV4_XDB_PATH, IPV6_XDB_PATH, e);
            throw new RuntimeException("IpRegionUtil初始化失败, 无法提供IP查询服务。请确保已下载字体库文件到 resources/ipdb/ 目录", e);
        }
    }

    /**
     * 统一IP查询入口, 自动识别IPv4/IPv6
     * <p>
     * 完整过滤所有内网IP, 包含10段、172段、192段、本地回环、IPv6回环地址
     *
     * @param ip IP地址（IPv4或IPv6）
     * @return 归属地信息, 格式为"国家|省份|城市|ISP|编码", 内网IP返回"内网IP|内网IP"
     */
    public String getIpRegion(String ip) {
        try {
            if (ip == null || ip.isBlank()) {
                return "内网IP|内网IP";
            }

            if (isPrivateIp(ip)) {
                return "内网IP|内网IP";
            }

            return ip2Region.search(ip);
        } catch (Exception e) {
            log.error("IpRegionUtil: IP查询异常, IP: {}", ip, e);
            return "查询失败";
        }
    }

    /**
     * 判断是否为内网IP地址
     * <p>
     * 支持的判断:
     * - IPv4内网范围: 127.0.0.1（本地回环）、10.0.0.0/8、172.16.0.0/12、192.168.0.0/16
     * - IPv6内网范围: ::1 或 0:0:0:0:0:0:0:1（回环地址）
     *
     * @param ip IP地址字符串
     * @return true表示是内网IP, false表示是公网IP
     */
    public boolean isPrivateIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return true;
        }

        if (ip.contains(".")) {
            return isPrivateIpv4(ip);
        } else if (ip.contains(":")) {
            return isPrivateIpv6(ip);
        }

        return true;
    }

    /**
     * 判断是否为IPv4内网地址
     */
    private boolean isPrivateIpv4(String ip) {
        if ("127.0.0.1".equals(ip) || "localhost".equalsIgnoreCase(ip)) {
            return true;
        }

        if (ip.startsWith("192.168.")) {
            return true;
        }

        if (ip.startsWith("10.")) {
            return true;
        }

        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                try {
                    int secondSegment = Integer.parseInt(parts[1]);
                    return secondSegment >= 16 && secondSegment <= 31;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * 判断是否为IPv6内网地址（回环地址）
     */
    private boolean isPrivateIpv6(String ip) {
        return "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }

    /**
     * 获取当前请求的真实客户端IP并查询归属地
     * <p>
     * 适配Nginx反向代理场景, 优先读取 X-Real-IP 请求头
     *
     * @param request HTTP请求对象
     * @return IP归属地信息
     */
    public String getRequestIpRegion(HttpServletRequest request) {
        String realIp = request.getHeader("X-Real-IP");
        if (realIp == null || realIp.isBlank()) {
            realIp = request.getHeader("X-Forwarded-For");
            if (realIp != null && realIp.contains(",")) {
                realIp = realIp.split(",")[0].trim();
            }
        }
        if (realIp == null || realIp.isBlank()) {
            realIp = request.getRemoteAddr();
        }

        log.debug("IpRegionUtil: 获取到真实客户端IP: {}", realIp);
        return getIpRegion(realIp);
    }

    /**
     * 服务关闭, 释放ip2region资源
     */
    @PreDestroy
    public void close() {
        try {
            if (ip2Region != null) {
                ip2Region.close();
                log.info("IpRegionUtil: 资源释放完成");
            }
        } catch (Exception e) {
            log.error("IpRegionUtil: 资源释放失败", e);
        }
    }

}
