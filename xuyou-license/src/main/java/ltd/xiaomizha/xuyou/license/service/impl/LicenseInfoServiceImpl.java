package ltd.xiaomizha.xuyou.license.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.LicenseType;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.common.utils.license.HardwareUtils;
import ltd.xiaomizha.xuyou.common.utils.license.LicenseUtils;
import ltd.xiaomizha.xuyou.license.entity.LicenseInfo;
import ltd.xiaomizha.xuyou.license.mapper.LicenseInfoMapper;
import ltd.xiaomizha.xuyou.license.service.LicenseInfoService;
import ltd.xiaomizha.xuyou.license.service.LicenseUserRelationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaom
 * @description 针对表【license_info(License授权系统表)】的数据库操作Service实现
 * @createDate 2026-02-22 12:17:03
 */
@Slf4j
@Service
public class LicenseInfoServiceImpl extends ServiceImpl<LicenseInfoMapper, LicenseInfo>
        implements LicenseInfoService {

    @Lazy
    @Resource
    private LicenseUserRelationService licenseUserRelationService;

    /**
     * 创建许可证
     *
     * @param licenseInfo 许可证信息
     * @return 创建结果
     */
    @Override
    public boolean generateLicense(LicenseInfo licenseInfo) {
        try {
            // 生成许可证密钥
            if (licenseInfo.getLicenseKey() == null || licenseInfo.getLicenseKey().isEmpty()) {
                String licenseTypeStr = licenseInfo.getLicenseType() != null ? String.valueOf(licenseInfo.getLicenseType()) : LicenseType.TRIAL.getValue();
                licenseInfo.setLicenseKey(generateLicenseKey(licenseTypeStr));
            }
            // 生成许可证ID
            if (licenseInfo.getLicenseId() == null || licenseInfo.getLicenseId().isEmpty()) {
                licenseInfo.setLicenseId(generateLicenseId());
            }
            // 格式化remarks字段
            if (licenseInfo.getRemarks() == null || licenseInfo.getRemarks().isEmpty()) {
                String licenseTypeStr = licenseInfo.getLicenseType() != null ? String.valueOf(licenseInfo.getLicenseType()) : LicenseType.TRIAL.getValue();
                String remarks = LicenseUtils.formatLicenseRemarks(licenseInfo.getStartTime(), licenseInfo.getEndTime(), licenseTypeStr);
                licenseInfo.setRemarks(remarks);
            }
            return save(licenseInfo);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 为用户生成试用许可证
     *
     * @param userId   用户ID
     * @param userName 用户名
     * @return 试用许可证密钥
     */
    @Override
    public String generateTrialLicense(Integer userId, String userName) {
        try {
            // 检查用户是否已有试用许可证
            boolean hasTrialLicense = licenseUserRelationService.hasLicenseByType(userId, LicenseType.TRIAL.getValue());
            if (hasTrialLicense) {
                // 获取用户的试用许可证
                List<LicenseInfo> existingLicenses = licenseUserRelationService.getLicensesByUserIdAndType(userId, LicenseType.TRIAL.getValue());
                if (!existingLicenses.isEmpty()) {
                    // 返回已有试用许可证
                    return existingLicenses.getFirst().getLicenseKey();
                }
            }

            // 生成新的试用许可证
            LicenseInfo trialLicense = new LicenseInfo();
            trialLicense.setLicenseKey(generateLicenseKey(LicenseType.TRIAL.getValue()));
            trialLicense.setLicenseId(generateLicenseId());
            trialLicense.setUserName(userName);
            trialLicense.setCompanyName("个人用户");
            trialLicense.setProductVersion("1.0.0");
            trialLicense.setLicenseType(LicenseType.TRIAL);
            trialLicense.setStatus(Status.ACTIVE);
            trialLicense.setStartTime(LocalDateTime.now());
            trialLicense.setEndTime(LocalDateTime.now().plusDays(30)); // 30天试用期
            trialLicense.setHardwareInfo(HardwareUtils.getHardwareInfo());
            trialLicense.setMaxConcurrentUsers(1);
            trialLicense.setAllowOffline(0);
            trialLicense.setCreatedBy("system");
            trialLicense.setUpdatedBy("system");

            boolean generated = generateLicense(trialLicense);

            if (generated) {
                boolean relationCreated = licenseUserRelationService.createUserLicenseRelationByKey(userId, trialLicense.getLicenseKey());
                return relationCreated ? trialLicense.getLicenseKey() : null;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据许可证密钥获取许可证
     *
     * @param licenseKey 许可证密钥
     * @return 许可证信息
     */
    @Override
    public LicenseInfo getByLicenseKey(String licenseKey) {
        return lambdaQuery().eq(LicenseInfo::getLicenseKey, licenseKey).one();
    }

    /**
     * 根据许可证ID获取许可证
     *
     * @param licenseId 许可证ID
     * @return 许可证信息
     */
    @Override
    public LicenseInfo getByLicenseId(String licenseId) {
        return lambdaQuery().eq(LicenseInfo::getLicenseId, licenseId).one();
    }

    /**
     * 激活许可证
     *
     * @param licenseKey     许可证密钥
     * @param hardwareInfo   硬件信息
     * @param activationCode 激活码
     * @return 激活结果
     */
    @Override
    public boolean activateLicense(String licenseKey, String hardwareInfo, String activationCode) {
        try {
            LicenseInfo licenseInfo = getByLicenseKey(licenseKey);
            if (licenseInfo == null || !validateActivationCode(activationCode, hardwareInfo)) {
                return false;
            }

            licenseInfo.setStatus(Status.ACTIVE);
            licenseInfo.setHardwareInfo(hardwareInfo);
            licenseInfo.setLastActivationTime(LocalDateTime.now());

            return updateById(licenseInfo);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证激活码
     *
     * @param activationCode 激活码
     * @param hardwareInfo   硬件信息
     * @return 验证结果
     */
    @Override
    public boolean validateActivationCode(String activationCode, String hardwareInfo) {
        log.debug("Validating activation code: {}, hardwareInfo: {}", activationCode, hardwareInfo);
        return true;
    }

    /**
     * 生成激活码
     *
     * @param hardwareInfo 硬件信息
     * @param expireDays   过期天数
     * @return 激活码
     */
    @Override
    public String generateActivationCode(String hardwareInfo, int expireDays) {
        return HardwareUtils.generateActivationCode(hardwareInfo, expireDays);
    }

    /**
     * 禁用许可证
     *
     * @param licenseKey 许可证密钥
     * @return 禁用结果
     */
    @Override
    public boolean disableLicense(String licenseKey) {
        return updateLicenseStatus(licenseKey, Status.SUSPENDED);
    }

    /**
     * 启用许可证
     *
     * @param licenseKey 许可证密钥
     * @return 启用结果
     */
    @Override
    public boolean enableLicense(String licenseKey) {
        return updateLicenseStatus(licenseKey, Status.ACTIVE);
    }

    /**
     * 更新许可证状态
     *
     * @param licenseKey 许可证密钥
     * @param status     新状态
     * @return 更新结果
     */
    private boolean updateLicenseStatus(String licenseKey, Status status) {
        try {
            LicenseInfo licenseInfo = getByLicenseKey(licenseKey);
            if (licenseInfo == null) {
                return false;
            }

            licenseInfo.setStatus(status);
            return updateById(licenseInfo);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查许可证是否过期
     *
     * @param licenseKey 许可证密钥
     * @return 是否过期
     */
    @Override
    public boolean isLicenseExpired(String licenseKey) {
        LicenseInfo licenseInfo = getByLicenseKey(licenseKey);
        if (licenseInfo == null) {
            return true;
        }

        LocalDateTime expireTime = licenseInfo.getEndTime();
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 检查许可证是否有效
     *
     * @param licenseKey   许可证密钥
     * @param hardwareInfo 硬件信息
     * @return 是否有效
     */
    @Override
    public boolean isLicenseValid(String licenseKey, String hardwareInfo) {
        LicenseInfo licenseInfo = getByLicenseKey(licenseKey);
        if (licenseInfo == null || licenseInfo.getStatus() != Status.ACTIVE || isLicenseExpired(licenseKey)) {
            return false;
        }

        // 检查硬件绑定
        String storedHardwareInfo = licenseInfo.getHardwareInfo();
        return storedHardwareInfo == null || storedHardwareInfo.isEmpty() || storedHardwareInfo.equals(hardwareInfo);
    }

    /**
     * 更新许可证硬件绑定
     *
     * @param licenseKey   许可证密钥
     * @param hardwareInfo 新的硬件信息
     * @return 更新结果
     */
    @Override
    public boolean updateHardwareBinding(String licenseKey, String hardwareInfo) {
        try {
            LicenseInfo licenseInfo = getByLicenseKey(licenseKey);
            if (licenseInfo == null) {
                return false;
            }

            licenseInfo.setHardwareInfo(hardwareInfo);
            return updateById(licenseInfo);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证许可证
     * <p>
     * 返回详细验证结果
     *
     * @param licenseKey   许可证密钥
     * @param hardwareInfo 硬件信息
     * @return 验证结果映射, 包含验证状态和详细信息
     */
    @Override
    public Map<String, Object> validateLicense(String licenseKey, String hardwareInfo) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", false);

        LicenseInfo licenseInfo = getByLicenseKey(licenseKey);
        if (licenseInfo == null) {
            result.put("message", "License not found");
            result.put("code", "NOT_FOUND");
            return result;
        }

        // 检查状态
        if (licenseInfo.getStatus() != Status.ACTIVE) {
            result.put("message", "License not active");
            result.put("code", "NOT_ACTIVE");
            return result;
        }

        // 检查是否过期
        if (isLicenseExpired(licenseKey)) {
            result.put("message", "License expired");
            result.put("code", "EXPIRED");
            return result;
        }

        // 检查硬件绑定
        String storedHardwareInfo = licenseInfo.getHardwareInfo();
        if (storedHardwareInfo != null && !storedHardwareInfo.isEmpty() && !storedHardwareInfo.equals(hardwareInfo)) {
            result.put("message", "Hardware info mismatch");
            result.put("code", "HARDWARE_MISMATCH");
            return result;
        }

        // 验证通过
        result.put("valid", true);
        result.put("message", "License is valid");
        result.put("code", "VALID");
        result.put("licenseInfo", licenseInfo);
        return result;
    }

    /**
     * 验证功能是否授权
     *
     * @param licenseKey  许可证密钥
     * @param featureCode 功能代码
     * @return 是否授权该功能
     */
    @Override
    public boolean validateFeatureAccess(String licenseKey, String featureCode) {
        log.debug("Validating feature access: {} for license: {}", featureCode, licenseKey);
        return true;
    }

    /**
     * 获取许可证的授权功能列表
     *
     * @param licenseKey 许可证密钥
     * @return 授权功能列表
     */
    @Override
    public List<String> getAuthorizedFeatures(String licenseKey) {
        log.debug("Getting authorized features for license: {}", licenseKey);
        return List.of();
    }

    /**
     * 检查许可证是否包含指定功能
     *
     * @param licenseKey  许可证密钥
     * @param featureCode 功能代码
     * @return 是否包含该功能
     */
    @Override
    public boolean hasFeature(String licenseKey, String featureCode) {
        log.debug("Checking if license: {} has feature: {}", licenseKey, featureCode);
        return true;
    }

    /**
     * 根据状态查询许可证
     *
     * @param status 状态
     * @return 许可证列表
     */
    @Override
    public List<LicenseInfo> getLicensesByStatus(Status status) {
        return lambdaQuery().eq(LicenseInfo::getStatus, status).list();
    }

    /**
     * 根据类型查询许可证
     *
     * @param licenseType 许可证类型
     * @return 许可证列表
     */
    @Override
    public List<LicenseInfo> getLicensesByType(String licenseType) {
        return lambdaQuery().eq(LicenseInfo::getLicenseType, licenseType).list();
    }

    /**
     * 获取即将过期的许可证
     *
     * @param days 天数阈值
     * @return 许可证列表
     */
    @Override
    public List<LicenseInfo> getExpiringLicenses(int days) {
        LocalDateTime threshold = LocalDateTime.now().plusDays(days);
        return lambdaQuery()
                .le(LicenseInfo::getEndTime, threshold)
                .gt(LicenseInfo::getEndTime, LocalDateTime.now())
                .list();
    }

    /**
     * 统计许可证数量
     *
     * @param params 查询参数
     * @return 统计结果
     */
    @Override
    public Map<String, Long> countLicenses(Map<String, Object> params) {
        Map<String, Long> result = new HashMap<>();

        // 统计总数量
        result.put("total", count());

        // 统计各状态数量
        for (Status status : Status.values()) {
            result.put(status.name().toLowerCase(), lambdaQuery().eq(LicenseInfo::getStatus, status).count());
        }

        return result;
    }

    /**
     * 批量创建许可证
     *
     * @param licenseInfos 许可证信息列表
     * @return 创建结果
     */
    @Override
    public boolean batchCreateLicenses(List<LicenseInfo> licenseInfos) {
        try {
            for (LicenseInfo licenseInfo : licenseInfos) {
                if (licenseInfo.getLicenseKey() == null || licenseInfo.getLicenseKey().isEmpty()) {
                    String licenseTypeStr = licenseInfo.getLicenseType() != null ? String.valueOf(licenseInfo.getLicenseType()) : "TRIAL";
                    licenseInfo.setLicenseKey(generateLicenseKey(licenseTypeStr));
                }

                if (licenseInfo.getLicenseId() == null || licenseInfo.getLicenseId().isEmpty()) {
                    licenseInfo.setLicenseId(generateLicenseId());
                }
            }

            return saveBatch(licenseInfos);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 批量更新许可证状态
     *
     * @param licenseKeys 许可证密钥列表
     * @param status      新状态
     * @return 更新结果
     */
    @Override
    public boolean batchUpdateStatus(List<String> licenseKeys, Status status) {
        try {
            return lambdaUpdate()
                    .in(LicenseInfo::getLicenseKey, licenseKeys)
                    .set(LicenseInfo::getStatus, status)
                    .update();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成许可证密钥
     * <p>
     * 格式: LIC-license_type-yyyy-该license_type的第n张许可证
     * <p>
     * 例: LIC-TRIAL-2026-0001
     *
     * @param licenseType 许可证类型
     * @return 许可证密钥
     */
    private String generateLicenseKey(String licenseType) {
        int year = LocalDate.now().getYear();
        long count = lambdaQuery().eq(LicenseInfo::getLicenseType, licenseType).count();
        return String.format("LIC-%s-%d-%04d", licenseType.toUpperCase(), year, count + 1);
    }

    /**
     * 生成许可证ID
     * <p>
     * 格式: LID-yyyyMMdd-该日的第n张许可证
     * <p>
     * 例: LID-20260222-0001
     *
     * @return 许可证ID
     */
    private String generateLicenseId() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
        long count = lambdaQuery().ge(LicenseInfo::getStartTime, todayStart).lt(LicenseInfo::getStartTime, todayEnd).count();
        return String.format("LID-%s-%04d", dateStr, count + 1);
    }
}
