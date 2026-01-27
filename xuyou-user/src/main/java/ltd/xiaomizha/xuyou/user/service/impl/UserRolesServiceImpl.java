package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.entity.UserRoles;
import ltd.xiaomizha.xuyou.user.service.UserRolesService;
import ltd.xiaomizha.xuyou.user.mapper.UserRolesMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_roles(用户角色表)】的数据库操作Service实现
 * @createDate 2026-01-25 22:39:25
 */
@Service
@Slf4j
public class UserRolesServiceImpl extends ServiceImpl<UserRolesMapper, UserRoles>
        implements UserRolesService {

    @Override
    public Page<UserRoles> getRolesPage(Integer current, Integer pageSize) {
        Page<UserRoles> page = new Page<>(current, pageSize);
        return this.page(page);
    }

    @Override
    public UserRoles getRoleById(Integer roleId) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("角色ID不能为空且必须大于0");
        }
        UserRoles role = this.getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return role;
    }

    @Override
    public boolean addRole(UserRoles userRoles) {
        if (userRoles == null) {
            throw new IllegalArgumentException("角色信息不能为空");
        }
        // 验证角色名称
        if (userRoles.getRoleName() == null || userRoles.getRoleName().isEmpty()) {
            throw new IllegalArgumentException("角色名称不能为空");
        }
        // 验证角色代码
        if (userRoles.getRoleCode() == null || userRoles.getRoleCode().isEmpty()) {
            throw new IllegalArgumentException("角色代码不能为空");
        }
        // 验证角色名称是否已存在
        QueryWrapper<UserRoles> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq("role_name", userRoles.getRoleName());
        if (this.count(nameWrapper) > 0) {
            throw new RuntimeException("角色名称已存在");
        }
        // 验证角色代码是否已存在
        QueryWrapper<UserRoles> codeWrapper = new QueryWrapper<>();
        codeWrapper.eq("role_code", userRoles.getRoleCode());
        if (this.count(codeWrapper) > 0) {
            throw new RuntimeException("角色代码已存在");
        }
        // 验证是否为系统角色
        if (userRoles.getIsSystemRole() != null && userRoles.getIsSystemRole() != 0 && userRoles.getIsSystemRole() != 1) {
            throw new IllegalArgumentException("是否为系统内置角色必须是0或1(0-否,1-是)");
        }
        // 验证是否默认角色
        if (userRoles.getIsDefault() != null && userRoles.getIsDefault() != 0 && userRoles.getIsDefault() != 1) {
            throw new IllegalArgumentException("是否默认角色必须是0或1(0-否,1-是)");
        }
        // 验证排序序号
        if (userRoles.getSortOrder() != null && userRoles.getSortOrder() < 0) {
            throw new IllegalArgumentException("排序序号不能为负数");
        }
        // 验证状态
        if (userRoles.getStatus() != null && userRoles.getStatus() != 0 && userRoles.getStatus() != 1) {
            throw new IllegalArgumentException("状态必须是0或1(0-禁用,1-启用)");
        }
        return this.save(userRoles);
    }

    @Override
    public boolean updateRole(Integer roleId, UserRoles userRoles) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("角色ID不能为空且必须大于0");
        }
        if (userRoles == null) {
            throw new IllegalArgumentException("角色信息不能为空");
        }
        // 验证角色是否存在
        if (!this.existsById(roleId)) {
            throw new RuntimeException("角色不存在");
        }
        // 验证角色名称
        if (userRoles.getRoleName() != null) {
            if (userRoles.getRoleName().isEmpty()) {
                throw new IllegalArgumentException("角色名称不能为空");
            }
            // 验证角色名称是否已被其他角色使用
            QueryWrapper<UserRoles> nameWrapper = new QueryWrapper<>();
            nameWrapper.eq("role_name", userRoles.getRoleName())
                    .ne("role_id", roleId);
            if (this.count(nameWrapper) > 0) {
                throw new RuntimeException("角色名称已被其他角色使用");
            }
        }
        // 验证角色代码
        if (userRoles.getRoleCode() != null) {
            if (userRoles.getRoleCode().isEmpty()) {
                throw new IllegalArgumentException("角色代码不能为空");
            }
            // 验证角色代码是否已被其他角色使用
            QueryWrapper<UserRoles> codeWrapper = new QueryWrapper<>();
            codeWrapper.eq("role_code", userRoles.getRoleCode())
                    .ne("role_id", roleId);
            if (this.count(codeWrapper) > 0) {
                throw new RuntimeException("角色代码已被其他角色使用");
            }
        }
        // 验证是否为系统角色
        if (userRoles.getIsSystemRole() != null && userRoles.getIsSystemRole() != 0 && userRoles.getIsSystemRole() != 1) {
            throw new IllegalArgumentException("是否为系统内置角色必须是0或1(0-否,1-是)");
        }
        // 验证是否默认角色
        if (userRoles.getIsDefault() != null && userRoles.getIsDefault() != 0 && userRoles.getIsDefault() != 1) {
            throw new IllegalArgumentException("是否默认角色必须是0或1(0-否,1-是)");
        }
        // 验证排序序号
        if (userRoles.getSortOrder() != null && userRoles.getSortOrder() < 0) {
            throw new IllegalArgumentException("排序序号不能为负数");
        }
        // 验证状态
        if (userRoles.getStatus() != null && userRoles.getStatus() != 0 && userRoles.getStatus() != 1) {
            throw new IllegalArgumentException("状态必须是0或1(0-禁用,1-启用)");
        }
        userRoles.setRoleId(roleId);
        return this.updateById(userRoles);
    }

    @Override
    public boolean deleteRole(Integer roleId) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("角色ID不能为空且必须大于0");
        }
        // 验证角色是否存在
        if (!this.existsById(roleId)) {
            throw new RuntimeException("角色不存在");
        }
        return this.removeById(roleId);
    }

    @Override
    public boolean batchAddRoles(List<UserRoles> userRolesList) {
        if (userRolesList == null || userRolesList.isEmpty()) {
            throw new IllegalArgumentException("角色列表不能为空");
        }
        // 验证列表中的角色名称是否已存在
        for (UserRoles role : userRolesList) {
            if (role == null) {
                throw new IllegalArgumentException("角色列表中包含空角色");
            }
            QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_name", role.getRoleName());
            if (this.count(queryWrapper) > 0) {
                throw new RuntimeException("角色名称已存在: " + role.getRoleName());
            }
        }
        return this.saveBatch(userRolesList);
    }

    @Override
    public boolean batchUpdateRoles(List<UserRoles> userRolesList) {
        if (userRolesList == null || userRolesList.isEmpty()) {
            throw new IllegalArgumentException("角色列表不能为空");
        }
        for (UserRoles role : userRolesList) {
            if (role == null || role.getRoleId() == null || role.getRoleId() <= 0) {
                throw new IllegalArgumentException("角色列表中包含无效角色");
            }
            // 验证角色是否存在
            if (!this.existsById(role.getRoleId())) {
                throw new RuntimeException("角色不存在: " + role.getRoleId());
            }
            // 验证角色名称是否已被其他角色使用
            QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_name", role.getRoleName())
                    .ne("role_id", role.getRoleId());
            if (this.count(queryWrapper) > 0) {
                throw new RuntimeException("角色名称已被其他角色使用: " + role.getRoleName());
            }
        }
        return this.updateBatchById(userRolesList);
    }

    @Override
    public boolean batchDeleteRoles(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("角色ID列表不能为空");
        }
        // 验证列表中的角色是否都存在
        for (Integer roleId : roleIds) {
            if (roleId == null || roleId <= 0) {
                throw new IllegalArgumentException("角色ID列表中包含无效ID");
            }
            if (!this.existsById(roleId)) {
                throw new RuntimeException("角色不存在: " + roleId);
            }
        }
        return this.removeByIds(roleIds);
    }

    private boolean existsById(Integer roleId) {
        if (roleId == null) {
            return false;
        }
        return this.getById(roleId) != null;
    }

}