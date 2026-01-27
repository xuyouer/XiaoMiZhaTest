package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserNames;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_names(用户名信息表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserNamesService extends IService<UserNames> {

    /**
     * 分页获取用户名信息列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户名信息列表
     */
    Page<UserNames> getNamesPage(Integer current, Integer pageSize);

    /**
     * 根据用户名ID获取用户名信息详情
     *
     * @param nameId 用户名ID
     * @return 用户名信息详情
     */
    UserNames getNameById(Integer nameId);

    /**
     * 新增用户名信息
     *
     * @param userNames 用户名信息
     * @return 是否新增成功
     */
    boolean addName(UserNames userNames);

    /**
     * 更新用户名信息
     *
     * @param nameId    用户名ID
     * @param userNames 用户名信息
     * @return 是否更新成功
     */
    boolean updateName(Integer nameId, UserNames userNames);

    /**
     * 删除用户名信息
     *
     * @param nameId 用户名ID
     * @return 是否删除成功
     */
    boolean deleteName(Integer nameId);

    /**
     * 创建默认用户名信息
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 是否创建成功
     */
    boolean createDefaultUserName(Integer userId, String username);

    /**
     * 根据用户ID获取用户名信息
     *
     * @param userId 用户ID
     * @return 用户名信息
     */
    UserNames getUserNameByUserId(Integer userId);

    /**
     * 批量添加用户名信息
     *
     * @param userNamesList 用户名信息列表
     * @return 是否添加成功
     */
    boolean batchAddNames(List<UserNames> userNamesList);

    /**
     * 批量更新用户名信息
     *
     * @param userNamesList 用户名信息列表
     * @return 是否更新成功
     */
    boolean batchUpdateNames(List<UserNames> userNamesList);

    /**
     * 批量删除用户名信息
     *
     * @param nameIds 用户名ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteNames(List<Integer> nameIds);

    /**
     * 验证用户名是否唯一
     *
     * @param username  用户名
     * @param excludeId 排除的用户名ID
     * @return 用户名是否唯一
     */
    boolean isUsernameUnique(String username, Integer excludeId);

}
