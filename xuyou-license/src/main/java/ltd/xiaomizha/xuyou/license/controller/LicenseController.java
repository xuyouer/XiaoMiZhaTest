package ltd.xiaomizha.xuyou.license.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.LicenseType;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.common.utils.license.HardwareUtils;
import ltd.xiaomizha.xuyou.common.utils.license.LicenseCryptUtils;
import ltd.xiaomizha.xuyou.common.utils.license.LicenseGeneratorUtils;
import ltd.xiaomizha.xuyou.license.entity.LicenseInfo;
import ltd.xiaomizha.xuyou.license.service.LicenseInfoService;
import ltd.xiaomizha.xuyou.license.service.LicenseUserRelationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("license")
@Tag(name = "许可证服务管理", description = "许可证服务API")
public class LicenseController {

    @Resource
    private LicenseInfoService licenseInfoService;

    @Resource
    private LicenseUserRelationService licenseUserRelationService;

    @Resource
    private LicenseGeneratorUtils licenseGeneratorUtils;

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

    @Data
    public static class MachineCodeResponse {
        private String machineCode;
        private String osName;
        private String osVersion;
        private String javaVersion;
        private String userName;
        private long timestamp;

        public MachineCodeResponse(String machineCode) {
            this.machineCode = machineCode;
            this.osName = System.getProperty("os.name");
            this.osVersion = System.getProperty("os.version");
            this.javaVersion = System.getProperty("java.version");
            this.userName = System.getProperty("user.name");
            this.timestamp = System.currentTimeMillis();
        }
    }

    @Data
    public static class LicenseStatusResponse {
        private boolean valid;
        private String code;
        private String message;
        private String issuedTo;
        private String companyName;
        private LocalDateTime issueAt;
        private LocalDateTime expireAt;
        private Long remainingDays;
        private Long remainingMinutes;
        private boolean isTrial;
        private String licenseType;
        private List<String> features;
        private String hardwareId;
        private String productVersion;
        private Integer maxConcurrentUsers;
        private Integer allowOffline;
        private String remarks;
        private LocalDateTime lastActivationTime;
    }

    @Data
    public static class UploadResult {
        private boolean success;
        private String fileName;
        private String savePath;
        private String licenseKey;
        private String message;
    }

    @GetMapping(value = {"/list", "/list/{current}", "/list/{current}/{pageSize}"})
    @Operation(summary = "分页获取所有许可证", description = "分页获取所有许可证列表")
    @Parameters({
            @Parameter(name = "current", description = "当前页码", example = "1"),
            @Parameter(name = "pageSize", description = "每页条数", example = "10")
    })
    public ResponseResultPage<LicenseInfo> getLicenseList(
            @PathVariable(required = false) Long current,
            @PathVariable(required = false) Long pageSize,
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size) {
        try {
            long currentPage = current != null ? current : (page != null ? page : 1);
            long pageSizeValue = pageSize != null ? pageSize : (size != null ? size : 10);
            Page<LicenseInfo> pageResult = licenseInfoService.page(
                    ResponseResultPage.getPage(currentPage, pageSizeValue)
            );
            return ResponseResultPage.ok(pageResult);
        } catch (Exception e) {
            return ResponseResultPage.empty();
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
     * 获取当前机器码
     */
    @GetMapping("/machine-code")
    @Operation(summary = "获取当前机器码", description = "获取当前服务器的硬件机器码, 用于申请许可证")
    public ResponseResult<MachineCodeResponse> getMachineCode() {
        try {
            String machineCode = HardwareUtils.getHardwareInfo();
            return ResponseResult.ok(new MachineCodeResponse(machineCode));
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting machine code: " + e.getMessage());
        }
    }

    /**
     * 获取当前License详细状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取License详细状态", description = "获取当前系统License的完整状态信息, 包含剩余时间、功能列表等")
    public ResponseResult<LicenseStatusResponse> getLicenseStatus() {
        try {
            String hardwareInfo = HardwareUtils.getHardwareInfo();
            LicenseInfo licenseInfo = licenseInfoService.getCurrentValidLicense(hardwareInfo);
            LicenseStatusResponse response = new LicenseStatusResponse();

            if (licenseInfo != null) {
                response.setValid(true);
                response.setCode("VALID");
                response.setMessage("License is valid");
                response.setIssuedTo(licenseInfo.getUserName());
                response.setCompanyName(licenseInfo.getCompanyName());
                response.setIssueAt(licenseInfo.getStartTime());
                response.setExpireAt(licenseInfo.getEndTime());
                response.setTrial(licenseInfo.getLicenseType() != null && licenseInfo.getLicenseType() == LicenseType.TRIAL);
                response.setLicenseType(licenseInfo.getLicenseType() != null ? licenseInfo.getLicenseType().name() : null);
                response.setHardwareId(licenseInfo.getHardwareInfo());
                response.setProductVersion(licenseInfo.getProductVersion());
                response.setMaxConcurrentUsers(licenseInfo.getMaxConcurrentUsers());
                response.setAllowOffline(licenseInfo.getAllowOffline());
                response.setRemarks(licenseInfo.getRemarks());
                response.setLastActivationTime(licenseInfo.getLastActivationTime());

                if (licenseInfo.getFeatures() != null && !licenseInfo.getFeatures().isEmpty()) {
                    response.setFeatures(List.of(licenseInfo.getFeatures().split(",")));
                } else {
                    response.setFeatures(List.of());
                }

                if (licenseInfo.getEndTime() != null) {
                    long remainingMillis = Duration.between(LocalDateTime.now(), licenseInfo.getEndTime()).toMillis();
                    if (remainingMillis > 0) {
                        if (response.isTrial()) {
                            response.setRemainingMinutes(remainingMillis / (60 * 1000));
                        } else {
                            response.setRemainingDays(remainingMillis / (24 * 60 * 60 * 1000));
                        }
                    } else {
                        response.setRemainingDays(0L);
                        response.setRemainingMinutes(0L);
                    }
                }
            } else {
                response.setValid(false);
                response.setCode("NO_LICENSE");
                response.setMessage("未找到有效的许可证");
            }
            return ResponseResult.ok(response);
        } catch (Exception e) {
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "Error getting license status: " + e.getMessage());
        }
    }

    /**
     * 上传 License 文件
     */
    @PostMapping("/upload")
    @Operation(summary = "上传License文件", description = "上传.lic格式的License文件, 解析并导入到系统")
    public ResponseResult<UploadResult> uploadLicense(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "上传文件不能为空");
            }
            long maxSize = 10 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "文件大小超出限制 (最大10MB)");
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".lic")) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "仅支持.lic格式文件");
            }

            // 过滤文件名中的路径遍历字符
            String safeFilename = sanitizeFilename(originalFilename);
            if (safeFilename.isEmpty()) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "无效的文件名");
            }

            // 构建路径
            String storagePath = licenseGeneratorUtils.getLicenseStoragePath();
            File storageDir = new File(storagePath);
            if (!storageDir.exists()) {
                boolean created = storageDir.mkdirs();
                if (!created) {
                    log.error("创建 License 存储目录失败: {}", storagePath);
                    return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "创建存储目录失败");
                }
                log.info("创建 License 存储目录: {}", storageDir.getAbsolutePath());
            }
            // 保存文件
            File targetFile = new File(storageDir, safeFilename);
            file.transferTo(targetFile);
            log.info("License 文件已保存: {} (大小: {} bytes)", targetFile.getAbsolutePath(), targetFile.length());
            // 读取并解析 License 文件内容
            String content = FileUtil.readUtf8String(targetFile);
            Map<String, String> parsedData = parseLicFile(content);
            // 校验 License 文件格式
            if (parsedData.isEmpty() || !parsedData.containsKey("DATA")) {
                // 删除无效文件
                targetFile.delete();
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "License 文件格式不正确或已损坏");
            }
            // Base64 解码获取 JSON 数据
            String encryptedData = parsedData.get("DATA");
            String jsonStr = new String(Base64.getDecoder().decode(encryptedData));
            // RSA 签名校验
            String signature = parsedData.getOrDefault("SIGNATURE", "");
            String publicKeyBase64 = parsedData.getOrDefault("PUBLIC_KEY", "");
            if (StrUtil.isNotBlank(signature)) {
                if (StrUtil.isBlank(publicKeyBase64)) {
                    log.warn("License 文件包含签名但缺少公钥, 跳过签名校验: {}", safeFilename);
                } else {
                    try {
                        PublicKey publicKey = LicenseCryptUtils.getPublicKeyFromBase64(publicKeyBase64);
                        boolean signValid = LicenseCryptUtils.verifyLicense(jsonStr, signature, publicKey);
                        if (!signValid) {
                            targetFile.delete();
                            log.warn("License 文件签名校验失败, 可能被篡改: {}", safeFilename);
                            return ResponseResult.error(ResultEnum.UNAUTHORIZED.getCode(), "License 文件签名校验失败, 文件可能被篡改");
                        }
                        log.info("License 文件签名校验通过");
                    } catch (Exception e) {
                        log.warn("License 文件公钥解析失败, 跳过签名校验: {} - {}", safeFilename, e.getMessage());
                    }
                }
            }

            JSONObject licenseJson = JSONUtil.parseObj(jsonStr);
            LicenseInfo licenseInfo = new LicenseInfo();
            licenseInfo.setLicenseKey(licenseJson.getStr("licenseKey", "LIC-" + System.currentTimeMillis()));
            licenseInfo.setLicenseId(licenseJson.getStr("licenseId", UUID.randomUUID().toString().replace("-", "")));
            licenseInfo.setCompanyName(licenseJson.getStr("companyName"));
            licenseInfo.setUserName(licenseJson.getStr("issuedTo"));
            licenseInfo.setProductVersion(licenseJson.getStr("productVersion", "1.0.0"));
            // 解析许可证类型
            String licenseTypeStr = licenseJson.getStr("licenseType", "BASIC");
            try {
                licenseInfo.setLicenseType(LicenseType.valueOf(licenseTypeStr));
            } catch (Exception e) {
                licenseInfo.setLicenseType(LicenseType.BASIC);
            }
            licenseInfo.setStatus(Status.ACTIVE);
            // 解析时间
            DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String issueAtStr = licenseJson.getStr("issueAt");
            String expireAtStr = licenseJson.getStr("expireAt");
            if (StrUtil.isNotBlank(issueAtStr)) {
                licenseInfo.setStartTime(LocalDateTime.parse(issueAtStr, dtFmt));
            } else {
                licenseInfo.setStartTime(LocalDateTime.now());
            }
            if (StrUtil.isNotBlank(expireAtStr)) {
                licenseInfo.setEndTime(LocalDateTime.parse(expireAtStr, dtFmt));
            } else {
                licenseInfo.setEndTime(LocalDateTime.now().plusDays(365)); // 默认1年
            }
            // 解析硬件绑定信息
            licenseInfo.setHardwareInfo(licenseJson.getStr("hardwareId"));
            licenseInfo.setMaxConcurrentUsers(licenseJson.getInt("maxConcurrentUsers", 1));
            licenseInfo.setFeatures(licenseJson.getStr("features", "[]"));
            // 设置备注
            String remarks = String.format("License类型: %s | 授权给: %s | 有效期: %s ~ %s",
                    licenseInfo.getLicenseType(),
                    licenseInfo.getCompanyName(),
                    licenseInfo.getStartTime(),
                    licenseInfo.getEndTime()
            );
            licenseInfo.setRemarks(remarks);

            boolean saved;
            LicenseInfo existingLicense = licenseInfoService.getByLicenseKey(licenseInfo.getLicenseKey());
            if (existingLicense != null) {
                licenseInfo.setId(existingLicense.getId());
                saved = licenseInfoService.updateById(licenseInfo);
                log.info("License 已更新: key={}", licenseInfo.getLicenseKey());
            } else {
                saved = licenseInfoService.save(licenseInfo);
                log.info("License 已创建: key={}", licenseInfo.getLicenseKey());
            }
            if (!saved) {
                return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "保存 License 信息到数据库失败");
            }

            UploadResult result = new UploadResult();
            result.setSuccess(true);
            result.setFileName(safeFilename);
            result.setSavePath(targetFile.getAbsolutePath());
            result.setLicenseKey(licenseInfo.getLicenseKey());
            result.setMessage("License 文件上传并解析成功");
            log.info("License 文件上传成功: filename={}, key={}, path={}", safeFilename, licenseInfo.getLicenseKey(), targetFile.getAbsolutePath());

            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("上传 License 文件失败", e);
            return ResponseResult.error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), "上传 License 文件失败: " + e.getMessage());
        }
    }

    /**
     * 安全处理文件名, 防止路径遍历攻击
     *
     * @param filename 原始文件名
     * @return 安全的文件名
     */
    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unknown_" + System.currentTimeMillis() + ".lic";
        }

        // 提取纯文件名
        String safeName = filename;
        // 替换反斜杠为正斜杠
        safeName = safeName.replace("\\", "/");
        // 取最后一个 / 之后的部分作为文件名
        int lastSlashIdx = safeName.lastIndexOf('/');
        if (lastSlashIdx >= 0) {
            safeName = safeName.substring(lastSlashIdx + 1);
        }
        // 移除危险字符
        safeName = safeName.replaceAll("[\\x00-\\x1f]", "");
        // 移除路径遍历关键字
        safeName = safeName.replace("..", "").replace("./", "").replace(".\\", "");
        // 处理后文件名为空或只有扩展名
        // 使用原始文件名的安全部分
        if (safeName.isBlank() || safeName.equals(".lic")) {
            // 提取原始文件名的字母数字部分
            String baseName = filename.replaceAll("[^a-zA-Z0-9._-]", "");
            if (baseName.isBlank()) {
                baseName = "uploaded_" + System.currentTimeMillis();
            }
            // 确保 .lic 扩展名
            if (!baseName.toLowerCase().endsWith(".lic")) {
                baseName += ".lic";
            }
            safeName = baseName;
        }
        // 限制文件名长度
        if (safeName.length() > 50) {
            String ext = safeName.contains(".") ? safeName.substring(safeName.lastIndexOf(".")) : ".lic";
            safeName = safeName.substring(0, 46) + ext;
        }
        // URL 编码处理
        try {
            // 尝试解码 URL 编码的文件名
            String decoded = java.net.URLDecoder.decode(safeName, StandardCharsets.UTF_8);
            // 再次清理解码后的内容
            decoded = decoded.replaceAll("[^a-zA-Z0-9._\\u4e00-\\u9fa5-]", "_");
            if (decoded.length() > 5) { // 至少保留一些有效字符才使用解码后的名称
                safeName = decoded;
            }
        } catch (Exception e) {
            // URL 解码失败, 使用原安全名称
        }
        // 添加唯一时间戳前缀
        safeName = System.currentTimeMillis() + "_" + safeName;

        log.debug("文件名安全处理: '{}' -> '{}'", filename, safeName);

        return safeName;
    }

    private Map<String, String> parseLicFile(String content) {
        Map<String, String> result = new HashMap<>();
        String[] sections = content.split("---");
        for (String section : sections) {
            String trimmed = section.trim();
            if (trimmed.startsWith("BEGIN LICENSE DATA")) {
                int endIdx = trimmed.indexOf("END LICENSE DATA");
                if (endIdx > 0) {
                    result.put("DATA", trimmed.substring("BEGIN LICENSE DATA".length(), endIdx).trim());
                }
            } else if (trimmed.startsWith("BEGIN SIGNATURE")) {
                int endIdx = trimmed.indexOf("END SIGNATURE");
                if (endIdx > 0) {
                    result.put("SIGNATURE", trimmed.substring("BEGIN SIGNATURE".length(), endIdx).trim());
                }
            } else if (trimmed.startsWith("PUBLIC KEY")) {
                int endIdx = trimmed.indexOf("END PUBLIC KEY");
                if (endIdx > 0) {
                    result.put("PUBLIC_KEY", trimmed.substring("PUBLIC KEY".length(), endIdx).trim());
                }
            }
        }
        return result;
    }

    /**
     * 批量更新许可证状态
     */
    @PostMapping("/batch-update-status")
    @Operation(summary = "批量更新许可证状态", description = "批量更新多个许可证的状态")
    public ResponseResult<Boolean> batchUpdateStatus(@RequestBody BatchUpdateStatusRequest request) {
        try {
            // Status status = Status.valueOf(request.getStatus().toUpperCase());
            if (StrUtil.isBlank(request.getStatus())) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "状态值不能为空");
            }
            Status status;
            try {
                status = Status.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseResult.error(ResultEnum.BAD_REQUEST.getCode(), "无效的许可证状态: " + request.getStatus());
            }
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
