package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserFeedback;

/**
 * @author xiaom
 * @description 针对表【user_feedback(用户反馈表)】的数据库操作Service
 * @createDate 2026-01-23 21:12:33
 */
public interface UserFeedbackService extends IService<UserFeedback> {

    /**
     * 根据用户ID获取用户反馈列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户反馈列表
     */
    Page<UserFeedback> getUserFeedbacksByUserId(Integer userId, Page<UserFeedback> page);

}
