package ltd.xiaomizha.xuyou.user.service;

import ltd.xiaomizha.xuyou.user.entity.UserNames;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author xiaom
 * @description 针对表【user_names(用户名信息表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserNamesService extends IService<UserNames> {

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

}
