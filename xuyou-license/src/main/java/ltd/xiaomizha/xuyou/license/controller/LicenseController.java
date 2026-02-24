package ltd.xiaomizha.xuyou.license.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.utils.license.HardwareUtils;
import ltd.xiaomizha.xuyou.license.entity.LicenseInfo;
import ltd.xiaomizha.xuyou.license.service.LicenseInfoService;
import ltd.xiaomizha.xuyou.license.service.LicenseUserRelationService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("license")
@Tag(name = "许可证服务管理", description = "许可证服务API")
public class LicenseController {

    @Resource
    private LicenseInfoService licenseInfoService;

    @Resource
    private LicenseUserRelationService licenseUserRelationService;

    @Data
    public static class GenerateLicenseRequest {
        private LicenseInfo licenseInfo;
        private int expireDays;
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

    @Data
    public static class GenerateTrialLicenseRequest {
        private Integer userId;
        private String userName;
    }

    @Data
    public static class CreateRelationRequest {
        private Integer userId;
        private String licenseId;
    }

    @Data
    public static class CreateRelationByKeyRequest {
        private Integer userId;
        private String licenseKey;
    }

    @Data
    public static class BatchRelationRequest {
        private Integer userId;
        private List<String> licenseKeys;
    }

    @Data
    public static class LicenseKeyResponse {
        private String licenseKey;

        public LicenseKeyResponse(String licenseKey) {
            this.licenseKey = licenseKey;
        }
    }

    @Data
    public static class LicenseActivationCodeResponse {
        private String activationCode;

        public LicenseActivationCodeResponse(String activationCode) {
            this.activationCode = activationCode;
        }
    }

    /**
     * 创建许可证
     */
    @PostMapping("/generate")
    @Operation(summary = "创建许可证", description = "创建新的许可证")
    public ResponseResult<LicenseInfo> generateLicense(@RequestBody GenerateLicenseRequest request) {
        try {
            if (request.getLicenseInfo() == null) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "LicenseInfo cannot be null");
            }
            if (request.getExpireDays() <= 0) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "ExpireDays must be positive");
            }
            request.getLicenseInfo().setStartTime(LocalDateTime.now());
            request.getLicenseInfo().setEndTime(LocalDateTime.now().plusDays(request.getExpireDays()));
            boolean created = licenseInfoService.generateLicense(request.getLicenseInfo());
            if (created) {
                return ResponseResult.ok(request.getLicenseInfo());
            } else {
                return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Failed to create license");
            }
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error creating license: " + e.getMessage());
        }
    }

    /**
     * 生成试用许可证
     */
    @PostMapping("/generate-trial")
    @Operation(summary = "生成试用许可证", description = "为用户生成试用许可证")
    public ResponseResult<?> generateTrialLicense(@RequestBody GenerateTrialLicenseRequest request) {
        try {
            String trialLicenseKey = licenseInfoService.generateTrialLicense(request.getUserId(), request.getUserName());
            return ResponseResult.ok(new LicenseKeyResponse(trialLicenseKey));
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error generating trial license: " + e.getMessage());
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
            return ResponseResult.ok(new LicenseActivationCodeResponse(activationCode));
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

    /**
     * 根据用户ID查询用户名下的所有许可证
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询许可证", description = "根据用户ID查询用户名下的所有许可证")
    public ResponseResult<?> getLicensesByUserId(@PathVariable Integer userId, @RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        try {
            List<LicenseInfo> licenses = licenseUserRelationService.getLicensesByUserId(userId);
            return ResponseResult.ok(licenses);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting licenses by user ID: " + e.getMessage());
        }
    }

    /**
     * 根据许可证ID查询关联的用户
     */
    @GetMapping("/relations/license/{licenseId}")
    @Operation(summary = "根据许可证ID查询用户", description = "根据许可证ID查询关联的用户")
    public ResponseResult<?> getUsersByLicenseId(@PathVariable String licenseId, @RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        try {
            List<?> relations = licenseUserRelationService.getUsersByLicenseId(licenseId);
            return ResponseResult.ok(relations);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting users by license ID: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID和许可证类型查询许可证
     */
    @GetMapping("/user/{userId}/type/{licenseType}")
    @Operation(summary = "根据用户ID和类型查询许可证", description = "根据用户ID和许可证类型查询许可证")
    public ResponseResult<?> getLicensesByUserIdAndType(@PathVariable Integer userId, @PathVariable String licenseType, @RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        try {
            List<LicenseInfo> licenses = licenseUserRelationService.getLicensesByUserIdAndType(userId, licenseType);
            return ResponseResult.ok(licenses);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting licenses by user ID and type: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否已有指定类型的许可证
     */
    @GetMapping("/user/{userId}/has-type/{licenseType}")
    @Operation(summary = "检查用户是否有指定类型许可证", description = "检查用户是否已有指定类型的许可证")
    public ResponseResult<Boolean> hasLicenseByType(@PathVariable Integer userId, @PathVariable String licenseType) {
        try {
            boolean hasLicense = licenseUserRelationService.hasLicenseByType(userId, licenseType);
            return ResponseResult.ok(hasLicense);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error checking license type: " + e.getMessage());
        }
    }

    /**
     * 创建用户与许可证的关联
     */
    @PostMapping("/relations")
    @Operation(summary = "创建用户许可证关联", description = "创建用户与许可证的关联")
    public ResponseResult<Boolean> createUserLicenseRelation(@RequestBody CreateRelationRequest request) {
        try {
            boolean created = licenseUserRelationService.createUserLicenseRelation(request.getUserId(), request.getLicenseId());
            return ResponseResult.ok(created);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error creating user license relation: " + e.getMessage());
        }
    }

    /**
     * 根据许可证密钥创建用户与许可证的关联
     */
    @PostMapping("/relations/by-key")
    @Operation(summary = "根据密钥创建用户许可证关联", description = "根据许可证密钥创建用户与许可证的关联")
    public ResponseResult<Boolean> createUserLicenseRelationByKey(@RequestBody CreateRelationByKeyRequest request) {
        try {
            boolean created = licenseUserRelationService.createUserLicenseRelationByKey(request.getUserId(), request.getLicenseKey());
            return ResponseResult.ok(created);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error creating user license relation by key: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID和许可证ID删除关联
     */
    @DeleteMapping("/relations/{userId}/{licenseId}")
    @Operation(summary = "删除用户许可证关联", description = "根据用户ID和许可证ID删除关联")
    public ResponseResult<Boolean> deleteUserLicenseRelation(@PathVariable Integer userId, @PathVariable String licenseId) {
        try {
            boolean deleted = licenseUserRelationService.deleteUserLicenseRelation(userId, licenseId);
            return ResponseResult.ok(deleted);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error deleting user license relation: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID删除所有关联
     */
    @DeleteMapping("/relations/user/{userId}")
    @Operation(summary = "删除用户所有许可证关联", description = "根据用户ID删除所有关联")
    public ResponseResult<Boolean> deleteAllRelationsByUserId(@PathVariable Integer userId) {
        try {
            boolean deleted = licenseUserRelationService.deleteAllRelationsByUserId(userId);
            return ResponseResult.ok(deleted);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error deleting all relations by user ID: " + e.getMessage());
        }
    }

    /**
     * 根据许可证ID删除所有关联
     */
    @DeleteMapping("/relations/license/{licenseId}")
    @Operation(summary = "删除许可证所有用户关联", description = "根据许可证ID删除所有关联")
    public ResponseResult<Boolean> deleteAllRelationsByLicenseId(@PathVariable String licenseId) {
        try {
            boolean deleted = licenseUserRelationService.deleteAllRelationsByLicenseId(licenseId);
            return ResponseResult.ok(deleted);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error deleting all relations by license ID: " + e.getMessage());
        }
    }

    /**
     * 根据许可证密钥查询关联的用户
     */
    @GetMapping("/relations/key/{licenseKey}")
    @Operation(summary = "根据许可证密钥查询用户", description = "根据许可证密钥查询关联的用户")
    public ResponseResult<?> getUsersByLicenseKey(@PathVariable String licenseKey, @RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        try {
            List<?> relations = licenseUserRelationService.getUsersByLicenseKey(licenseKey);
            return ResponseResult.ok(relations);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting users by license key: " + e.getMessage());
        }
    }

    /**
     * 根据许可证密钥和用户ID检查关联是否存在
     */
    @GetMapping("/relations/exists/{licenseKey}/{userId}")
    @Operation(summary = "检查关联是否存在", description = "根据许可证密钥和用户ID检查关联是否存在")
    public ResponseResult<Boolean> existsByLicenseKeyAndUserId(@PathVariable String licenseKey, @PathVariable Integer userId) {
        try {
            boolean exists = licenseUserRelationService.existsByLicenseKeyAndUserId(licenseKey, userId);
            return ResponseResult.ok(exists);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error checking relation existence: " + e.getMessage());
        }
    }

    /**
     * 根据许可证密钥删除所有关联
     */
    @DeleteMapping("/relations/key/{licenseKey}")
    @Operation(summary = "删除许可证密钥所有关联", description = "根据许可证密钥删除所有关联")
    public ResponseResult<Boolean> deleteAllRelationsByLicenseKey(@PathVariable String licenseKey) {
        try {
            boolean deleted = licenseUserRelationService.deleteAllRelationsByLicenseKey(licenseKey);
            return ResponseResult.ok(deleted);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error deleting all relations by license key: " + e.getMessage());
        }
    }

    /**
     * 根据许可证密钥和用户ID删除关联
     */
    @DeleteMapping("/relations/key/{licenseKey}/{userId}")
    @Operation(summary = "根据密钥和用户ID删除关联", description = "根据许可证密钥和用户ID删除关联")
    public ResponseResult<Boolean> deleteRelationByLicenseKeyAndUserId(@PathVariable String licenseKey, @PathVariable Integer userId) {
        try {
            boolean deleted = licenseUserRelationService.deleteRelationByLicenseKeyAndUserId(licenseKey, userId);
            return ResponseResult.ok(deleted);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error deleting relation by license key and user ID: " + e.getMessage());
        }
    }

    /**
     * 根据许可证密钥查询许可证信息
     */
    @GetMapping("/info/key/{licenseKey}")
    @Operation(summary = "根据密钥查询许可证信息", description = "根据许可证密钥查询许可证信息")
    public ResponseResult<LicenseInfo> getLicenseByLicenseKey(@PathVariable String licenseKey) {
        try {
            LicenseInfo licenseInfo = licenseUserRelationService.getLicenseByLicenseKey(licenseKey);
            return ResponseResult.ok(licenseInfo);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting license by license key: " + e.getMessage());
        }
    }

    /**
     * 根据许可证ID查询许可证信息
     */
    @GetMapping("/info/id/{licenseId}")
    @Operation(summary = "根据ID查询许可证信息", description = "根据许可证ID查询许可证信息")
    public ResponseResult<LicenseInfo> getLicenseByLicenseId(@PathVariable String licenseId) {
        try {
            LicenseInfo licenseInfo = licenseUserRelationService.getLicenseByLicenseId(licenseId);
            return ResponseResult.ok(licenseInfo);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting license by license ID: " + e.getMessage());
        }
    }

    /**
     * 批量创建用户与许可证的关联
     */
    @PostMapping("/relations/batch")
    @Operation(summary = "批量创建用户许可证关联", description = "批量创建用户与许可证的关联")
    public ResponseResult<Boolean> batchCreateUserLicenseRelations(@RequestBody BatchRelationRequest request) {
        try {
            boolean created = licenseUserRelationService.batchCreateUserLicenseRelations(request.getUserId(), request.getLicenseKeys());
            return ResponseResult.ok(created);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error batch creating user license relations: " + e.getMessage());
        }
    }

    /**
     * 批量删除用户与许可证的关联
     */
    @DeleteMapping("/relations/batch")
    @Operation(summary = "批量删除用户许可证关联", description = "批量删除用户与许可证的关联")
    public ResponseResult<Boolean> batchDeleteUserLicenseRelations(@RequestBody BatchRelationRequest request) {
        try {
            boolean deleted = licenseUserRelationService.batchDeleteUserLicenseRelations(request.getUserId(), request.getLicenseKeys());
            return ResponseResult.ok(deleted);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error batch deleting user license relations: " + e.getMessage());
        }
    }
}
