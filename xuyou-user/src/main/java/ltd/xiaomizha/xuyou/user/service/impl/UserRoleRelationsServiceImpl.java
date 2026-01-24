package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.user.entity.UserRoleRelations;
import ltd.xiaomizha.xuyou.user.mapper.UserRoleRelationsMapper;
import ltd.xiaomizha.xuyou.user.service.UserRoleRelationsService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_role_relations(用户角色关联表)】的数据库操作Service实现
 * @createDate 2026-01-24 11:33:32
 */
@Service
@Slf4j
public class UserRoleRelationsServiceImpl extends ServiceImpl<UserRoleRelationsMapper, UserRoleRelations>
        implements UserRoleRelationsService {

    @Override
    public boolean createDefaultUserRoleRelation(Integer userId) {
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
        // 激活user_role_relations
        // UserRoleRelations userRoleRelations = new UserRoleRelations();
        // userRoleRelations.setUserId(userId);
        QueryWrapper<UserRoleRelations> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserRoleRelations userRoleRelations = this.getOne(queryWrapper);
        userRoleRelations.setStatus(Status.ACTIVE);

        return this.updateById(userRoleRelations);
    }

}




