package ltd.xiaomizha.xuyou.common.utils.printer;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.utils.http.ServiceUtils;
import ltd.xiaomizha.xuyou.common.utils.object.StringUtils;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 启动信息打印工具类
 */
@Slf4j
public class StartupPrinter {

    public static void printStartupInfo(Environment env, String... configValues) throws Exception {
        String[] serverInfo = ServiceUtils.getServerInfo(env);
        String druidUsername = ServiceUtils.getServerContextPath(env, "spring.datasource.druid.stat-view-servlet.login-username", "");
        String druidPassword = ServiceUtils.getServerContextPath(env, "spring.datasource.druid.stat-view-servlet.login-password", "");
        // 打印启动信息
        StartupPrinter.printStartupInfo(serverInfo[0], serverInfo[1], serverInfo[2], druidUsername, druidPassword, configValues);
    }

    public static void printStartupInfo(String ip, String port, String contextPath, String druidUsername, String druidPassword, String... configValues) {
        try {
            // 获取IP地址
            // String ip = InetAddress.getLocalHost().getHostAddress();
            String hostname = InetAddress.getLocalHost().getHostName();

            // 打印彩色横幅
            printColoredBanner();

            // 构建信息字符串
            String versionInfo = """
                    
                     %s %s %s                %s
                    
                    """.formatted(
                    ConsoleColor.cyan("::"),
                    ConsoleColor.green(StringUtils.getValueAt(0, configValues)),
                    ConsoleColor.cyan("::"),
                    ConsoleColor.yellow("(%s)".formatted(StringUtils.getValueAt(1, configValues)))
            );
            String info = """
                     ╔══════════════════════════════════════════════════════════════════════════════════╗
                     ║
                     ║                      🎲 %s后端服务启动成功 🎲
                     ║
                     ║         版本: Monopoly Service %s
                     ║         时间: %s
                     ║         主机: %s (%s)
                     ║
                     ║         本地地址: http://localhost:%s%s
                     ║         网络地址: http://%s:%s%s
                     ║
                     ║         API文档: http://localhost:%s%s/doc.html
                     ║         SwaggerUi: http://localhost:%s%s/swagger-ui/index.html
                     ║         健康检查: http://localhost:%s%s/actuator/health
                     ║         Druid监控(官方): http://localhost:%s%s/druid/login.html
                     ║         Druid监控(自定义): http://localhost:%s%s/monitor/druid
                     ║
                     ║         数据库监控:
                     ║               用户名: %s
                     ║               密码: %s
                     ║
                     ║         服务状态:
                     ║               %s 服务已启动
                     ║               %s 数据库连接正常
                     ║               %s 缓存服务就绪
                     ║               %s API文档可用
                     ║
                     ╚══════════════════════════════════════════════════════════════════════════════════╝
                    """.formatted(
                    StringUtils.getValueAt(0, configValues),
                    StringUtils.getValueAt(1, configValues),
                    DateUtil.date(),
                    hostname, ip,
                    port, contextPath,
                    ip, port, contextPath,
                    port, contextPath,
                    port, contextPath,
                    port, contextPath,
                    port, contextPath,
                    port, contextPath,
                    druidUsername, druidPassword,
                    ConsoleColor.green("🟢"),
                    ConsoleColor.green("🟢"),
                    ConsoleColor.green("🟢"),
                    ConsoleColor.green("🟢")
            );

            System.out.println(versionInfo + ConsoleColor.RESET);
            System.out.println(info + ConsoleColor.RESET);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印彩色横幅
     */
    private static void printColoredBanner() {
        // tmplr Classy Elite THIS
        String banner = """
                 ▄▀▀▄ ▄▀▄  ▄▀▀▀▀▄   ▄▀▀▄ ▀▄  ▄▀▀▀▀▄   ▄▀▀▄▀▀▀▄  ▄▀▀▀▀▄   ▄▀▀▀▀▄  ▄▀▀▄ ▀▀▄      ▄▀▀▀▀▄  ▄▀▀█▄▄▄▄  ▄▀▀▄▀▀▀▄  ▄▀▀▄ ▄▀▀▄  ▄▀▀█▀▄    ▄▀▄▄▄▄   ▄▀▀█▄▄▄▄\s
                █  █ ▀  █ █      █ █  █ █ █ █      █ █   █   █ █      █ █    █  █   ▀▄ ▄▀     █ █   ▐ ▐  ▄▀   ▐ █   █   █ █   █    █ █   █  █  █ █    ▌ ▐  ▄▀   ▐\s
                ▐  █    █ █      █ ▐  █  ▀█ █      █ ▐  █▀▀▀▀  █      █ ▐    █  ▐     █          ▀▄     █▄▄▄▄▄  ▐  █▀▀█▀  ▐  █    █  ▐   █  ▐  ▐ █        █▄▄▄▄▄ \s
                  █    █  ▀▄    ▄▀   █   █  ▀▄    ▄▀    █      ▀▄    ▄▀     █         █       ▀▄   █    █    ▌   ▄▀    █     █   ▄▀      █       █        █    ▌ \s
                ▄▀   ▄▀     ▀▀▀▀   ▄▀   █     ▀▀▀▀    ▄▀         ▀▀▀▀     ▄▀▄▄▄▄▄▄▀ ▄▀         █▀▀▀    ▄▀▄▄▄▄   █     █       ▀▄▀     ▄▀▀▀▀▀▄   ▄▀▄▄▄▄▀  ▄▀▄▄▄▄  \s
                █    █             █    ▐            █                    █         █          ▐       █    ▐   ▐     ▐              █       █ █     ▐   █    ▐  \s
                ▐    ▐             ▐                 ▐                    ▐         ▐                  ▐                             ▐       ▐ ▐         ▐       \s
                """;
        System.out.println(ConsoleColor.GREEN + banner + ConsoleColor.RESET);
    }

    /**
     * 添加空格以对齐
     */
    private static void appendSpaces(StringBuilder sb, int count) {
        // for (int i = 0; i < count; i++) {
        //     sb.append(" ");
        // }
        sb.append(" ".repeat(Math.max(0, count)));
    }
}