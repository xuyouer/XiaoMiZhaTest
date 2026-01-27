package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserProfiles;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_profiles(用户表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserProfilesService extends IService<UserProfiles> {

    /**
     * 分页获取用户资料列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户资料列表
     */
    Page<UserProfiles> getProfilesPage(Integer current, Integer pageSize);

    /**
     * 根据资料ID获取用户资料详情
     *
     * @param profileId 资料ID
     * @return 用户资料详情
     */
    UserProfiles getProfileById(Integer profileId);

    /**
     * 新增用户资料
     *
     * @param userProfiles 用户资料信息
     * @return 是否新增成功
     */
    boolean addProfile(UserProfiles userProfiles);

    /**
     * 更新用户资料
     *
     * @param profileId    资料ID
     * @param userProfiles 用户资料信息
     * @return 是否更新成功
     */
    boolean updateProfile(Integer profileId, UserProfiles userProfiles);

    /**
     * 删除用户资料
     *
     * @param profileId 资料ID
     * @return 是否删除成功
     */
    boolean deleteProfile(Integer profileId);

    /**
     * 创建默认用户资料
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 是否创建成功
     */
    boolean createDefaultUserProfile(Integer userId, String username);

    /**
     * 根据用户ID获取用户资料
     *
     * @param userId 用户ID
     * @return 用户资料
     */
    UserProfiles getUserProfileByUserId(Integer userId);

    /**
     * 批量添加用户资料
     *
     * @param userProfilesList 用户资料列表
     * @return 是否添加成功
     */
    boolean batchAddProfiles(List<UserProfiles> userProfilesList);

    /**
     * 批量更新用户资料
     *
     * @param userProfilesList 用户资料列表
     * @return 是否更新成功
     */
    boolean batchUpdateProfiles(List<UserProfiles> userProfilesList);

    /**
     * 批量删除用户资料
     *
     * @param profileIds 资料ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteProfiles(List<Integer> profileIds);

}
