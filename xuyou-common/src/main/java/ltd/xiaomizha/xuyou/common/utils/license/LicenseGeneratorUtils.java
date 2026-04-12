package ltd.xiaomizha.xuyou.common.utils.license;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.LicenseType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * License 生成工具
 */
@Slf4j
@Data
@Component
public class LicenseGeneratorUtils {

    @Value("${xuyou.license.storage-path:./licenses}")
    private String licenseStoragePath;

    @Value("${xuyou.license.trial.enabled:true}")
    private Boolean trialEnabled;

    @Value("${xuyou.license.trial.hours:2}")
    private Integer trialHours;

    @Value("${xuyou.license.trial.redis-key-prefix:license:trial:}")
    private String trialRedisKeyPrefix;

    @Value("${xuyou.license.rsa.private-key-path:classpath:keys/license_private.key}")
    private String privateKeyPath;

    @Value("${xuyou.license.rsa.public-key-path:classpath:keys/license_public.key}")
    private String publicKeyPath;

    public String getLicenseStoragePath() {
        return licenseStoragePath;
    }

    public Boolean getTrialEnabled() {
        return trialEnabled;
    }

    public Integer getTrialHours() {
        return trialHours;
    }

    public String getTrialRedisKeyPrefix() {
        return trialRedisKeyPrefix;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    private static final String LIC_HEADER = "XIAOMIZHA-LICENSE-V1";
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成 License 文件
     *
     * @param params          License 参数
     * @param privateKeyPath  私钥文件路径
     * @param outputPath      输出文件路径
     * @param publicKeyBase64 公钥 (可选, 写入lic文件中)
     */
    public static void generate(LicenseParams params, String privateKeyPath, String outputPath, String publicKeyBase64) {
        PrivateKey privateKey = LicenseCryptUtils.getPrivateKeyFromBase64(FileUtil.readUtf8String(privateKeyPath));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(params.expireDays);

        Map<String, Object> licenseData = new LinkedHashMap<>();
        licenseData.put("issuedTo", params.issuedTo);
        licenseData.put("companyName", params.companyName);
        licenseData.put("licenseType", params.licenseType.name());
        licenseData.put("issueAt", now.format(DT_FMT));
        licenseData.put("expireAt", expireAt.format(DT_FMT));
        licenseData.put("expireDays", params.expireDays);
        licenseData.put("hardwareId", params.hardwareId);
        licenseData.put("maxConcurrentUsers", params.maxConcurrentUsers);
        licenseData.put("features", params.features);
        licenseData.put("productVersion", params.productVersion);
        licenseData.put("generator", "XIAOMIZHA-License-Generator-v1.0");
        licenseData.put("generatedAt", now.format(DT_FMT));

        String jsonStr = JSONUtil.toJsonStr(licenseData);
        String signature = LicenseCryptUtils.signLicense(jsonStr, privateKey);
        String encryptedContent = Base64.getEncoder().encodeToString(jsonStr.getBytes());

        StringBuilder licContent = new StringBuilder();
        licContent.append(LIC_HEADER).append("\n");
        licContent.append("---BEGIN LICENSE DATA---").append("\n");
        licContent.append(wrapLine(encryptedContent, 76)).append("\n");
        licContent.append("---END LICENSE DATA---").append("\n");
        licContent.append("---BEGIN SIGNATURE---").append("\n");
        licContent.append(wrapLine(signature, 76)).append("\n");
        licContent.append("---END SIGNATURE---").append("\n");
        if (StrUtil.isNotBlank(publicKeyBase64)) {
            licContent.append("---PUBLIC KEY---").append("\n");
            licContent.append(wrapLine(publicKeyBase64, 76)).append("\n");
            licContent.append("---END PUBLIC KEY---").append("\n");
        }
        licContent.append("---INFO---").append("\n");
        licContent.append(String.format("IssuedTo: %s\n", params.issuedTo));
        licContent.append(String.format("Type: %s\n", params.licenseType.name()));
        licContent.append(String.format("Valid: %s ~ %s\n", now.format(DT_FMT), expireAt.format(DT_FMT)));
        licContent.append(String.format("Remarks: %s\n",
                LicenseUtils.formatLicenseRemarks(now, expireAt, params.licenseType.name())));
        licContent.append("---END INFO---").append("\n");

        FileUtil.writeUtf8String(licContent.toString(), outputPath);
    }

    /**
     * 长文本换行处理
     */
    private static String wrapLine(String text, int lineLength) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += lineLength) {
            if (i > 0) {
                result.append("\n");
            }
            result.append(text, i, Math.min(i + lineLength, text.length()));
        }
        return result.toString();
    }

    /**
     * License 生成参数
     */
    public static class LicenseParams {
        public String issuedTo;
        public String companyName;
        public LicenseType licenseType = LicenseType.BASIC;
        public int expireDays = 365;
        public String hardwareId;
        public Integer maxConcurrentUsers;
        public String features;
        public String productVersion = "1.0.0";
    }

}
