package ltd.xiaomizha.xuyou.license.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.license.entity.LicenseInfo;
import ltd.xiaomizha.xuyou.license.entity.LicenseUserRelation;
import ltd.xiaomizha.xuyou.license.mapper.LicenseUserRelationMapper;
import ltd.xiaomizha.xuyou.license.service.LicenseInfoService;
import ltd.xiaomizha.xuyou.license.service.LicenseUserRelationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaom
 * @description 针对表【license_user_relation(License用户关联表)】的数据库操作Service实现
 * @createDate 2026-02-23 11:17:17
 */
@Slf4j
@Service
public class LicenseUserRelationServiceImpl extends ServiceImpl<LicenseUserRelationMapper, LicenseUserRelation>
        implements LicenseUserRelationService {

    @Lazy
    @Resource
    private LicenseInfoService licenseInfoService;

    private static final Long DEFAULT_ASSIGNED_BY = 10000L;

    /**
     * 根据用户ID查询用户名下的所有许可证
     *
     * @param userId 用户ID
     * @return 许可证列表
     */
    @Override
    public List<LicenseInfo> getLicensesByUserId(Integer userId) {
        List<LicenseUserRelation> relations = lambdaQuery()
                .eq(LicenseUserRelation::getUserId, userId.longValue())
                .list();

        List<LicenseInfo> licenses = new ArrayList<>();
        for (LicenseUserRelation relation : relations) {
            LicenseInfo license = getLicenseByLicenseKey(relation.getLicenseKey());
            if (license != null) {
                licenses.add(license);
            }
        }
        return licenses;
    }

    /**
     * 根据许可证ID查询关联的用户
     *
     * @param licenseId 许可证ID
     * @return 用户关联列表
     */
    @Override
    public List<LicenseUserRelation> getUsersByLicenseId(String licenseId) {
        LicenseInfo license = getLicenseByLicenseId(licenseId);
        if (license == null) {
            return new ArrayList<>();
        }
        return lambdaQuery()
                .eq(LicenseUserRelation::getLicenseKey, license.getLicenseKey())
                .list();
    }

    /**
     * 根据用户ID和许可证类型查询许可证
     *
     * @param userId      用户ID
     * @param licenseType 许可证类型
     * @return 许可证列表
     */
    @Override
    public List<LicenseInfo> getLicensesByUserIdAndType(Integer userId, String licenseType) {
        List<LicenseUserRelation> relations = lambdaQuery()
                .eq(LicenseUserRelation::getUserId, userId.longValue())
                .list();
        List<LicenseInfo> licenses = new ArrayList<>();
        for (LicenseUserRelation relation : relations) {
            LicenseInfo license = getLicenseByLicenseKey(relation.getLicenseKey());
            if (license != null && Objects.equals(licenseType, String.valueOf(license.getLicenseType()))) {
                licenses.add(license);
            }
        }
        return licenses;
    }

    /**
     * 检查用户是否已有指定类型的许可证
     *
     * @param userId      用户ID
     * @param licenseType 许可证类型
     * @return 是否已有该类型许可证
     */
    @Override
    public boolean hasLicenseByType(Integer userId, String licenseType) {
        return !getLicensesByUserIdAndType(userId, licenseType).isEmpty();
    }

    /**
     * 创建用户与许可证的关联
     *
     * @param userId    用户ID
     * @param licenseId 许可证ID
     * @return 创建结果
     */
    @Override
    public boolean createUserLicenseRelation(Integer userId, String licenseId) {
        LicenseInfo license = getLicenseByLicenseId(licenseId);
        if (license == null) {
            return false;
        }
        return createRelation(userId, license.getLicenseKey());
    }

    /**
     * 根据许可证密钥创建用户与许可证的关联
     *
     * @param userId     用户ID
     * @param licenseKey 许可证密钥
     * @return 创建结果
     */
    @Override
    public boolean createUserLicenseRelationByKey(Integer userId, String licenseKey) {
        LicenseInfo license = getLicenseByLicenseKey(licenseKey);
        if (license == null) {
            return false;
        }
        return createRelation(userId, licenseKey);
    }

    /**
     * 根据用户ID和许可证ID删除关联
     *
     * @param userId    用户ID
     * @param licenseId 许可证ID
     * @return 删除结果
     */
    @Override
    public boolean deleteUserLicenseRelation(Integer userId, String licenseId) {
        LicenseInfo license = getLicenseByLicenseId(licenseId);
        if (license == null) {
            return false;
        }
        return deleteRelation(userId, license.getLicenseKey());
    }

    /**
     * 根据用户ID删除所有关联
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @Override
    public boolean deleteAllRelationsByUserId(Integer userId) {
        return lambdaUpdate()
                .eq(LicenseUserRelation::getUserId, userId.longValue())
                .remove();
    }

    /**
     * 根据许可证ID删除所有关联
     *
     * @param licenseId 许可证ID
     * @return 删除结果
     */
    @Override
    public boolean deleteAllRelationsByLicenseId(String licenseId) {
        LicenseInfo license = getLicenseByLicenseId(licenseId);
        if (license == null) {
            return false;
        }
        return lambdaUpdate()
                .eq(LicenseUserRelation::getLicenseKey, license.getLicenseKey())
                .remove();
    }

    /**
     * 根据许可证密钥查询关联的用户
     *
     * @param licenseKey 许可证密钥
     * @return 用户关联列表
     */
    @Override
    public List<LicenseUserRelation> getUsersByLicenseKey(String licenseKey) {
        return lambdaQuery()
                .eq(LicenseUserRelation::getLicenseKey, licenseKey)
                .list();
    }

    /**
     * 根据许可证密钥和用户ID检查关联是否存在
     *
     * @param licenseKey 许可证密钥
     * @param userId     用户ID
     * @return 关联是否存在
     */
    @Override
    public boolean existsByLicenseKeyAndUserId(String licenseKey, Integer userId) {
        return lambdaQuery()
                .eq(LicenseUserRelation::getLicenseKey, licenseKey)
                .eq(LicenseUserRelation::getUserId, userId.longValue())
                .exists();
    }

    /**
     * 根据许可证密钥删除所有关联
     *
     * @param licenseKey 许可证密钥
     * @return 删除结果
     */
    @Override
    public boolean deleteAllRelationsByLicenseKey(String licenseKey) {
        return lambdaUpdate()
                .eq(LicenseUserRelation::getLicenseKey, licenseKey)
                .remove();
    }

    /**
     * 根据许可证密钥和用户ID删除关联
     *
     * @param licenseKey 许可证密钥
     * @param userId     用户ID
     * @return 删除结果
     */
    @Override
    public boolean deleteRelationByLicenseKeyAndUserId(String licenseKey, Integer userId) {
        return deleteRelation(userId, licenseKey);
    }

    /**
     * 根据许可证密钥查询许可证信息
     *
     * @param licenseKey 许可证密钥
     * @return 许可证信息
     */
    @Override
    public LicenseInfo getLicenseByLicenseKey(String licenseKey) {
        return licenseInfoService.getByLicenseKey(licenseKey);
    }

    /**
     * 根据许可证ID查询许可证信息
     *
     * @param licenseId 许可证ID
     * @return 许可证信息
     */
    @Override
    public LicenseInfo getLicenseByLicenseId(String licenseId) {
        return licenseInfoService.getByLicenseId(licenseId);
    }

    /**
     * 批量创建用户与许可证的关联
     *
     * @param userId      用户ID
     * @param licenseKeys 许可证密钥列表
     * @return 创建结果
     */
    @Override
    public boolean batchCreateUserLicenseRelations(Integer userId, List<String> licenseKeys) {
        List<LicenseUserRelation> relations = new ArrayList<>();

        for (String licenseKey : licenseKeys) {
            if (!existsByLicenseKeyAndUserId(licenseKey, userId)) {
                LicenseUserRelation relation = new LicenseUserRelation();
                relation.setLicenseKey(licenseKey);
                relation.setUserId(userId.longValue());
                relation.setStatus(Status.ACTIVE);
                relation.setAssignedBy(DEFAULT_ASSIGNED_BY);
                relation.setAssignedAt(LocalDateTime.now());
                relation.setExpiresAt(getLicenseByLicenseKey(licenseKey).getEndTime());
                relation.setLastUsedAt(LocalDateTime.now());
                relations.add(relation);
            }
        }

        return relations.isEmpty() || saveBatch(relations);
    }

    /**
     * 批量删除用户与许可证的关联
     *
     * @param userId      用户ID
     * @param licenseKeys 许可证密钥列表
     * @return 删除结果
     */
    @Override
    public boolean batchDeleteUserLicenseRelations(Integer userId, List<String> licenseKeys) {
        return licenseKeys.isEmpty() || lambdaUpdate()
                .eq(LicenseUserRelation::getUserId, userId.longValue())
                .in(LicenseUserRelation::getLicenseKey, licenseKeys)
                .remove();
    }

    /**
     * 创建关联关系
     *
     * @param userId     用户ID
     * @param licenseKey 许可证密钥
     * @return 创建结果
     */
    private boolean createRelation(Integer userId, String licenseKey) {
        if (existsByLicenseKeyAndUserId(licenseKey, userId)) {
            return true;
        }

        LicenseUserRelation relation = new LicenseUserRelation();
        relation.setLicenseKey(licenseKey);
        relation.setUserId(userId.longValue());
        relation.setStatus(Status.ACTIVE);
        relation.setAssignedBy(DEFAULT_ASSIGNED_BY);
        relation.setAssignedAt(LocalDateTime.now());
        relation.setExpiresAt(getLicenseByLicenseKey(licenseKey).getEndTime());
        relation.setLastUsedAt(LocalDateTime.now());

        return save(relation);
    }

    /**
     * 删除关联关系
     *
     * @param userId     用户ID
     * @param licenseKey 许可证密钥
     * @return 删除结果
     */
    private boolean deleteRelation(Integer userId, String licenseKey) {
        return lambdaUpdate()
                .eq(LicenseUserRelation::getUserId, userId.longValue())
                .eq(LicenseUserRelation::getLicenseKey, licenseKey)
                .remove();
    }
}
