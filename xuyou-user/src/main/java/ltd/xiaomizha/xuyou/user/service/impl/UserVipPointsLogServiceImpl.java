package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserVipPointsLog;
import ltd.xiaomizha.xuyou.user.mapper.UserVipPointsLogMapper;
import ltd.xiaomizha.xuyou.user.service.UserVipPointsLogService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_vip_points_log(用户成长值获取记录表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
public class UserVipPointsLogServiceImpl extends ServiceImpl<UserVipPointsLogMapper, UserVipPointsLog>
        implements UserVipPointsLogService {

    @Override
    public Page<UserVipPointsLog> getUserVipPointsLogsByUserId(Integer userId, Page<UserVipPointsLog> page) {
        QueryWrapper<UserVipPointsLog> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }

}
