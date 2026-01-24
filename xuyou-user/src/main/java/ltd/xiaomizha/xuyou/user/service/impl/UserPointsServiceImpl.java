package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.user.entity.UserPoints;
import ltd.xiaomizha.xuyou.user.mapper.UserPointsMapper;
import ltd.xiaomizha.xuyou.user.service.UserPointsService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_points(用户积分表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints>
        implements UserPointsService {

    @Override
    public boolean createDefaultUserPoints(Integer userId) {
        // 添加user_points记录
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(userId);
        userPoints.setTotalPoints(UserConstants.DEFAULT_POINTS); // 默认总积分
        userPoints.setAvailablePoints(UserConstants.DEFAULT_POINTS); // 默认可用积分
        userPoints.setFrozenPoints(UserConstants.DEFAULT_POINTS); // 默认冻结积分
        userPoints.setConsumedPoints(UserConstants.DEFAULT_POINTS); // 默认已消费积分

        return this.save(userPoints);
    }

}




