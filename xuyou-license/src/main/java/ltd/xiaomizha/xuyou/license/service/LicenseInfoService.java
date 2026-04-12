package ltd.xiaomizha.xuyou.license.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.license.entity.LicenseInfo;

import java.util.List;
import java.util.Map;

/**
 * @author xiaom
 * @description 针对表【license_info(License授权系统表)】的数据库操作Service
 * @createDate 2026-02-22 12:17:03
 */
public interface LicenseInfoService extends IService<LicenseInfo> {

    /**
     * 创建许可证
     *
     * @param licenseInfo 许可证信息
     * @return 创建结果
     */
    boolean generateLicense(LicenseInfo licenseInfo);

    /**
     * 为用户生成试用许可证
     *
     * @param userId   用户ID
     * @param userName 用户名
     * @return 试用许可证密钥
     */
    String generateTrialLicense(Integer userId, String userName);

    /**
     * 根据许可证密钥获取许可证
     *
     * @param licenseKey 许可证密钥
     * @return 许可证信息
     */
    LicenseInfo getByLicenseKey(String licenseKey);

    /**
     * 根据许可证ID获取许可证
     *
     * @param licenseId 许可证ID
     * @return 许可证信息
     */
    LicenseInfo getByLicenseId(String licenseId);

    /**
     * 激活许可证
     *
     * @param licenseKey     许可证密钥
     * @param hardwareInfo   硬件信息
     * @param activationCode 激活码
     * @return 激活结果
     */
    boolean activateLicense(String licenseKey, String hardwareInfo, String activationCode);

    /**
     * 验证激活码
     *
     * @param activationCode 激活码
     * @param hardwareInfo   硬件信息
     * @return 验证结果
     */
    boolean validateActivationCode(String activationCode, String hardwareInfo);

    /**
     * 生成激活码
     *
     * @param hardwareInfo 硬件信息
     * @param expireDays   过期天数
     * @return 激活码
     */
    String generateActivationCode(String hardwareInfo, int expireDays);

    /**
     * 禁用许可证
     *
     * @param licenseKey 许可证密钥
     * @return 禁用结果
     */
    boolean disableLicense(String licenseKey);

    /**
     * 启用许可证
     *
     * @param licenseKey 许可证密钥
     * @return 启用结果
     */
    boolean enableLicense(String licenseKey);

    /**
     * 检查许可证是否过期
     *
     * @param licenseKey 许可证密钥
     * @return 是否过期
     */
    boolean isLicenseExpired(String licenseKey);

    /**
     * 检查许可证是否有效
     *
     * @param licenseKey   许可证密钥
     * @param hardwareInfo 硬件信息
     * @return 是否有效
     */
    boolean isLicenseValid(String licenseKey, String hardwareInfo);

    /**
     * 更新许可证硬件绑定
     *
     * @param licenseKey   许可证密钥
     * @param hardwareInfo 新的硬件信息
     * @return 更新结果
     */
    boolean updateHardwareBinding(String licenseKey, String hardwareInfo);

    /**
     * 验证许可证
     * <p>
     * 返回详细验证结果
     *
     * @param licenseKey   许可证密钥
     * @param hardwareInfo 硬件信息
     * @return 验证结果映射，包含验证状态和详细信息
     */
    Map<String, Object> validateLicense(String licenseKey, String hardwareInfo);

    /**
     * 验证功能是否授权
     *
     * @param licenseKey  许可证密钥
     * @param featureCode 功能代码
     * @return 是否授权该功能
     */
    boolean validateFeatureAccess(String licenseKey, String featureCode);

    /**
     * 获取许可证的授权功能列表
     *
     * @param licenseKey 许可证密钥
     * @return 授权功能列表
     */
    List<String> getAuthorizedFeatures(String licenseKey);

    /**
     * 检查许可证是否包含指定功能
     *
     * @param licenseKey  许可证密钥
     * @param featureCode 功能代码
     * @return 是否包含该功能
     */
    boolean hasFeature(String licenseKey, String featureCode);

    /**
     * 根据状态查询许可证
     *
     * @param status 状态
     * @return 许可证列表
     */
    List<LicenseInfo> getLicensesByStatus(Status status);

    /**
     * 根据类型查询许可证
     *
     * @param licenseType 许可证类型
     * @return 许可证列表
     */
    List<LicenseInfo> getLicensesByType(String licenseType);

    /**
     * 获取即将过期的许可证
     *
     * @param days 天数阈值
     * @return 许可证列表
     */
    List<LicenseInfo> getExpiringLicenses(int days);

    /**
     * 统计许可证数量
     *
     * @param params 查询参数
     * @return 统计结果
     */
    Map<String, Long> countLicenses(Map<String, Object> params);

    /**
     * 批量创建许可证
     *
     * @param licenseInfos 许可证信息列表
     * @return 创建结果
     */
    boolean batchCreateLicenses(List<LicenseInfo> licenseInfos);

    /**
     * 批量更新许可证状态
     *
     * @param licenseKeys 许可证密钥列表
     * @param status      新状态
     * @return 更新结果
     */
    boolean batchUpdateStatus(List<String> licenseKeys, Status status);

    /**
     * 获取当前硬件绑定的有效许可证
     * <p>
     * 查找逻辑: 状态为ACTIVE + 未过期 + 硬件匹配, 或未绑定硬件
     *
     * @param hardwareInfo 硬件信息
     * @return 当前有效的许可证, 无则返回null
     */
    LicenseInfo getCurrentValidLicense(String hardwareInfo);

}

