package ltd.xiaomizha.xuyou.user.service;

import ltd.xiaomizha.xuyou.user.entity.UserProfiles;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author xiaom
* @description 针对表【user_profiles(用户表)】的数据库操作Service
* @createDate 2026-01-21 19:16:15
*/
public interface UserProfilesService extends IService<UserProfiles> {

    /**
     * 创建默认用户资料
     * @param userId 用户ID
     * @param username 用户名
     * @return 是否创建成功
     */
    boolean createDefaultUserProfile(Integer userId, String username);

    /**
     * 根据用户ID获取用户资料
     * @param userId 用户ID
     * @return 用户资料
     */
    UserProfiles getUserProfileByUserId(Integer userId);

}
