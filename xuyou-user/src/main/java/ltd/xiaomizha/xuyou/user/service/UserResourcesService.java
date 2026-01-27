package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserResources;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_resources(用户资源表)】的数据库操作Service
 * @createDate 2026-01-24 12:37:48
 */
public interface UserResourcesService extends IService<UserResources> {

    /**
     * 分页获取资源列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页资源列表
     */
    Page<UserResources> getResourcesPage(Integer current, Integer pageSize);

    /**
     * 根据资源ID获取资源详情
     *
     * @param resourceId 资源ID
     * @return 资源详情
     */
    UserResources getResourceById(Integer resourceId);

    /**
     * 新增资源
     *
     * @param userResources 资源信息
     * @return 是否新增成功
     */
    boolean addResource(UserResources userResources);

    /**
     * 更新资源
     *
     * @param resourceId    资源ID
     * @param userResources 资源信息
     * @return 是否更新成功
     */
    boolean updateResource(Integer resourceId, UserResources userResources);

    /**
     * 删除资源
     *
     * @param resourceId 资源ID
     * @return 是否删除成功
     */
    boolean deleteResource(Integer resourceId);

    /**
     * 根据用户ID获取用户资源列表
     *
     * @param userId 用户ID
     * @return 用户资源列表
     */
    List<UserResources> getUserResourcesByUserId(Integer userId);

    /**
     * 批量添加资源
     *
     * @param userResourcesList 资源列表
     * @return 是否添加成功
     */
    boolean batchAddResources(List<UserResources> userResourcesList);

    /**
     * 批量更新资源
     *
     * @param userResourcesList 资源列表
     * @return 是否更新成功
     */
    boolean batchUpdateResources(List<UserResources> userResourcesList);

    /**
     * 批量删除资源
     *
     * @param resourceIds 资源ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteResources(List<Integer> resourceIds);

}
