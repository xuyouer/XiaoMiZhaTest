package ltd.xiaomizha.xuyou.user.service;

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
     * 根据用户ID获取用户资源列表
     *
     * @param userId 用户ID
     * @return 用户资源列表
     */
    List<UserResources> getUserResourcesByUserId(Integer userId);

}
