package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.GrantType;
import ltd.xiaomizha.xuyou.common.enums.entity.PermissionType;
import ltd.xiaomizha.xuyou.user.entity.UserResourceRelations;
import ltd.xiaomizha.xuyou.user.entity.UserResources;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserResourceRelationsMapper;
import ltd.xiaomizha.xuyou.user.service.UserResourceRelationsService;
import ltd.xiaomizha.xuyou.user.service.UserResourcesService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaom
 * @description 针对表【user_resource_relations(用户资源关联表(直接授权))】的数据库操作Service实现
 * @createDate 2026-01-24 12:37:48
 */
@Service
@Slf4j
public class UserResourceRelationsServiceImpl extends ServiceImpl<UserResourceRelationsMapper, UserResourceRelations>
        implements UserResourceRelationsService {

    @Resource
    private UsersService usersService;

    @Resource
    private UserResourcesService userResourcesService;

    /**
     * 分页获取用户资源关联列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户资源关联列表
     */
    @Override
    public Page<UserResourceRelations> getRelationsPage(Integer current, Integer pageSize) {
        Page<UserResourceRelations> page = new Page<>(current, pageSize);
        QueryWrapper<UserResourceRelations> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }

    /**
     * 根据关联ID获取用户资源关联详情
     *
     * @param relationId 关联ID
     * @return 用户资源关联详情
     */
    @Override
    public UserResourceRelations getRelationById(Long relationId) {
        if (relationId == null || relationId <= 0) {
            throw new IllegalArgumentException("关联ID不能为空且必须大于0");
        }
        UserResourceRelations relation = this.getById(relationId);
        if (relation == null) {
            throw new RuntimeException("用户资源关联不存在");
        }
        return relation;
    }

    /**
     * 新增用户资源关联
     *
     * @param userResourceRelations 用户资源关联信息
     * @return 是否新增成功
     */
    @Override
    public boolean addRelation(UserResourceRelations userResourceRelations) {
        if (userResourceRelations == null) {
            throw new IllegalArgumentException("用户资源关联信息不能为空");
        }
        Integer userId = userResourceRelations.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Integer resourceId = userResourceRelations.getResourceId();
        if (resourceId == null || resourceId <= 0) {
            throw new IllegalArgumentException("资源ID不能为空且必须大于0");
        }
        UserResources resource = userResourcesService.getById(resourceId);
        if (resource == null) {
            throw new RuntimeException("资源不存在");
        }
        QueryWrapper<UserResourceRelations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("resource_id", resourceId);
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("用户已拥有该资源");
        }
        if (userResourceRelations.getIsActive() == null) {
            userResourceRelations.setIsActive(1);
        }
        if (userResourceRelations.getGrantType() == null) {
            userResourceRelations.setGrantType(GrantType.DIRECT);
        }
        if (userResourceRelations.getPermissionType() == null) {
            userResourceRelations.setPermissionType(PermissionType.READ);
        }
        return this.save(userResourceRelations);
    }

    /**
     * 更新用户资源关联
     *
     * @param relationId            关联ID
     * @param userResourceRelations 用户资源关联信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateRelation(Long relationId, UserResourceRelations userResourceRelations) {
        if (relationId == null || relationId <= 0) {
            throw new IllegalArgumentException("关联ID不能为空且必须大于0");
        }
        if (userResourceRelations == null) {
            throw new IllegalArgumentException("用户资源关联信息不能为空");
        }
        if (!this.existsById(relationId)) {
            throw new RuntimeException("用户资源关联不存在");
        }
        Integer userId = userResourceRelations.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
        }
        Integer resourceId = userResourceRelations.getResourceId();
        if (resourceId != null) {
            if (resourceId <= 0) {
                throw new IllegalArgumentException("资源ID必须大于0");
            }
            UserResources resource = userResourcesService.getById(resourceId);
            if (resource == null) {
                throw new RuntimeException("资源不存在");
            }
            if (userId != null) {
                QueryWrapper<UserResourceRelations> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId)
                        .eq("resource_id", resourceId)
                        .ne("relation_id", relationId);
                if (this.count(queryWrapper) > 0) {
                    throw new RuntimeException("用户已拥有该资源");
                }
            }
        }
        userResourceRelations.setRelationId(relationId);
        return this.updateById(userResourceRelations);
    }

    /**
     * 删除用户资源关联
     *
     * @param relationId 关联ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteRelation(Long relationId) {
        if (relationId == null || relationId <= 0) {
            throw new IllegalArgumentException("关联ID不能为空且必须大于0");
        }
        if (!this.existsById(relationId)) {
            throw new RuntimeException("用户资源关联不存在");
        }
        return this.removeById(relationId);
    }

    /**
     * 根据用户ID获取用户资源列表
     *
     * @param userId 用户ID
     * @return 用户资源列表
     */
    @Override
    public List<UserResources> getUserResourcesByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserResourceRelations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("is_active", 1);
        List<UserResourceRelations> relations = this.list(queryWrapper);
        if (relations == null || relations.isEmpty()) {
            return List.of();
        }
        List<Integer> resourceIds = relations.stream()
                .map(UserResourceRelations::getResourceId)
                .filter(Objects::nonNull)
                .toList();
        if (resourceIds.isEmpty()) {
            return List.of();
        }
        return userResourcesService.listByIds(resourceIds);
    }

    /**
     * 根据用户ID更新用户资源列表
     *
     * @param userId      用户ID
     * @param resourceIds 资源ID列表
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserResourcesByUserId(Integer userId, List<Integer> resourceIds) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserResourceRelations> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("user_id", userId);
        this.remove(deleteWrapper);
        if (resourceIds == null || resourceIds.isEmpty()) {
            return true;
        }
        Set<Integer> uniqueResourceIds = new HashSet<>(resourceIds);
        List<UserResources> existingResources = userResourcesService.listByIds(uniqueResourceIds);
        if (existingResources.size() != uniqueResourceIds.size()) {
            Set<Integer> existingIds = existingResources.stream()
                    .map(UserResources::getResourceId)
                    .collect(Collectors.toSet());
            List<Integer> invalidIds = uniqueResourceIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .toList();
            throw new RuntimeException("以下资源ID不存在: " + invalidIds);
        }
        List<UserResourceRelations> newRelations = new ArrayList<>();
        for (Integer resourceId : uniqueResourceIds) {
            UserResourceRelations relation = new UserResourceRelations();
            relation.setUserId(userId);
            relation.setResourceId(resourceId);
            relation.setIsActive(1);
            relation.setGrantType(GrantType.DIRECT);
            relation.setPermissionType(PermissionType.READ);
            newRelations.add(relation);
        }
        return this.saveBatch(newRelations);
    }

    /**
     * 根据用户ID获取用户资源关联列表
     *
     * @param userId 用户ID
     * @return 用户资源关联列表
     */
    @Override
    public List<UserResourceRelations> getRelationsByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserResourceRelations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    /**
     * 批量添加用户资源关联
     *
     * @param userResourceRelationsList 用户资源关联列表
     * @return 是否添加成功
     */
    @Override
    public boolean batchAddRelations(List<UserResourceRelations> userResourceRelationsList) {
        if (userResourceRelationsList == null || userResourceRelationsList.isEmpty()) {
            throw new IllegalArgumentException("用户资源关联列表不能为空");
        }
        for (UserResourceRelations relation : userResourceRelationsList) {
            if (relation == null) {
                throw new IllegalArgumentException("用户资源关联列表中包含空关联");
            }
            Integer userId = relation.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
            Integer resourceId = relation.getResourceId();
            if (resourceId == null || resourceId <= 0) {
                throw new IllegalArgumentException("资源ID不能为空且必须大于0");
            }
            UserResources resource = userResourcesService.getById(resourceId);
            if (resource == null) {
                throw new RuntimeException("资源不存在: " + resourceId);
            }
            QueryWrapper<UserResourceRelations> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("resource_id", resourceId);
            if (this.count(queryWrapper) > 0) {
                throw new RuntimeException("用户" + userId + "已拥有资源" + resourceId);
            }
        }
        return this.saveBatch(userResourceRelationsList);
    }

    /**
     * 批量更新用户资源关联
     *
     * @param userResourceRelationsList 用户资源关联列表
     * @return 是否更新成功
     */
    @Override
    public boolean batchUpdateRelations(List<UserResourceRelations> userResourceRelationsList) {
        if (userResourceRelationsList == null || userResourceRelationsList.isEmpty()) {
            throw new IllegalArgumentException("用户资源关联列表不能为空");
        }
        for (UserResourceRelations relation : userResourceRelationsList) {
            if (relation == null || relation.getRelationId() == null || relation.getRelationId() <= 0) {
                throw new IllegalArgumentException("用户资源关联列表中包含无效关联");
            }
            if (!this.existsById(relation.getRelationId())) {
                throw new RuntimeException("用户资源关联不存在: " + relation.getRelationId());
            }
            Integer userId = relation.getUserId();
            if (userId != null) {
                if (userId <= 0) {
                    throw new IllegalArgumentException("用户ID必须大于0");
                }
                Users user = usersService.getById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在: " + userId);
                }
            }
            Integer resourceId = relation.getResourceId();
            if (resourceId != null) {
                if (resourceId <= 0) {
                    throw new IllegalArgumentException("资源ID必须大于0");
                }
                UserResources resource = userResourcesService.getById(resourceId);
                if (resource == null) {
                    throw new RuntimeException("资源不存在: " + resourceId);
                }
            }
        }
        return this.updateBatchById(userResourceRelationsList);
    }

    /**
     * 批量删除用户资源关联
     *
     * @param relationIds 关联ID列表
     * @return 是否删除成功
     */
    @Override
    public boolean batchDeleteRelations(List<Long> relationIds) {
        if (relationIds == null || relationIds.isEmpty()) {
            throw new IllegalArgumentException("关联ID列表不能为空");
        }
        for (Long relationId : relationIds) {
            if (relationId == null || relationId <= 0) {
                throw new IllegalArgumentException("关联ID列表中包含无效ID");
            }
            if (!this.existsById(relationId)) {
                throw new RuntimeException("用户资源关联不存在: " + relationId);
            }
        }
        return this.removeByIds(relationIds);
    }

    private boolean existsById(Long relationId) {
        if (relationId == null) {
            return false;
        }
        return this.getById(relationId) != null;
    }
    
}
