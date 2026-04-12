package ltd.xiaomizha.xuyou.common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

/**
 * 邮件服务自动配置类
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailAutoConfiguration {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String defaultEncoding;
    private Properties properties = new Properties();

    @Bean
    @ConditionalOnProperty(name = "spring.mail.host")
    @ConditionalOnProperty(name = "spring.mail.username")
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        log.info("正在初始化 JavaMailSender Bean... host={}, port={}, username={}", host, port, username);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port != null ? port : 465);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        mailSender.setDefaultEncoding(Objects.requireNonNullElse(defaultEncoding, "UTF-8"));

        // 配置 SMTP 属性
        mailSender.setJavaMailProperties(properties);

        log.info("JavaMailSender Bean 初始化完成: host={}", host);
        return mailSender;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
