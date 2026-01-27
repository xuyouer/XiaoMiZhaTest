package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * 分页获取角色关联列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页角色关联列表
     */
    Page<UserRoleRelations> getRelationsPage(Integer current, Integer pageSize);

    /**
     * 根据关联ID获取角色关联详情
     *
     * @param relationId 关联ID
     * @return 角色关联详情
     */
    UserRoleRelations getRelationById(Long relationId);

    /**
     * 新增角色关联
     *
     * @param userRoleRelations 角色关联信息
     * @return 是否新增成功
     */
    boolean addRelation(UserRoleRelations userRoleRelations);

    /**
     * 更新角色关联
     *
     * @param relationId        关联ID
     * @param userRoleRelations 角色关联信息
     * @return 是否更新成功
     */
    boolean updateRelation(Long relationId, UserRoleRelations userRoleRelations);

    /**
     * 删除角色关联
     *
     * @param relationId 关联ID
     * @return 是否删除成功
     */
    boolean deleteRelation(Long relationId);

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

    /**
     * 批量添加角色关联
     *
     * @param userRoleRelationsList 角色关联列表
     * @return 是否添加成功
     */
    boolean batchAddRelations(List<UserRoleRelations> userRoleRelationsList);

    /**
     * 批量更新角色关联
     *
     * @param userRoleRelationsList 角色关联列表
     * @return 是否更新成功
     */
    boolean batchUpdateRelations(List<UserRoleRelations> userRoleRelationsList);

    /**
     * 批量删除角色关联
     *
     * @param relationIds 关联ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteRelations(List<Long> relationIds);

}
