package ltd.xiaomizha.xuyou.monitor;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(
        scanBasePackages = "ltd.xiaomizha.xuyou",
        exclude = {
                DataSourceAutoConfiguration.class,
                DruidDataSourceAutoConfigure.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class,
                RabbitAutoConfiguration.class,
                // OaMainConfig.class
        }
)
@EnableDiscoveryClient
public class XuyouMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuyouMonitorApplication.class, args);
    }

}
