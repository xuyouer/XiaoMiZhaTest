package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
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
public class UserResourcesServiceImpl extends ServiceImpl<UserResourcesMapper, UserResources>
        implements UserResourcesService {

    @Resource
    private UserRoleRelationsService userRoleRelationsService;

    @Resource
    private RoleResourceRelationsService roleResourceRelationsService;

    @Override
    public List<UserResources> getUserResourcesByUserId(Integer userId) {
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

}




