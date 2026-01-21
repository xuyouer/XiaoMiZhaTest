package ltd.xiaomizha.xuyou.common.utils.http;

import org.springframework.core.env.Environment;

import java.net.InetAddress;

public class ServiceUtils {

    /**
     * 获取服务器IP地址
     *
     * @return 服务器IP地址
     */
    public static String getServerIp() throws Exception {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获取服务器端口
     *
     * @param env 环境变量
     * @return 服务器端口
     */
    public static String getServerPort(Environment env) {
        return env.getProperty("server.port");
    }

    /**
     * 获取服务器上下文路径
     *
     * @param env          环境变量
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 上下文路径
     */
    public static String getServerContextPath(Environment env, String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    /**
     * 获取服务器上下文路径: server.servlet.context-path
     *
     * @param env 环境变量
     * @return 上下文路径
     */
    public static String getServerContextPath(Environment env) {
        return getServerContextPath(env, "server.servlet.context-path", "");
    }

    /**
     * 获取服务器信息
     *
     * @param env 环境变量
     * @return String数组, 包含[ip, port, contextPath: server.servlet.context-path]
     */
    public static String[] getServerInfo(Environment env) throws Exception {
        return new String[]{getServerIp(), getServerPort(env), getServerContextPath(env)};
    }

    /**
     * 获取本地地址
     *
     * @param port        端口
     * @param contextPath 上下文路径
     * @return 本地地址
     */
    public static String getLocalAddress(String port, String contextPath) {
        return String.format("http://localhost:%s%s", port, contextPath);
    }

    /**
     * 获取网络地址
     *
     * @param ip          IP地址
     * @param port        端口
     * @param contextPath 上下文路径
     * @return 网络地址
     */
    public static String getNetworkAddress(String ip, String port, String contextPath) {
        return String.format("http://%s:%s%s", ip, port, contextPath);
    }
}
