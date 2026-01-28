package ltd.xiaomizha.xuyou.eureka;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication(
        scanBasePackages = "ltd.xiaomizha.xuyou",
        exclude = {
                DruidDataSourceAutoConfigure.class,
                DataSourceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
                org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
                org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class
        }
)
@EnableEurekaServer
public class XuyouEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuyouEurekaApplication.class, args);
    }

}
