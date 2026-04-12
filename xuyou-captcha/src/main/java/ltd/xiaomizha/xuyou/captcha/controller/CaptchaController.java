package ltd.xiaomizha.xuyou.captcha.controller;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.CacheConstant;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("captcha")
@Tag(name = "行为验证码", description = "滑块/旋转/点选验证码生成与校验")
public class CaptchaController {

    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final long CAPTCHA_TOKEN_EXPIRE_MINUTES = 5;

    @Operation(summary = "生成验证码", description = "支持SLIDER(滑块)/ROTATE(旋转)/WORD_IMAGE_CLICK(文字点选)类型")
    @PostMapping("/generate")
    public ResponseResult<ImageCaptchaVO> generateCaptcha(@RequestParam(value = "type", defaultValue = "SLIDER") String type) {
        ApiResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(
                GenerateParam.builder().type(type).build()
        );

        if (response.isSuccess()) {
            log.debug("验证码生成成功, type={}, id={}", type, response.getData().getId());
            return ResponseResult.success(response.getData());
        }

        return ResponseResult.error(ResultEnum.CAPTCHA_ERROR.getCode(), "验证码生成失败");
    }

    @Operation(summary = "校验验证码", description = "校验用户操作结果, 成功后返回一次性token供业务接口使用")
    @PostMapping("/check")
    public ResponseResult<String> checkCaptcha(@RequestBody Map<String, Object> param) {
        String id = (String) param.get("id");
        Object data = param.get("data");

        boolean matchResult;
        if (data instanceof Number) {
            matchResult = imageCaptchaApplication.matching(id, ((Number) data).floatValue());
        } else if (data instanceof Map<?, ?> dataMap) {
            Object pointX = dataMap.get("pointX");
            if (pointX instanceof Number) {
                matchResult = imageCaptchaApplication.matching(id, ((Number) pointX).floatValue());
            } else {
                log.warn("验证码校验参数格式错误, id={}", id);
                return ResponseResult.<String>error(ResultEnum.CAPTCHA_ERROR.getCode(), "验证码参数格式错误");
            }
        } else {
            log.warn("验证码校验参数类型不支持, id={}, dataType={}", id, data != null ? data.getClass().getName() : "null");
            return ResponseResult.<String>error(ResultEnum.CAPTCHA_ERROR.getCode(), "验证码参数类型错误");
        }

        if (matchResult) {
            String captchaToken = UUID.randomUUID().toString().replace("-", "");
            stringRedisTemplate.opsForValue().set(
                    CacheConstant.REDIS_PREFIX_CAPTCHA_TOKEN + captchaToken,
                    "1",
                    CAPTCHA_TOKEN_EXPIRE_MINUTES,
                    TimeUnit.MINUTES
            );
            log.info("验证码校验通过, 生成一次性token={}", captchaToken);
            return ResponseResult.success(captchaToken);
        }

        log.warn("验证码校验失败, id={}", id);
        return ResponseResult.error(ResultEnum.CAPTCHA_ERROR.getCode(), "验证码校验失败");
    }

    @Operation(summary = "二次校验token", description = "业务接口调用前校验验证码token, 一次性使用后立即删除")
    @PostMapping("/verify-token")
    public ResponseResult<Void> verifyToken(@RequestParam String captchaToken) {
        String key = CacheConstant.REDIS_PREFIX_CAPTCHA_TOKEN + captchaToken;
        String value = stringRedisTemplate.opsForValue().getAndDelete(key);

        if (value != null) {
            return ResponseResult.success();
        }

        return ResponseResult.error(ResultEnum.CAPTCHA_ERROR.getCode(), "验证码已过期或无效");
    }

}
