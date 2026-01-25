package ltd.xiaomizha.xuyou.user.service;

import ltd.xiaomizha.xuyou.user.entity.UserPoints;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author xiaom
* @description 针对表【user_points(用户积分表)】的数据库操作Service
* @createDate 2026-01-21 19:16:15
*/
public interface UserPointsService extends IService<UserPoints> {

    /**
     * 创建默认用户积分
     * @param userId 用户ID
     * @return 是否创建成功
     */
    boolean createDefaultUserPoints(Integer userId);

    /**
     * 根据用户ID获取用户积分信息
     * @param userId 用户ID
     * @return 用户积分信息
     */
    UserPoints getUserPointsByUserId(Integer userId);

}
