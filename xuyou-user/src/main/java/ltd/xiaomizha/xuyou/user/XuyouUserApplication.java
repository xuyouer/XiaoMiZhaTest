package ltd.xiaomizha.xuyou.user;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "ltd.xiaomizha.xuyou")
@EnableDiscoveryClient
@Slf4j
@EnableTransactionManagement  // 开启事务管理
@EnableCaching  // 开启缓存
@EnableAsync  // 开启异步
@EnableScheduling  // 开启定时任务
@MapperScan({"ltd.xiaomizha.xuyou.**.mapper"})
public class XuyouUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuyouUserApplication.class, args);
    }

}
