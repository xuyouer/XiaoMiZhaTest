package ltd.xiaomizha.xuyou.license.interceptor;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.http.HeaderModifiableRequestWrapper;
import ltd.xiaomizha.xuyou.common.utils.license.HardwareUtils;
import ltd.xiaomizha.xuyou.license.service.LicenseInfoService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Map;

/**
 * 许可证验证拦截器
 * <p>
 * 用于验证请求是否携带有效的许可证
 */
@Slf4j
@Component
public class LicenseValidationInterceptor implements HandlerInterceptor {

    private final LicenseInfoService licenseInfoService;

    public LicenseValidationInterceptor(LicenseInfoService licenseInfoService) {
        this.licenseInfoService = licenseInfoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String licenseKey = request.getHeader("X-License-Key");
        // String hardwareInfo = request.getHeader("X-Hardware-Info");
        String hardwareInfo = HardwareUtils.getHardwareInfo();
        HeaderModifiableRequestWrapper wrappedRequest = new HeaderModifiableRequestWrapper(request);
        wrappedRequest.addHeader("X-Hardware-Info", hardwareInfo);

        // 检查许可证密钥是否存在
        if (licenseKey == null || licenseKey.isEmpty()) {
            sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), "需要许可证密钥");
            return false;
        }

        // 检查硬件信息是否存在
        if (hardwareInfo == null || hardwareInfo.isEmpty()) {
            sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), "需要提供硬件信息");
            return false;
        }

        // 验证许可证是否有效
        boolean isValid = licenseInfoService.isLicenseValid(licenseKey, hardwareInfo);
        if (!isValid) {
            Map<String, Object> validationResult = licenseInfoService.validateLicense(licenseKey, hardwareInfo);
            String errorMessage = validationResult.getOrDefault("message", "许可证无效").toString();
            sendErrorResponse(response, ResultEnum.UNAUTHORIZED.getCode(), errorMessage);
            return false;
        }

        return true;
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
