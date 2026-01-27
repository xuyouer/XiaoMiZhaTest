package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.entity.UserResources;
import ltd.xiaomizha.xuyou.user.entity.UserRoles;
import ltd.xiaomizha.xuyou.user.mapper.UserResourcesMapper;
import ltd.xiaomizha.xuyou.user.service.RoleResourceRelationsService;
import ltd.xiaomizha.xuyou.user.service.UserResourcesService;
import ltd.xiaomizha.xuyou.user.service.UserRoleRelationsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaom
 * @description 针对表【user_resources(用户资源表)】的数据库操作Service实现
 * @createDate 2026-01-24 12:37:48
 */
@Service
@Slf4j
public class UserResourcesServiceImpl extends ServiceImpl<UserResourcesMapper, UserResources>
        implements UserResourcesService {

    @Resource
    private UserRoleRelationsService userRoleRelationsService;

    @Resource
    private RoleResourceRelationsService roleResourceRelationsService;

    @Override
    public Page<UserResources> getResourcesPage(Integer current, Integer pageSize) {
        Page<UserResources> page = new Page<>(current, pageSize);
        return this.page(page);
    }

    @Override
    public UserResources getResourceById(Integer resourceId) {
        if (resourceId == null || resourceId <= 0) {
            throw new IllegalArgumentException("资源ID不能为空且必须大于0");
        }
        UserResources resource = this.getById(resourceId);
        if (resource == null) {
            throw new RuntimeException("资源不存在");
        }
        return resource;
    }

    @Override
    public boolean addResource(UserResources userResources) {
        if (userResources == null) {
            throw new IllegalArgumentException("资源信息不能为空");
        }
        // 验证资源名称
        if (userResources.getResourceName() == null || userResources.getResourceName().isEmpty()) {
            throw new IllegalArgumentException("资源名称不能为空");
        }
        // 验证资源代码唯一性
        if (userResources.getResourceCode() == null || userResources.getResourceCode().isEmpty()) {
            throw new IllegalArgumentException("资源代码不能为空");
        }
        QueryWrapper<UserResources> codeWrapper = new QueryWrapper<>();
        codeWrapper.eq("resource_code", userResources.getResourceCode());
        if (this.count(codeWrapper) > 0) {
            throw new RuntimeException("资源代码已存在");
        }
        // 验证资源路径唯一性
        if (userResources.getResourcePath() != null && !userResources.getResourcePath().isEmpty()) {
            QueryWrapper<UserResources> pathWrapper = new QueryWrapper<>();
            pathWrapper.eq("resource_path", userResources.getResourcePath());
            if (this.count(pathWrapper) > 0) {
                throw new RuntimeException("资源路径已存在");
            }
        }
        // 验证资源分类
        if (userResources.getResourceCategory() == null) {
            throw new IllegalArgumentException("资源分类不能为空");
        }
        // 验证父资源是否存在
        Integer parentId = userResources.getParentId();
        if (parentId != null) {
            if (parentId <= 0) {
                throw new IllegalArgumentException("父资源ID必须大于0");
            }
            UserResources parentResource = this.getById(parentId);
            if (parentResource == null) {
                throw new RuntimeException("父资源不存在");
            }
        }
        // 验证资源层级
        if (userResources.getLevel() <= 0) {
            throw new IllegalArgumentException("资源层级必须大于0");
        }
        // 验证状态
        if (userResources.getStatus() != null && userResources.getStatus() != 0 && userResources.getStatus() != 1) {
            throw new IllegalArgumentException("状态必须是0或1(0-禁用,1-启用)");
        }
        // 验证可见性
        if (userResources.getVisible() != null && userResources.getVisible() != 0 && userResources.getVisible() != 1) {
            throw new IllegalArgumentException("可见性必须是0或1(0-隐藏,1-可见)");
        }
        // 验证是否为系统资源
        if (userResources.getIsSystem() != null && userResources.getIsSystem() != 0 && userResources.getIsSystem() != 1) {
            throw new IllegalArgumentException("是否为系统资源必须是0或1(0-否,1-是)");
        }
        // 验证是否需要认证
        if (userResources.getRequiresAuth() != null && userResources.getRequiresAuth() != 0 && userResources.getRequiresAuth() != 1) {
            throw new IllegalArgumentException("是否需要认证必须是0或1(0-否,1-是)");
        }
        return this.save(userResources);
    }

    @Override
    public boolean updateResource(Integer resourceId, UserResources userResources) {
        if (resourceId == null || resourceId <= 0) {
            throw new IllegalArgumentException("资源ID不能为空且必须大于0");
        }
        if (userResources == null) {
            throw new IllegalArgumentException("资源信息不能为空");
        }
        // 验证资源是否存在
        if (!this.existsById(resourceId)) {
            throw new RuntimeException("资源不存在");
        }
        // 验证资源名称
        if (userResources.getResourceName() != null && userResources.getResourceName().isEmpty()) {
            throw new IllegalArgumentException("资源名称不能为空");
        }
        // 验证资源代码唯一性
        if (userResources.getResourceCode() != null) {
            if (userResources.getResourceCode().isEmpty()) {
                throw new IllegalArgumentException("资源代码不能为空");
            }
            QueryWrapper<UserResources> codeWrapper = new QueryWrapper<>();
            codeWrapper.eq("resource_code", userResources.getResourceCode())
                    .ne("resource_id", resourceId);
            if (this.count(codeWrapper) > 0) {
                throw new RuntimeException("资源代码已被其他资源使用");
            }
        }
        // 验证资源路径唯一性
        if (userResources.getResourcePath() != null) {
            if (!userResources.getResourcePath().isEmpty()) {
                QueryWrapper<UserResources> pathWrapper = new QueryWrapper<>();
                pathWrapper.eq("resource_path", userResources.getResourcePath())
                        .ne("resource_id", resourceId);
                if (this.count(pathWrapper) > 0) {
                    throw new RuntimeException("资源路径已被其他资源使用");
                }
            }
        }
        // 验证父资源是否存在
        Integer parentId = userResources.getParentId();
        if (parentId != null) {
            if (parentId <= 0) {
                throw new IllegalArgumentException("父资源ID必须大于0");
            }
            if (!parentId.equals(resourceId)) { // 不能将自身设为父资源
                UserResources parentResource = this.getById(parentId);
                if (parentResource == null) {
                    throw new RuntimeException("父资源不存在");
                }
            } else {
                throw new IllegalArgumentException("不能将资源自身设为父资源");
            }
        }
        // 验证资源层级
        if (userResources.getLevel() != null && userResources.getLevel() <= 0) {
            throw new IllegalArgumentException("资源层级必须大于0");
        }
        // 验证状态
        if (userResources.getStatus() != null && userResources.getStatus() != 0 && userResources.getStatus() != 1) {
            throw new IllegalArgumentException("状态必须是0或1(0-禁用,1-启用)");
        }
        // 验证可见性
        if (userResources.getVisible() != null && userResources.getVisible() != 0 && userResources.getVisible() != 1) {
            throw new IllegalArgumentException("可见性必须是0或1(0-隐藏,1-可见)");
        }
        // 验证是否为系统资源
        if (userResources.getIsSystem() != null && userResources.getIsSystem() != 0 && userResources.getIsSystem() != 1) {
            throw new IllegalArgumentException("是否为系统资源必须是0或1(0-否,1-是)");
        }
        // 验证是否需要认证
        if (userResources.getRequiresAuth() != null && userResources.getRequiresAuth() != 0 && userResources.getRequiresAuth() != 1) {
            throw new IllegalArgumentException("是否需要认证必须是0或1(0-否,1-是)");
        }
        userResources.setResourceId(resourceId);
        return this.updateById(userResources);
    }

    @Override
    public boolean deleteResource(Integer resourceId) {
        if (resourceId == null || resourceId <= 0) {
            throw new IllegalArgumentException("资源ID不能为空且必须大于0");
        }
        // 验证资源是否存在
        if (!this.existsById(resourceId)) {
            throw new RuntimeException("资源不存在");
        }
        return this.removeById(resourceId);
    }

    @Override
    public List<UserResources> getUserResourcesByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 获取用户角色列表
        List<UserRoles> userRoles = userRoleRelationsService.getUserRolesByUserId(userId);

        if (userRoles.isEmpty()) {
            return List.of();
        }

        // 获取角色ID列表
        List<Integer> roleIds = userRoles.stream()
                .map(UserRoles::getRoleId)
                .collect(Collectors.toList());

        // 获取角色资源关联关系
        QueryWrapper<ltd.xiaomizha.xuyou.user.entity.RoleResourceRelations> resourceRelationWrapper = new QueryWrapper<>();
        resourceRelationWrapper.in("role_id", roleIds);
        List<ltd.xiaomizha.xuyou.user.entity.RoleResourceRelations> roleResourceRelations = roleResourceRelationsService.list(resourceRelationWrapper);

        if (roleResourceRelations.isEmpty()) {
            return List.of();
        }

        // 获取资源ID列表
        List<Integer> resourceIds = roleResourceRelations.stream()
                .map(ltd.xiaomizha.xuyou.user.entity.RoleResourceRelations::getResourceId)
                .collect(Collectors.toList());

        // 根据资源ID获取资源信息
        QueryWrapper<UserResources> resourcesWrapper = new QueryWrapper<>();
        resourcesWrapper.in("resource_id", resourceIds);
        return this.list(resourcesWrapper);
    }

    @Override
    public boolean batchAddResources(List<UserResources> userResourcesList) {
        if (userResourcesList == null || userResourcesList.isEmpty()) {
            throw new IllegalArgumentException("资源列表不能为空");
        }
        // 验证列表中的资源名称是否已存在
        for (UserResources resource : userResourcesList) {
            if (resource == null) {
                throw new IllegalArgumentException("资源列表中包含空资源");
            }
            QueryWrapper<UserResources> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("resource_name", resource.getResourceName());
            if (this.count(queryWrapper) > 0) {
                throw new RuntimeException("资源名称已存在: " + resource.getResourceName());
            }
        }
        return this.saveBatch(userResourcesList);
    }

    @Override
    public boolean batchUpdateResources(List<UserResources> userResourcesList) {
        if (userResourcesList == null || userResourcesList.isEmpty()) {
            throw new IllegalArgumentException("资源列表不能为空");
        }
        for (UserResources resource : userResourcesList) {
            if (resource == null || resource.getResourceId() == null || resource.getResourceId() <= 0) {
                throw new IllegalArgumentException("资源列表中包含无效资源");
            }
            // 验证资源是否存在
            if (!this.existsById(resource.getResourceId())) {
                throw new RuntimeException("资源不存在: " + resource.getResourceId());
            }
            // 验证资源名称是否已被其他资源使用
            QueryWrapper<UserResources> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("resource_name", resource.getResourceName())
                    .ne("resource_id", resource.getResourceId());
            if (this.count(queryWrapper) > 0) {
                throw new RuntimeException("资源名称已被其他资源使用: " + resource.getResourceName());
            }
        }
        return this.updateBatchById(userResourcesList);
    }

    @Override
    public boolean batchDeleteResources(List<Integer> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            throw new IllegalArgumentException("资源ID列表不能为空");
        }
        // 验证列表中的资源是否都存在
        for (Integer resourceId : resourceIds) {
            if (resourceId == null || resourceId <= 0) {
                throw new IllegalArgumentException("资源ID列表中包含无效ID");
            }
            if (!this.existsById(resourceId)) {
                throw new RuntimeException("资源不存在: " + resourceId);
            }
        }
        return this.removeByIds(resourceIds);
    }

    private boolean existsById(Integer resourceId) {
        if (resourceId == null) {
            return false;
        }
        return this.getById(resourceId) != null;
    }

}
