package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserPoints;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_points(用户积分表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserPointsService extends IService<UserPoints> {

    /**
     * 分页获取用户积分列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户积分列表
     */
    Page<UserPoints> getPointsPage(Integer current, Integer pageSize);

    /**
     * 根据积分ID获取用户积分详情
     *
     * @param pointsId 积分ID
     * @return 用户积分详情
     */
    UserPoints getPointsById(Integer pointsId);

    /**
     * 新增用户积分
     *
     * @param userPoints 用户积分信息
     * @return 是否新增成功
     */
    boolean addPoints(UserPoints userPoints);

    /**
     * 更新用户积分
     *
     * @param pointsId   积分ID
     * @param userPoints 用户积分信息
     * @return 是否更新成功
     */
    boolean updatePoints(Integer pointsId, UserPoints userPoints);

    /**
     * 删除用户积分
     *
     * @param pointsId 积分ID
     * @return 是否删除成功
     */
    boolean deletePoints(Integer pointsId);

    /**
     * 创建默认用户积分
     *
     * @param userId 用户ID
     * @return 是否创建成功
     */
    boolean createDefaultUserPoints(Integer userId);

    /**
     * 根据用户ID获取用户积分信息
     *
     * @param userId 用户ID
     * @return 用户积分信息
     */
    UserPoints getUserPointsByUserId(Integer userId);

    /**
     * 批量添加用户积分
     *
     * @param userPointsList 用户积分列表
     * @return 是否添加成功
     */
    boolean batchAddPoints(List<UserPoints> userPointsList);

    /**
     * 批量更新用户积分
     *
     * @param userPointsList 用户积分列表
     * @return 是否更新成功
     */
    boolean batchUpdatePoints(List<UserPoints> userPointsList);

    /**
     * 批量删除用户积分
     *
     * @param pointsIds 积分ID列表
     * @return 是否删除成功
     */
    boolean batchDeletePoints(List<Integer> pointsIds);

    /**
     * 增加用户积分
     *
     * @param userId 用户ID
     * @param points 增加的积分数量
     * @return 是否增加成功
     */
    boolean addUserPoints(Integer userId, Integer points);

    /**
     * 减少用户积分
     *
     * @param userId 用户ID
     * @param points 减少的积分数量
     * @return 是否减少成功
     */
    boolean reduceUserPoints(Integer userId, Integer points);

}
