package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserRoleRelations;
import ltd.xiaomizha.xuyou.user.entity.UserRoles;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_role_relations(用户角色关联表)】的数据库操作Service
 * @createDate 2026-01-24 11:33:32
 */
public interface UserRoleRelationsService extends IService<UserRoleRelations> {

    /**
     * 创建默认用户角色关系
     *
     * @param userId 用户ID
     * @return 是否创建成功
     */
    boolean createDefaultUserRoleRelation(Integer userId);

    /**
     * 激活用户角色关系
     *
     * @param userId 用户ID
     * @return 是否激活成功
     */
    boolean activateUserRoleRelation(Integer userId);

    /**
     * 根据用户ID获取用户角色列表
     *
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<UserRoles> getUserRolesByUserId(Integer userId);

}
