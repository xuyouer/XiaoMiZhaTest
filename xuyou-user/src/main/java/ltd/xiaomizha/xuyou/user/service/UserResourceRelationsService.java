package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserResourceRelations;
import ltd.xiaomizha.xuyou.user.entity.UserResources;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_resource_relations(用户资源关联表(直接授权))】的数据库操作Service
 * @createDate 2026-01-24 12:37:48
 */
public interface UserResourceRelationsService extends IService<UserResourceRelations> {

    /**
     * 分页获取用户资源关联列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户资源关联列表
     */
    Page<UserResourceRelations> getRelationsPage(Integer current, Integer pageSize);

    /**
     * 根据关联ID获取用户资源关联详情
     *
     * @param relationId 关联ID
     * @return 用户资源关联详情
     */
    UserResourceRelations getRelationById(Long relationId);

    /**
     * 新增用户资源关联
     *
     * @param userResourceRelations 用户资源关联信息
     * @return 是否新增成功
     */
    boolean addRelation(UserResourceRelations userResourceRelations);

    /**
     * 更新用户资源关联
     *
     * @param relationId            关联ID
     * @param userResourceRelations 用户资源关联信息
     * @return 是否更新成功
     */
    boolean updateRelation(Long relationId, UserResourceRelations userResourceRelations);

    /**
     * 删除用户资源关联
     *
     * @param relationId 关联ID
     * @return 是否删除成功
     */
    boolean deleteRelation(Long relationId);

    /**
     * 根据用户ID获取用户资源列表
     *
     * @param userId 用户ID
     * @return 用户资源列表
     */
    List<UserResources> getUserResourcesByUserId(Integer userId);

    /**
     * 根据用户ID更新用户资源列表
     *
     * @param userId      用户ID
     * @param resourceIds 资源ID列表
     * @return 是否更新成功
     */
    boolean updateUserResourcesByUserId(Integer userId, List<Integer> resourceIds);

    /**
     * 根据用户ID获取用户资源关联列表
     *
     * @param userId 用户ID
     * @return 用户资源关联列表
     */
    List<UserResourceRelations> getRelationsByUserId(Integer userId);

    /**
     * 批量添加用户资源关联
     *
     * @param userResourceRelationsList 用户资源关联列表
     * @return 是否添加成功
     */
    boolean batchAddRelations(List<UserResourceRelations> userResourceRelationsList);

    /**
     * 批量更新用户资源关联
     *
     * @param userResourceRelationsList 用户资源关联列表
     * @return 是否更新成功
     */
    boolean batchUpdateRelations(List<UserResourceRelations> userResourceRelationsList);

    /**
     * 批量删除用户资源关联
     *
     * @param relationIds 关联ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteRelations(List<Long> relationIds);

}
