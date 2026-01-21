package ltd.xiaomizha.xuyou.common.utils.app;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VersionChecker {

    @PostConstruct
    public void checkVersions() {
        log.info("================== 版本信息检查 ==================");
        log.info("Spring Boot 版本: {}", SpringBootVersion.getVersion());
        log.info("Spring Framework 版本: {}", SpringVersion.getVersion());
        log.info("Java 版本: {}", System.getProperty("java.version"));
        log.info("Java 供应商: {}", System.getProperty("java.vendor"));
        log.info("JVM 版本: {}", System.getProperty("java.vm.version"));
        log.info("JVM 供应商: {}", System.getProperty("java.vm.vendor"));
        log.info("JVM 名称: {}", System.getProperty("java.vm.name"));
        log.info("操作系统: {} {} {}",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
        log.info("文件编码: {}", System.getProperty("file.encoding"));
        log.info("用户目录: {}", System.getProperty("user.dir"));
        log.info("================================================");

        // 检查Spring Boot 3.x兼容性
        String springBootVersion = SpringBootVersion.getVersion();
        if (springBootVersion != null && springBootVersion.startsWith("3")) {
            log.info("检测到 Spring Boot 3.x，已启用 Jakarta EE 9+ 支持");
        } else {
            log.warn("检测到非 Spring Boot 3.x 版本，请注意 Jakarta EE 兼容性问题");
        }
    }
}