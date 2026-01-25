package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserVipLog;
import ltd.xiaomizha.xuyou.user.mapper.UserVipLogMapper;
import ltd.xiaomizha.xuyou.user.service.UserVipLogService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_vip_log(用户会员变更记录表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
public class UserVipLogServiceImpl extends ServiceImpl<UserVipLogMapper, UserVipLog>
        implements UserVipLogService {

    @Override
    public Page<UserVipLog> getUserVipLogsByUserId(Integer userId, Page<UserVipLog> page) {
        QueryWrapper<UserVipLog> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }

}
