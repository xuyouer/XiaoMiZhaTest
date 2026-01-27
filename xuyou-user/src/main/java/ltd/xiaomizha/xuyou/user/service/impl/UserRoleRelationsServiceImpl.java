package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.user.entity.UserRoleRelations;
import ltd.xiaomizha.xuyou.user.entity.UserRoles;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserRoleRelationsMapper;
import ltd.xiaomizha.xuyou.user.service.UserRoleRelationsService;
import ltd.xiaomizha.xuyou.user.service.UserRolesService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_role_relations(用户角色关联表)】的数据库操作Service实现
 * @createDate 2026-01-24 11:33:32
 */
@Service
@Slf4j
public class UserRoleRelationsServiceImpl extends ServiceImpl<UserRoleRelationsMapper, UserRoleRelations>
        implements UserRoleRelationsService {

    @Resource
    private UserRolesService userRolesService;

    @Resource
    private UsersService usersService;

    @Override
    public Page<UserRoleRelations> getRelationsPage(Integer current, Integer pageSize) {
        Page<UserRoleRelations> page = new Page<>(current, pageSize);
        return this.page(page);
    }

    @Override
    public UserRoleRelations getRelationById(Long relationId) {
        if (relationId == null || relationId <= 0) {
            throw new IllegalArgumentException("关联ID不能为空且必须大于0");
        }
        UserRoleRelations relation = this.getById(relationId);
        if (relation == null) {
            throw new RuntimeException("角色关联不存在");
        }
        return relation;
    }

    @Override
    public boolean addRelation(UserRoleRelations userRoleRelations) {
        if (userRoleRelations == null) {
            throw new IllegalArgumentException("角色关联信息不能为空");
        }
        // 验证用户是否存在
        Integer userId = userRoleRelations.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证角色是否存在
        Integer roleId = userRoleRelations.getRoleId();
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("角色ID不能为空且必须大于0");
        }
        UserRoles role = userRolesService.getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        // 验证关联状态
        if (userRoleRelations.getStatus() == null) {
            throw new IllegalArgumentException("关联状态不能为空");
        }
        // 验证是否为主角色
        if (userRoleRelations.getIsPrimary() == null) {
            userRoleRelations.setIsPrimary(0); // 默认不是主角色
        } else if (userRoleRelations.getIsPrimary() != 0 && userRoleRelations.getIsPrimary() != 1) {
            throw new IllegalArgumentException("是否为主角色只能是0或1");
        }
        // 验证用户是否已拥有该角色
        QueryWrapper<UserRoleRelations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("role_id", roleId);
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("用户已拥有该角色");
        }
        return this.save(userRoleRelations);
    }

    @Override
    public boolean updateRelation(Long relationId, UserRoleRelations userRoleRelations) {
        if (relationId == null || relationId <= 0) {
            throw new IllegalArgumentException("关联ID不能为空且必须大于0");
        }
        if (userRoleRelations == null) {
            throw new IllegalArgumentException("角色关联信息不能为空");
        }
        // 验证角色关联是否存在
        if (!this.existsById(relationId)) {
            throw new RuntimeException("角色关联不存在");
        }
        // 验证用户是否存在(如果更新了用户ID)
        Integer userId = userRoleRelations.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
        }
        // 验证角色是否存在(如果更新了角色ID)
        Integer roleId = userRoleRelations.getRoleId();
        if (roleId != null) {
            if (roleId <= 0) {
                throw new IllegalArgumentException("角色ID必须大于0");
            }
            UserRoles role = userRolesService.getById(roleId);
            if (role == null) {
                throw new RuntimeException("角色不存在");
            }
            // 验证用户是否已拥有该角色(如果更新了角色ID)
            if (userId != null) {
                QueryWrapper<UserRoleRelations> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId)
                        .eq("role_id", roleId)
                        .ne("relation_id", relationId);
                if (this.count(queryWrapper) > 0) {
                    throw new RuntimeException("用户已拥有该角色");
                }
            }
        }
        // 验证是否为主角色
        if (userRoleRelations.getIsPrimary() != null && userRoleRelations.getIsPrimary() != 0 && userRoleRelations.getIsPrimary() != 1) {
            throw new IllegalArgumentException("是否为主角色只能是0或1");
        }
        userRoleRelations.setRelationId(relationId);
        return this.updateById(userRoleRelations);
    }

    @Override
    public boolean deleteRelation(Long relationId) {
        if (relationId == null || relationId <= 0) {
            throw new IllegalArgumentException("关联ID不能为空且必须大于0");
        }
        // 验证角色关联是否存在
        if (!this.existsById(relationId)) {
            throw new RuntimeException("角色关联不存在");
        }
        return this.removeById(relationId);
    }

    @Override
    public boolean createDefaultUserRoleRelation(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证角色是否存在
        UserRoles defaultRole = userRolesService.getById(UserConstants.DEFAULT_ROLE_ID);
        if (defaultRole == null) {
            throw new RuntimeException("默认角色不存在");
        }
        // 检查是否已存在默认角色关联
        QueryWrapper<UserRoleRelations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("role_id", UserConstants.DEFAULT_ROLE_ID);
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("用户已拥有默认角色");
        }
        // 添加user_role_relations记录
        UserRoleRelations userRoleRelations = new UserRoleRelations();
        userRoleRelations.setUserId(userId);
        userRoleRelations.setRoleId(UserConstants.DEFAULT_ROLE_ID); // 默认普通用户
        userRoleRelations.setAssignedBy(UserConstants.DEFAULT_ASSIGNED_BY); // 默认分配人: 管理员
        userRoleRelations.setExpiresAt(null); // 默认身份不过期
        userRoleRelations.setIsPrimary(UserConstants.DEFAULT_IS_PRIMARY); // 默认主角色
        userRoleRelations.setStatus(Status.INACTIVE); // 默认未激活, 需登录一次进行激活ACTIVE

        return this.save(userRoleRelations);
    }

    @Override
    public boolean activateUserRoleRelation(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 激活user_role_relations
        QueryWrapper<UserRoleRelations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserRoleRelations userRoleRelations = this.getOne(queryWrapper);
        if (userRoleRelations == null) {
            throw new RuntimeException("用户角色关联不存在");
        }
        userRoleRelations.setStatus(Status.ACTIVE);

        return this.updateById(userRoleRelations);
    }

    @Override
    public List<UserRoles> getUserRolesByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 获取用户角色列表
        List<UserRoles> userRoles = userRolesService.list(new QueryWrapper<UserRoles>().inSql("role_id", "SELECT role_id FROM user_role_relations WHERE user_id = " + userId));

        return userRoles;
    }

    @Override
    public boolean batchAddRelations(List<UserRoleRelations> userRoleRelationsList) {
        if (userRoleRelationsList == null || userRoleRelationsList.isEmpty()) {
            throw new IllegalArgumentException("角色关联列表不能为空");
        }
        for (UserRoleRelations relation : userRoleRelationsList) {
            if (relation == null) {
                throw new IllegalArgumentException("角色关联列表中包含空关联");
            }
            // 验证用户是否存在
            Integer userId = relation.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
            // 验证角色是否存在
            Integer roleId = relation.getRoleId();
            if (roleId == null || roleId <= 0) {
                throw new IllegalArgumentException("角色ID不能为空且必须大于0");
            }
            UserRoles role = userRolesService.getById(roleId);
            if (role == null) {
                throw new RuntimeException("角色不存在: " + roleId);
            }
            // 验证用户是否已拥有该角色
            QueryWrapper<UserRoleRelations> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("role_id", roleId);
            if (this.count(queryWrapper) > 0) {
                throw new RuntimeException("用户" + userId + "已拥有角色" + roleId);
            }
        }
        return this.saveBatch(userRoleRelationsList);
    }

    @Override
    public boolean batchUpdateRelations(List<UserRoleRelations> userRoleRelationsList) {
        if (userRoleRelationsList == null || userRoleRelationsList.isEmpty()) {
            throw new IllegalArgumentException("角色关联列表不能为空");
        }
        for (UserRoleRelations relation : userRoleRelationsList) {
            if (relation == null || relation.getRelationId() == null || relation.getRelationId() <= 0) {
                throw new IllegalArgumentException("角色关联列表中包含无效关联");
            }
            // 验证角色关联是否存在
            if (!this.existsById(relation.getRelationId())) {
                throw new RuntimeException("角色关联不存在: " + relation.getRelationId());
            }
            // 验证用户是否存在(如果更新了用户ID)
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
            // 验证角色是否存在(如果更新了角色ID)
            Integer roleId = relation.getRoleId();
            if (roleId != null) {
                if (roleId <= 0) {
                    throw new IllegalArgumentException("角色ID必须大于0");
                }
                UserRoles role = userRolesService.getById(roleId);
                if (role == null) {
                    throw new RuntimeException("角色不存在: " + roleId);
                }
            }
        }
        return this.updateBatchById(userRoleRelationsList);
    }

    @Override
    public boolean batchDeleteRelations(List<Long> relationIds) {
        if (relationIds == null || relationIds.isEmpty()) {
            throw new IllegalArgumentException("关联ID列表不能为空");
        }
        // 验证列表中的关联是否都存在
        for (Long relationId : relationIds) {
            if (relationId == null || relationId <= 0) {
                throw new IllegalArgumentException("关联ID列表中包含无效ID");
            }
            if (!this.existsById(relationId)) {
                throw new RuntimeException("角色关联不存在: " + relationId);
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