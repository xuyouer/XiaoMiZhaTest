package ltd.xiaomizha.xuyou.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JansiConfig {

    @PostConstruct
    public void initJansi() {
        try {
            // 设置系统属性以避免警告
            System.setProperty("jansi.passthrough", "true");
            System.setProperty("jansi.force", "true");
            // 安装JANSI
            AnsiConsole.systemInstall();
            log.info("JANSI 初始化成功");
        } catch (Throwable e) {
            log.error("JANSI 初始化失败: {}", e.getMessage());
            log.info("继续运行, 未使用 JANSI 支持...");
        }
    }
}
