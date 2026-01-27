package ltd.xiaomizha.xuyou.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.service.UserRolesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("roles")
@Tag(name = "用户角色管理", description = "用户角色管理API")
public class UserRolesController {

    @Resource
    private UserRolesService userRolesService;

}
