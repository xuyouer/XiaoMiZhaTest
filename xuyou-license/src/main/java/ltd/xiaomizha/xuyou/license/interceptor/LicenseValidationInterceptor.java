package ltd.xiaomizha.xuyou.license.interceptor;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.CacheConstant;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.http.HeaderModifiableRequestWrapper;
import ltd.xiaomizha.xuyou.common.utils.license.HardwareUtils;
import ltd.xiaomizha.xuyou.common.utils.license.LicenseGeneratorUtils;
import ltd.xiaomizha.xuyou.license.service.LicenseInfoService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 许可证验证拦截器
 * <p>
 * 用于验证请求是否携带有效的许可证
 */
@Slf4j
@Component
public class LicenseValidationInterceptor implements HandlerInterceptor {

    @Resource
    private LicenseGeneratorUtils licenseGeneratorUtils;

    private final LicenseInfoService licenseInfoService;
    private final StringRedisTemplate stringRedisTemplate;

    public LicenseValidationInterceptor(LicenseInfoService licenseInfoService, StringRedisTemplate stringRedisTemplate) {
        this.licenseInfoService = licenseInfoService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String licenseKey = request.getHeader("X-License-Key");
        // String hardwareInfo = request.getHeader("X-Hardware-Info");
        String hardwareInfo = HardwareUtils.getHardwareInfo();
        HeaderModifiableRequestWrapper wrappedRequest = new HeaderModifiableRequestWrapper(request);
        wrappedRequest.addHeader("X-Hardware-Info", hardwareInfo);

        // // 检查许可证密钥是否存在
        // if (licenseKey == null || licenseKey.isEmpty()) {
        //     sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), "需要许可证密钥");
        //     return false;
        // }
        // // 检查硬件信息是否存在
        // if (hardwareInfo == null || hardwareInfo.isEmpty()) {
        //     sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), "需要提供硬件信息");
        //     return false;
        // }
        // // 验证许可证是否有效
        // boolean isValid = licenseInfoService.isLicenseValid(licenseKey, hardwareInfo);
        // if (!isValid) {
        //     Map<String, Object> validationResult = licenseInfoService.validateLicense(licenseKey, hardwareInfo);
        //     String errorMessage = validationResult.getOrDefault("message", "许可证无效").toString();
        //     sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), errorMessage);
        //     return false;
        // }
        // return true;

        // 有许可证密钥, 验证正式授权
        if (licenseKey != null && !licenseKey.isEmpty()) {
            return validateFormalLicense(licenseKey, hardwareInfo, response);
        }

        // 无许可证密钥, 检查是否启用试用模式
        if (licenseGeneratorUtils.getTrialEnabled()) {
            return validateTrialMode(hardwareInfo, response);
        }

        // 无试用模式且无许可证, 拒绝访问
        sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), "需要提供有效的许可证密钥");
        return false;
    }

    /**
     * 验证正式授权的许可证
     *
     * @param licenseKey   许可证密钥
     * @param hardwareInfo 硬件信息
     * @param response     HTTP响应
     * @return 是否通过验证
     */
    private boolean validateFormalLicense(String licenseKey, String hardwareInfo,
                                          HttpServletResponse response) throws Exception {
        if (hardwareInfo == null || hardwareInfo.isEmpty()) {
            sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), "无法获取硬件信息");
            return false;
        }

        boolean isValid = licenseInfoService.isLicenseValid(licenseKey, hardwareInfo);
        if (!isValid) {
            Map<String, Object> validationResult = licenseInfoService.validateLicense(licenseKey, hardwareInfo);
            String errorMessage = validationResult.getOrDefault("message", "许可证无效").toString();
            sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), errorMessage);
            return false;
        }

        log.debug("License validation passed: licenseKey={}", licenseKey);
        return true;
    }

    /**
     * 验证试用模式
     * <p>
     * 无正式License时自动启用试用模式, 首次启动时记录开始时间,
     * 试用期内允许访问, 过期后拒绝
     *
     * @param hardwareInfo 硬件信息
     * @param response     HTTP响应
     * @return 是否在试用有效期内
     */
    private boolean validateTrialMode(String hardwareInfo, HttpServletResponse response) throws Exception {
        String trialRedisKey = licenseGeneratorUtils.getTrialRedisKeyPrefix() + hardwareInfo;

        // 检查是否已有试用记录
        Boolean hasTrialRecord = stringRedisTemplate.hasKey(trialRedisKey + ":" + CacheConstant.REDIS_KEY_TRIAL_START_TIME);

        if (hasTrialRecord) {
            // 已有试用记录, 检查是否过期
            return checkTrialExpiration(trialRedisKey, response);
        } else {
            // 首次使用, 初始化试用期
            return initializeTrialPeriod(trialRedisKey, hardwareInfo, response);
        }
    }

    /**
     * 初始化试用期
     *
     * @param trialRedisKey Redis key前缀
     * @param hardwareInfo  硬件信息
     * @param response      HTTP响应
     * @return 是否初始化成功
     */
    private boolean initializeTrialPeriod(String trialRedisKey, String hardwareInfo,
                                          HttpServletResponse response) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusHours(licenseGeneratorUtils.getTrialHours());

        // 记录试用开始时间
        stringRedisTemplate.opsForValue().set(
                trialRedisKey + ":" + CacheConstant.REDIS_KEY_TRIAL_START_TIME,
                now.toString(),
                licenseGeneratorUtils.getTrialHours(),
                TimeUnit.HOURS
        );

        // 记录硬件绑定, 防止换机器重置试用
        stringRedisTemplate.opsForValue().set(
                trialRedisKey + ":" + CacheConstant.REDIS_KEY_TRIAL_HARDWARE,
                hardwareInfo,
                licenseGeneratorUtils.getTrialHours(),
                TimeUnit.HOURS
        );

        long remainingMinutes = ChronoUnit.MINUTES.between(now, expireTime);

        log.info("Trial mode initialized for hardware={}, expireAt={}, remainingMinutes={}", hardwareInfo, expireTime, remainingMinutes);

        // 返回试用信息到响应头, 方便前端展示
        response.setHeader("X-Trial-Mode", "true");
        response.setHeader("X-Trial-Remaining-Minutes", String.valueOf(remainingMinutes));
        response.setHeader("X-Trial-Expire-At", expireTime.toString());

        return true; // 允许访问
    }

    /**
     * 检查试用期是否已过期
     *
     * @param trialRedisKey Redis key前缀
     * @param response      HTTP响应
     * @return 是否在有效期内
     */
    private boolean checkTrialExpiration(String trialRedisKey, HttpServletResponse response) throws Exception {
        String startTimeStr = stringRedisTemplate.opsForValue().get(trialRedisKey + ":" + CacheConstant.REDIS_KEY_TRIAL_START_TIME);

        if (startTimeStr == null) {
            // Redis中无记录, 重新初始化
            return initializeTrialPeriod(trialRedisKey, stringRedisTemplate.opsForValue().get(trialRedisKey + ":" + CacheConstant.REDIS_KEY_TRIAL_HARDWARE), response);
        }

        LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
        LocalDateTime expireTime = startTime.plusHours(licenseGeneratorUtils.getTrialHours());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(expireTime)) {
            // 试用已过期
            long expiredHours = ChronoUnit.HOURS.between(expireTime, now);
            log.warn("Trial mode expired for key={}, expired {} hours ago", trialRedisKey, expiredHours);

            sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), String.format("试用期限已过期（%d小时）, 请联系管理员获取正式授权", licenseGeneratorUtils.getTrialHours()));
            return false;
        }

        // 试用仍在有效期内
        long remainingMinutes = ChronoUnit.MINUTES.between(now, expireTime);

        log.debug("Trial mode active, remainingMinutes={}", remainingMinutes);

        // 返回试用信息到响应头
        response.setHeader("X-Trial-Mode", "true");
        response.setHeader("X-Trial-Remaining-Minutes", String.valueOf(remainingMinutes));

        return true; // 允许访问
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(ResponseResult.error(code, message)));
        writer.flush();
        writer.close();
    }
}
