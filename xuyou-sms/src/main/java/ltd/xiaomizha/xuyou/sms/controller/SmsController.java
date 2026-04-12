package ltd.xiaomizha.xuyou.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.sms.service.SmsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("sms")
@Tag(name = "短信", description = "短信发送API")
public class SmsController {

    @Resource
    private SmsService smsService;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 发送结果，成功时返回验证码（开发环境）
     */
    @PostMapping("/verification-code/{phone}")
    @Operation(summary = "发送短信验证码", description = "生成6位随机验证码并发送短信，返回验证码（开发环境）")
    public ResponseResult<Void> sendVerificationCode(@PathVariable String phone) {
        log.info("请求发送短信验证码: phone={}", phone);
        return smsService.sendVerificationCode(phone);
    }

    /**
     * 校验短信验证码
     *
     * @param phone 手机号
     * @param code  用户输入的验证码
     * @return 校验结果
     */
    @PostMapping("/verify-code")
    @Operation(summary = "校验短信验证码", description = "校验用户输入的验证码是否正确")
    public ResponseResult<Void> verifyCode(
            @RequestParam String phone,
            @RequestParam String code) {
        log.info("请求校验短信验证码: phone={}, code={}", phone, code);
        return smsService.verifyCode(phone, code);
    }

    /**
     * 发送自定义短信内容
     *
     * @param phone   手机号
     * @param content 短信模板内容
     * @param args    模板参数（可选）
     * @return 发送结果
     */
    @PostMapping("/custom")
    @Operation(summary = "发送自定义短信", description = "发送自定义内容的短信（需符合模板规范）")
    public ResponseResult<Void> sendCustomMessage(
            @RequestParam String phone,
            @RequestParam String content,
            @RequestParam(required = false) String... args) {
        log.info("请求发送自定义短信: phone={}, content={}", phone, content);
        return smsService.sendCustomMessage(phone, content, args);
    }

}
