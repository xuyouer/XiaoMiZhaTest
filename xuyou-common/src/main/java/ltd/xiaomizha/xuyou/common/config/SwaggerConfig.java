package ltd.xiaomizha.xuyou.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI基本信息
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("小咪楂 API文档")
                        .description("小咪楂项目的 RESTful API 文档")
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("xuyouer")
                                .email("xuyouer@example.com")
                                .url("https://github.com/xuyouer")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .servers(List.of(
                        new Server().url("http://localhost:8092/api").description("开发环境"),
                        new Server().url("http://localhost:8091/api").description("网关环境")
                ));
    }

    /**
     * 配置用户服务API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户服务")
                .pathsToMatch("/api/users/**", "/api/auth/**", "/api/roles/**", "/api/resources/**")
                .build();
    }

    /**
     * 配置演示API分组
     */
    @Bean
    public GroupedOpenApi demoApi() {
        return GroupedOpenApi.builder()
                .group("演示服务")
                .pathsToMatch("/api/demo/**")
                .build();
    }

    /**
     * 配置所有API分组
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("全部API")
                .pathsToMatch("/api/**")
                .build();
    }

}
