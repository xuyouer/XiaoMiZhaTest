package ltd.xiaomizha.xuyou.mail.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.mail.service.EmailCodeService;
import ltd.xiaomizha.xuyou.mail.service.EmailValidateTokensService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("email")
@Tag(name = "邮箱验证管理", description = "邮箱验证API")
public class EmailController {

    @Resource
    private EmailValidateTokensService emailValidateTokensService;

    @Resource
    private EmailCodeService emailCodeService;

    /**
     * 发送邮箱验证邮件
     *
     * @param userId       用户ID
     * @param email        邮箱地址
     * @param validateType 验证类型: REGISTER-注册, RESET-重置密码, BIND-绑定
     * @return 验证结果, 包含脱敏邮箱、有效期等
     */
    @PostMapping("/send-validate")
    @Operation(summary = "发送验证邮件", description = "向指定邮箱发送验证邮件，用于注册、重置密码、绑定邮箱等场景")
    public ResponseResult<?> sendValidationEmail(
            @RequestParam Long userId,
            @RequestParam String email,
            @RequestParam(required = false, defaultValue = "REGISTER") String validateType) {
        log.info("收到发送验证邮件请求: userId={}, email={}, type={}", userId, email, validateType);
        return emailValidateTokensService.sendValidationEmail(userId, email, TokenType.valueOf(validateType.toUpperCase()));
    }

    /**
     * 校验邮箱验证Token
     *
     * @param token 验证Token, 从URL参数中获取
     * @return 验证结果
     */
    @GetMapping("/validate")
    @Operation(summary = "校验验证Token", description = "校验邮箱验证Token是否有效并完成验证")
    public ResponseResult<?> validateToken(
            @RequestParam String token,
            HttpServletRequest request) {

        // 获取客户端IP
        String clientIp = request.getHeader("X-Real-IP");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        log.info("收到验证Token请求: token={}, ip={}", token, clientIp);
        return emailValidateTokensService.validateToken(token, clientIp);
    }

    /**
     * 查询邮箱验证状态
     *
     * @param userId 用户ID
     * @param email  邮箱地址
     * @return 是否已验证
     */
    @GetMapping("/status")
    @Operation(summary = "查询验证状态", description = "查询指定用户邮箱的验证状态")
    public ResponseResult<?> checkEmailValidated(
            @RequestParam Long userId,
            @RequestParam String email) {
        log.debug("查询邮箱验证状态: userId={}, email={}", userId, email);
        return emailValidateTokensService.checkEmailValidated(userId, email);
    }

    /**
     * 重新发送验证邮件
     *
     * @param userId 用户ID
     * @param email  邮箱地址
     * @return 重发结果
     */
    @PostMapping("/resend")
    @Operation(summary = "重新发送验证邮件", description = "重新向指定邮箱发送验证邮件")
    public ResponseResult<?> resendValidationEmail(
            @RequestParam Long userId,
            @RequestParam String email) {
        log.info("收到重新发送验证邮件请求: userId={}, email={}", userId, email);
        return emailValidateTokensService.resendValidationEmail(userId, email);
    }

    /**
     * 撤销验证Token
     *
     * @param token 要撤销的Token值
     * @return 撤销结果
     */
    @PostMapping("/revoke")
    @Operation(summary = "撤销验证Token", description = "撤销指定的未使用验证Token(管理员接口)")
    public ResponseResult<?> revokeToken(@RequestParam String token) {

        log.warn("收到撤销Token请求: token={}", token);
        return emailValidateTokensService.revokeToken(token);
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @param scene 使用场景: REGISTER-注册, LOGIN-登录, RESET-重置密码, BIND-绑定, MODIFY-修改信息
     * @return 发送结果，包含脱敏邮箱、有效期等
     */
    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "向指定邮箱发送数字验证码，用于注册、登录、重置密码等场景")
    public ResponseResult<?> sendVerificationCode(
            @RequestParam String email,
            @RequestParam(required = false, defaultValue = "REGISTER") String scene) {
        log.info("收到发送验证码请求: email={}, scene={}", email, scene);
        return emailCodeService.sendVerificationCode(email, scene.toUpperCase());
    }

    /**
     * 校验邮箱验证码
     *
     * @param email 邮箱地址
     * @param code  用户输入的6位验证码
     * @return 校验结果
     */
    @PostMapping("/verify-code")
    @Operation(summary = "校验验证码", description = "校验邮箱验证码是否正确（一次性使用）")
    public ResponseResult<?> verifyCode(
            @RequestParam String email,
            @RequestParam String code,
            HttpServletRequest request) {

        String clientIp = request.getHeader("X-Real-IP");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        log.info("收到校验验证码请求: email={}, ip={}", email, clientIp);
        return emailCodeService.verifyCode(email, code, clientIp);
    }

    /**
     * 查询验证码发送状态 (冷却时间)
     *
     * @param email 邮箱地址
     * @param scene 使用场景
     * @return 是否可发送、剩余冷却时间等
     */
    @GetMapping("/code-status")
    @Operation(summary = "查询验证码状态", description = "查询指定邮箱的验证码发送冷却状态")
    public ResponseResult<?> checkSendStatus(
            @RequestParam String email,
            @RequestParam(required = false, defaultValue = "REGISTER") String scene) {
        log.debug("查询验证码发送状态: email={}, scene={}", email, scene);
        return emailCodeService.checkSendStatus(email, scene.toUpperCase());
    }

}
