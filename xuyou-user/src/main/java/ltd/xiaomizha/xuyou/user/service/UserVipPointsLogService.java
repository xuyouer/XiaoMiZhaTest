package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserVipPointsLog;

/**
 * @author xiaom
 * @description 针对表【user_vip_points_log(用户成长值获取记录表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserVipPointsLogService extends IService<UserVipPointsLog> {

    /**
     * 根据用户ID获取用户VIP积分日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户VIP积分日志列表
     */
    Page<UserVipPointsLog> getUserVipPointsLogsByUserId(Integer userId, Page<UserVipPointsLog> page);

}
