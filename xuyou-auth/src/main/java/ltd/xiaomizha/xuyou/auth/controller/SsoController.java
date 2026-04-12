package ltd.xiaomizha.xuyou.auth.controller;

import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.dev33.satoken.util.SaResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SSO 单点登录控制器
 */
@Slf4j
@RestController
@RequestMapping("auth/sso")
@Tag(name = "SSO单点登录", description = "Sa-Token SSO 单点登录API")
public class SsoController {

    @Resource
    private UsersService usersService;

    /**
     * 处理所有 SSO 相关请求
     *
     * @return SSO 处理结果
     */
    @RequestMapping("/*")
    public Object ssoRequest() {
        return SaSsoServerProcessor.instance.dister();
    }

    /**
     * 自定义 SSO 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含双Token）
     */
    @RequestMapping("/login")
    public SaResult login(@RequestParam String username, @RequestParam String password) {
        boolean loginSuccess = usersService.loginUser(
                username,
                password,
                null,
                null,
                null,
                LoginType.LOGIN
        );
        if (!loginSuccess) {
            log.error("用户登录失败: 用户名密码错误或用户已被禁用");
            return SaResult.error("用户名或密码错误").setCode(ResultEnum.PASSWORD_ERROR.getCode());
        }
        // 获取用户信息
        Users user = usersService.lambdaQuery()
                .eq(Users::getUsername, username)
                .one();
        if (user == null) {
            return SaResult.error("用户不存在").setCode(ResultEnum.USER_NOT_FOUND.getCode());
        }
        StpUtil.login(user.getUserId());

        // 返回登录成功信息
        return SaResult.ok("登录成功")
                .set("accessToken", StpUtil.getTokenValue())
                .set("refreshToken", SaTempUtil.createToken(user.getUserId(), 2592000));
    }

    /**
     * SSO 单点注销
     *
     * @return 注销结果
     */
    @RequestMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok("注销成功, 所有系统已同步退出");
    }
}
