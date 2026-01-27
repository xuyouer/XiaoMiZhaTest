package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserFeedback;
import ltd.xiaomizha.xuyou.user.mapper.UserFeedbackMapper;
import ltd.xiaomizha.xuyou.user.service.UserFeedbackService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_feedback(用户反馈表)】的数据库操作Service实现
 * @createDate 2026-01-23 21:12:33
 */
@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback>
        implements UserFeedbackService {

    @Override
    public Page<UserFeedback> getUserFeedbacksByUserId(Integer userId, Page<UserFeedback> page) {
        QueryWrapper<UserFeedback> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }

}
