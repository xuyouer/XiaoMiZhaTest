package ltd.xiaomizha.xuyou.license.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.license.entity.LicenseInfo;
import ltd.xiaomizha.xuyou.license.entity.LicenseUserRelation;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【license_user_relation(License用户关联表)】的数据库操作Service
 * @createDate 2026-02-23 11:17:17
 */
public interface LicenseUserRelationService extends IService<LicenseUserRelation> {

    /**
     * 根据用户ID查询用户名下的所有许可证
     *
     * @param userId 用户ID
     * @return 许可证列表
     */
    List<LicenseInfo> getLicensesByUserId(Integer userId);

    /**
     * 根据许可证ID查询关联的用户
     *
     * @param licenseId 许可证ID
     * @return 用户关联列表
     */
    List<LicenseUserRelation> getUsersByLicenseId(String licenseId);

    /**
     * 根据用户ID和许可证类型查询许可证
     *
     * @param userId      用户ID
     * @param licenseType 许可证类型
     * @return 许可证列表
     */
    List<LicenseInfo> getLicensesByUserIdAndType(Integer userId, String licenseType);

    /**
     * 检查用户是否已有指定类型的许可证
     *
     * @param userId      用户ID
     * @param licenseType 许可证类型
     * @return 是否已有该类型许可证
     */
    boolean hasLicenseByType(Integer userId, String licenseType);

    /**
     * 创建用户与许可证的关联
     *
     * @param userId    用户ID
     * @param licenseId 许可证ID
     * @return 创建结果
     */
    boolean createUserLicenseRelation(Integer userId, String licenseId);

    /**
     * 根据许可证密钥创建用户与许可证的关联
     *
     * @param userId     用户ID
     * @param licenseKey 许可证密钥
     * @return 创建结果
     */
    boolean createUserLicenseRelationByKey(Integer userId, String licenseKey);

    /**
     * 根据用户ID和许可证ID删除关联
     *
     * @param userId    用户ID
     * @param licenseId 许可证ID
     * @return 删除结果
     */
    boolean deleteUserLicenseRelation(Integer userId, String licenseId);

    /**
     * 根据用户ID删除所有关联
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteAllRelationsByUserId(Integer userId);

    /**
     * 根据许可证ID删除所有关联
     *
     * @param licenseId 许可证ID
     * @return 删除结果
     */
    boolean deleteAllRelationsByLicenseId(String licenseId);

    /**
     * 根据许可证密钥查询关联的用户
     *
     * @param licenseKey 许可证密钥
     * @return 用户关联列表
     */
    List<LicenseUserRelation> getUsersByLicenseKey(String licenseKey);

    /**
     * 根据许可证密钥和用户ID检查关联是否存在
     *
     * @param licenseKey 许可证密钥
     * @param userId     用户ID
     * @return 关联是否存在
     */
    boolean existsByLicenseKeyAndUserId(String licenseKey, Integer userId);

    /**
     * 根据许可证密钥删除所有关联
     *
     * @param licenseKey 许可证密钥
     * @return 删除结果
     */
    boolean deleteAllRelationsByLicenseKey(String licenseKey);

    /**
     * 根据许可证密钥和用户ID删除关联
     *
     * @param licenseKey 许可证密钥
     * @param userId     用户ID
     * @return 删除结果
     */
    boolean deleteRelationByLicenseKeyAndUserId(String licenseKey, Integer userId);

    /**
     * 根据许可证密钥查询许可证信息
     *
     * @param licenseKey 许可证密钥
     * @return 许可证信息
     */
    LicenseInfo getLicenseByLicenseKey(String licenseKey);

    /**
     * 根据许可证ID查询许可证信息
     *
     * @param licenseId 许可证ID
     * @return 许可证信息
     */
    LicenseInfo getLicenseByLicenseId(String licenseId);

    /**
     * 批量创建用户与许可证的关联
     *
     * @param userId      用户ID
     * @param licenseKeys 许可证密钥列表
     * @return 创建结果
     */
    boolean batchCreateUserLicenseRelations(Integer userId, List<String> licenseKeys);

    /**
     * 批量删除用户与许可证的关联
     *
     * @param userId      用户ID
     * @param licenseKeys 许可证密钥列表
     * @return 删除结果
     */
    boolean batchDeleteUserLicenseRelations(Integer userId, List<String> licenseKeys);

}
