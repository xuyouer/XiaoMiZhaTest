package ltd.xiaomizha.xuyou.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.service.UserResourcesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("resources")
@Tag(name = "用户资源管理", description = "用户资源管理API")
public class UserResourcesController {

    @Resource
    private UserResourcesService userResourcesService;

}
