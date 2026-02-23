package ltd.xiaomizha.xuyou.license.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.license.HardwareUtils;
import ltd.xiaomizha.xuyou.license.entity.LicenseInfo;
import ltd.xiaomizha.xuyou.license.service.LicenseInfoService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("license")
@Tag(name = "许可证服务管理", description = "许可证服务API")
public class LicenseController {

    private final LicenseInfoService licenseInfoService;

    public LicenseController(LicenseInfoService licenseInfoService) {
        this.licenseInfoService = licenseInfoService;
    }

    @Data
    public static class ActivateLicenseRequest {
        private String licenseKey;
        private String activationCode;
    }

    @Data
    public static class BatchUpdateStatusRequest {
        private List<String> licenseKeys;
        private String status;
    }

    /**
     * 创建许可证
     */
    @PostMapping("/generate/{expireDays}")
    @Operation(summary = "创建许可证", description = "创建新的许可证")
    public ResponseResult<LicenseInfo> generateLicense(@RequestBody LicenseInfo licenseInfo, @PathVariable int expireDays) {
        try {
            licenseInfo.setStartTime(LocalDateTime.now());
            licenseInfo.setEndTime(LocalDateTime.now().plusDays(expireDays));
            boolean created = licenseInfoService.generateLicense(licenseInfo);
            if (created) {
                return ResponseResult.ok(licenseInfo);
            } else {
                return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Failed to create license");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error creating license: " + e.getMessage());
        }
    }

    /**
     * 获取许可证信息
     */
    @GetMapping("/get/{licenseKey}")
    @Operation(summary = "获取许可证信息", description = "根据许可证密钥获取许可证信息")
    public ResponseResult<LicenseInfo> getLicense(@PathVariable String licenseKey) {
        try {
            LicenseInfo licenseInfo = licenseInfoService.getByLicenseKey(licenseKey);
            if (licenseInfo != null) {
                return ResponseResult.ok(licenseInfo);
            } else {
                return ResponseResult.error(ResultEnum.NOT_FOUND.getCode(), "License not found");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting license: " + e.getMessage());
        }
    }

    /**
     * 激活许可证
     */
    @PostMapping("/activate")
    @Operation(summary = "激活许可证", description = "激活许可证并绑定硬件信息")
    public ResponseResult<Boolean> activateLicense(@RequestBody ActivateLicenseRequest request) {
        try {
            boolean activated = licenseInfoService.activateLicense(
                    request.getLicenseKey(),
                    HardwareUtils.getHardwareInfo(),
                    request.getActivationCode()
            );
            if (activated) {
                return ResponseResult.ok(true);
            } else {
                return ResponseResult.error(ResultEnum.UNAUTHORIZED.getCode(), "Failed to activate license");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error activating license: " + e.getMessage());
        }
    }

    /**
     * 验证许可证
     */
    @PostMapping("/validate/{licenseKey}")
    @Operation(summary = "验证许可证", description = "验证许可证是否有效")
    public ResponseResult<Map<String, Object>> validateLicense(@PathVariable String licenseKey) {
        try {
            Map<String, Object> validationResult = licenseInfoService.validateLicense(licenseKey, HardwareUtils.getHardwareInfo());
            return ResponseResult.ok(validationResult);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error validating license: " + e.getMessage());
        }
    }

    /**
     * 生成激活码
     */
    @PostMapping("/generate-activation-code/{expireDays}")
    @Operation(summary = "生成激活码", description = "为许可证生成激活码")
    public ResponseResult<?> generateActivationCode(@PathVariable int expireDays) {
        try {
            String activationCode = licenseInfoService.generateActivationCode(HardwareUtils.getHardwareInfo(), expireDays);
            return ResponseResult.ok(activationCode);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error generating activation code: " + e.getMessage());
        }
    }

    /**
     * 禁用许可证
     */
    @PostMapping("/disable/{licenseKey}")
    @Operation(summary = "禁用许可证", description = "禁用指定的许可证")
    public ResponseResult<Boolean> disableLicense(@PathVariable String licenseKey) {
        try {
            boolean disabled = licenseInfoService.disableLicense(licenseKey);
            if (disabled) {
                return ResponseResult.ok(true);
            } else {
                return ResponseResult.error(ResultEnum.NOT_FOUND.getCode(), "Failed to disable license");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error disabling license: " + e.getMessage());
        }
    }

    /**
     * 启用许可证
     */
    @PostMapping("/enable/{licenseKey}")
    @Operation(summary = "启用许可证", description = "启用指定的许可证")
    public ResponseResult<Boolean> enableLicense(@PathVariable String licenseKey) {
        try {
            boolean enabled = licenseInfoService.enableLicense(licenseKey);
            if (enabled) {
                return ResponseResult.ok(true);
            } else {
                return ResponseResult.error(ResultEnum.NOT_FOUND.getCode(), "Failed to enable license");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error enabling license: " + e.getMessage());
        }
    }

    /**
     * 更新许可证硬件绑定
     */
    @PostMapping("/update-hardware-binding/{licenseKey}")
    @Operation(summary = "更新硬件绑定", description = "更新许可证的硬件绑定信息")
    public ResponseResult<Boolean> updateHardwareBinding(@PathVariable String licenseKey) {
        try {
            boolean updated = licenseInfoService.updateHardwareBinding(licenseKey, HardwareUtils.getHardwareInfo());
            if (updated) {
                return ResponseResult.ok(true);
            } else {
                return ResponseResult.error(ResultEnum.NOT_FOUND.getCode(), "Failed to update hardware binding");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error updating hardware binding: " + e.getMessage());
        }
    }

    /**
     * 验证功能访问权限
     */
    @GetMapping("/validate-feature/{licenseKey}/{featureCode}")
    @Operation(summary = "验证功能访问权限", description = "验证许可证是否有权访问指定功能")
    public ResponseResult<Boolean> validateFeatureAccess(@PathVariable String licenseKey, @PathVariable String featureCode) {
        try {
            boolean hasAccess = licenseInfoService.validateFeatureAccess(licenseKey, featureCode);
            return ResponseResult.ok(hasAccess);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error validating feature access: " + e.getMessage());
        }
    }

    /**
     * 获取授权功能列表
     */
    @GetMapping("/features/{licenseKey}")
    @Operation(summary = "获取授权功能列表", description = "获取许可证的授权功能列表")
    public ResponseResult<List<String>> getAuthorizedFeatures(@PathVariable String licenseKey) {
        try {
            List<String> features = licenseInfoService.getAuthorizedFeatures(licenseKey);
            return ResponseResult.ok(features);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting authorized features: " + e.getMessage());
        }
    }

    /**
     * 获取即将过期的许可证
     */
    @GetMapping("/expiring/{days}")
    @Operation(summary = "获取即将过期的许可证", description = "获取指定天数内即将过期的许可证")
    public ResponseResult<List<LicenseInfo>> getExpiringLicenses(@PathVariable int days) {
        try {
            List<LicenseInfo> licenses = licenseInfoService.getExpiringLicenses(days);
            return ResponseResult.ok(licenses);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting expiring licenses: " + e.getMessage());
        }
    }

    /**
     * 统计许可证数量
     */
    @GetMapping("/count")
    @Operation(summary = "统计许可证数量", description = "统计不同状态的许可证数量")
    public ResponseResult<Map<String, Long>> countLicenses() {
        try {
            Map<String, Long> countResult = licenseInfoService.countLicenses(Map.of());
            return ResponseResult.ok(countResult);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error counting licenses: " + e.getMessage());
        }
    }

    /**
     * 批量更新许可证状态
     */
    @PostMapping("/batch-update-status")
    @Operation(summary = "批量更新许可证状态", description = "批量更新多个许可证的状态")
    public ResponseResult<Boolean> batchUpdateStatus(@RequestBody BatchUpdateStatusRequest request) {
        try {
            Status status = Status.valueOf(request.getStatus().toUpperCase());
            boolean updated = licenseInfoService.batchUpdateStatus(request.getLicenseKeys(), status);
            if (updated) {
                return ResponseResult.ok(true);
            } else {
                return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Failed to batch update status");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error batch updating status: " + e.getMessage());
        }
    }
}
