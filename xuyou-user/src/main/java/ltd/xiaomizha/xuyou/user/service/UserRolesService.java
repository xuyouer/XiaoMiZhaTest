package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserRoles;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_roles(用户角色表)】的数据库操作Service
 * @createDate 2026-01-25 22:39:25
 */
public interface UserRolesService extends IService<UserRoles> {

    /**
     * 分页获取角色列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页角色列表
     */
    Page<UserRoles> getRolesPage(Integer current, Integer pageSize);

    /**
     * 根据角色ID获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    UserRoles getRoleById(Integer roleId);

    /**
     * 新增角色
     *
     * @param userRoles 角色信息
     * @return 是否新增成功
     */
    boolean addRole(UserRoles userRoles);

    /**
     * 更新角色
     *
     * @param roleId    角色ID
     * @param userRoles 角色信息
     * @return 是否更新成功
     */
    boolean updateRole(Integer roleId, UserRoles userRoles);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Integer roleId);

    /**
     * 批量添加角色
     *
     * @param userRolesList 角色列表
     * @return 是否添加成功
     */
    boolean batchAddRoles(List<UserRoles> userRolesList);

    /**
     * 批量更新角色
     *
     * @param userRolesList 角色列表
     * @return 是否更新成功
     */
    boolean batchUpdateRoles(List<UserRoles> userRolesList);

    /**
     * 批量删除角色
     *
     * @param roleIds 角色ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteRoles(List<Integer> roleIds);

}
