package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.user.entity.UserPoints;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserPointsMapper;
import ltd.xiaomizha.xuyou.user.service.UserPointsService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_points(用户积分表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
@Slf4j
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints>
        implements UserPointsService {

    @Resource
    private UsersService usersService;

    @Override
    public Page<UserPoints> getPointsPage(Integer current, Integer pageSize) {
        Page<UserPoints> page = new Page<>(current, pageSize);
        return this.page(page);
    }

    @Override
    public UserPoints getPointsById(Integer pointsId) {
        if (pointsId == null || pointsId <= 0) {
            throw new IllegalArgumentException("积分ID不能为空且必须大于0");
        }
        UserPoints points = this.getById(pointsId);
        if (points == null) {
            throw new RuntimeException("用户积分不存在");
        }
        return points;
    }

    @Override
    public boolean addPoints(UserPoints userPoints) {
        if (userPoints == null) {
            throw new IllegalArgumentException("用户积分信息不能为空");
        }
        // 验证用户是否存在
        Integer userId = userPoints.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证用户是否已存在积分记录
        UserPoints existingPoints = this.getUserPointsByUserId(userId);
        if (existingPoints != null) {
            throw new RuntimeException("用户积分记录已存在");
        }
        // 验证积分数量
        if (userPoints.getTotalPoints() < 0) {
            throw new IllegalArgumentException("总积分不能为负数");
        }
        if (userPoints.getAvailablePoints() < 0) {
            throw new IllegalArgumentException("可用积分不能为负数");
        }
        if (userPoints.getFrozenPoints() < 0) {
            throw new IllegalArgumentException("冻结积分不能为负数");
        }
        if (userPoints.getConsumedPoints() < 0) {
            throw new IllegalArgumentException("已消费积分不能为负数");
        }
        // 验证积分平衡
        int calculatedTotal = userPoints.getAvailablePoints() + userPoints.getFrozenPoints() + userPoints.getConsumedPoints();
        if (userPoints.getTotalPoints() != calculatedTotal) {
            throw new IllegalArgumentException("积分总额不等于可用积分、冻结积分和已消费积分之和");
        }
        return this.save(userPoints);
    }

    @Override
    public boolean updatePoints(Integer pointsId, UserPoints userPoints) {
        if (pointsId == null || pointsId <= 0) {
            throw new IllegalArgumentException("积分ID不能为空且必须大于0");
        }
        if (userPoints == null) {
            throw new IllegalArgumentException("用户积分信息不能为空");
        }
        // 验证积分是否存在
        if (!this.existsById(pointsId)) {
            throw new RuntimeException("用户积分不存在");
        }
        // 验证用户是否存在
        Integer userId = userPoints.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            // 验证用户是否已存在其他积分记录
            QueryWrapper<UserPoints> existingWrapper = new QueryWrapper<>();
            existingWrapper.eq("user_id", userId)
                    .ne("points_id", pointsId);
            if (this.count(existingWrapper) > 0) {
                throw new RuntimeException("该用户已存在其他积分记录");
            }
        }
        // 获取当前积分信息
        UserPoints currentPoints = this.getById(pointsId);
        // 验证积分数量
        int totalPoints = userPoints.getTotalPoints() != null ? userPoints.getTotalPoints() : currentPoints.getTotalPoints();
        int availablePoints = userPoints.getAvailablePoints() != null ? userPoints.getAvailablePoints() : currentPoints.getAvailablePoints();
        int frozenPoints = userPoints.getFrozenPoints() != null ? userPoints.getFrozenPoints() : currentPoints.getFrozenPoints();
        int consumedPoints = userPoints.getConsumedPoints() != null ? userPoints.getConsumedPoints() : currentPoints.getConsumedPoints();
        
        if (totalPoints < 0) {
            throw new IllegalArgumentException("总积分不能为负数");
        }
        if (availablePoints < 0) {
            throw new IllegalArgumentException("可用积分不能为负数");
        }
        if (frozenPoints < 0) {
            throw new IllegalArgumentException("冻结积分不能为负数");
        }
        if (consumedPoints < 0) {
            throw new IllegalArgumentException("已消费积分不能为负数");
        }
        // 验证积分平衡
        int calculatedTotal = availablePoints + frozenPoints + consumedPoints;
        if (totalPoints != calculatedTotal) {
            throw new IllegalArgumentException("积分总额不等于可用积分、冻结积分和已消费积分之和");
        }
        userPoints.setPointsId(pointsId);
        return this.updateById(userPoints);
    }

    @Override
    public boolean deletePoints(Integer pointsId) {
        if (pointsId == null || pointsId <= 0) {
            throw new IllegalArgumentException("积分ID不能为空且必须大于0");
        }
        // 验证积分是否存在
        if (!this.existsById(pointsId)) {
            throw new RuntimeException("用户积分不存在");
        }
        return this.removeById(pointsId);
    }

    @Override
    public boolean createDefaultUserPoints(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证是否已存在积分记录
        UserPoints existingPoints = this.getUserPointsByUserId(userId);
        if (existingPoints != null) {
            throw new RuntimeException("用户积分记录已存在");
        }
        // 添加user_points记录
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(userId);
        userPoints.setTotalPoints(UserConstants.DEFAULT_POINTS); // 默认总积分
        userPoints.setAvailablePoints(UserConstants.DEFAULT_POINTS); // 默认可用积分
        userPoints.setFrozenPoints(UserConstants.DEFAULT_POINTS); // 默认冻结积分
        userPoints.setConsumedPoints(UserConstants.DEFAULT_POINTS); // 默认已消费积分

        return this.save(userPoints);
    }

    @Override
    public UserPoints getUserPointsByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserPoints> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean batchAddPoints(List<UserPoints> userPointsList) {
        if (userPointsList == null || userPointsList.isEmpty()) {
            throw new IllegalArgumentException("用户积分列表不能为空");
        }
        for (UserPoints points : userPointsList) {
            if (points == null) {
                throw new IllegalArgumentException("用户积分列表中包含空记录");
            }
            // 验证用户是否存在
            Integer userId = points.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
            // 验证积分数量
            if (points.getTotalPoints() < 0) {
                throw new IllegalArgumentException("总积分不能为负数: 用户ID " + userId);
            }
            if (points.getAvailablePoints() < 0) {
                throw new IllegalArgumentException("可用积分不能为负数: 用户ID " + userId);
            }
            if (points.getFrozenPoints() < 0) {
                throw new IllegalArgumentException("冻结积分不能为负数: 用户ID " + userId);
            }
            if (points.getConsumedPoints() < 0) {
                throw new IllegalArgumentException("已消费积分不能为负数: 用户ID " + userId);
            }
            // 验证积分平衡
            if (points.getTotalPoints() != points.getAvailablePoints() + points.getFrozenPoints() + points.getConsumedPoints()) {
                throw new IllegalArgumentException("积分总额不等于可用积分、冻结积分和已消费积分之和: 用户ID " + userId);
            }
        }
        return this.saveBatch(userPointsList);
    }

    @Override
    public boolean batchUpdatePoints(List<UserPoints> userPointsList) {
        if (userPointsList == null || userPointsList.isEmpty()) {
            throw new IllegalArgumentException("用户积分列表不能为空");
        }
        for (UserPoints points : userPointsList) {
            if (points == null || points.getPointsId() == null || points.getPointsId() <= 0) {
                throw new IllegalArgumentException("用户积分列表中包含无效记录");
            }
            // 验证积分是否存在
            if (!this.existsById(points.getPointsId())) {
                throw new RuntimeException("用户积分不存在: " + points.getPointsId());
            }
            // 验证用户是否存在
            Integer userId = points.getUserId();
            if (userId != null) {
                if (userId <= 0) {
                    throw new IllegalArgumentException("用户ID必须大于0");
                }
                Users user = usersService.getById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在: " + userId);
                }
            }
            // 验证积分数量
            if (points.getTotalPoints() != null && points.getTotalPoints() < 0) {
                throw new IllegalArgumentException("总积分不能为负数");
            }
            if (points.getAvailablePoints() != null && points.getAvailablePoints() < 0) {
                throw new IllegalArgumentException("可用积分不能为负数");
            }
            if (points.getFrozenPoints() != null && points.getFrozenPoints() < 0) {
                throw new IllegalArgumentException("冻结积分不能为负数");
            }
            if (points.getConsumedPoints() != null && points.getConsumedPoints() < 0) {
                throw new IllegalArgumentException("已消费积分不能为负数");
            }
        }
        return this.updateBatchById(userPointsList);
    }

    @Override
    public boolean batchDeletePoints(List<Integer> pointsIds) {
        if (pointsIds == null || pointsIds.isEmpty()) {
            throw new IllegalArgumentException("积分ID列表不能为空");
        }
        // 验证列表中的积分是否都存在
        for (Integer pointsId : pointsIds) {
            if (pointsId == null || pointsId <= 0) {
                throw new IllegalArgumentException("积分ID列表中包含无效ID");
            }
            if (!this.existsById(pointsId)) {
                throw new RuntimeException("用户积分不存在: " + pointsId);
            }
        }
        return this.removeByIds(pointsIds);
    }

    @Override
    public boolean addUserPoints(Integer userId, Integer points) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("增加的积分数量必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 获取当前积分信息
        UserPoints userPoints = this.getUserPointsByUserId(userId);
        if (userPoints == null) {
            throw new RuntimeException("用户积分记录不存在");
        }
        // 更新积分
        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        userPoints.setAvailablePoints(userPoints.getAvailablePoints() + points);
        return this.updateById(userPoints);
    }

    @Override
    public boolean reduceUserPoints(Integer userId, Integer points) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("减少的积分数量必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 获取当前积分信息
        UserPoints userPoints = this.getUserPointsByUserId(userId);
        if (userPoints == null) {
            throw new RuntimeException("用户积分记录不存在");
        }
        // 验证可用积分是否足够
        if (userPoints.getAvailablePoints() < points) {
            throw new RuntimeException("可用积分不足");
        }
        // 更新积分
        userPoints.setTotalPoints(userPoints.getTotalPoints() - points);
        userPoints.setAvailablePoints(userPoints.getAvailablePoints() - points);
        userPoints.setConsumedPoints(userPoints.getConsumedPoints() + points);
        return this.updateById(userPoints);
    }

    private boolean existsById(Integer pointsId) {
        if (pointsId == null) {
            return false;
        }
        return this.getById(pointsId) != null;
    }

}