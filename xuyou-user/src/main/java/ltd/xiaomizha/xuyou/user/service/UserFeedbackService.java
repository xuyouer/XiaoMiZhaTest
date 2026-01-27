package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserFeedback;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_feedback(用户反馈表)】的数据库操作Service
 * @createDate 2026-01-23 21:12:33
 */
public interface UserFeedbackService extends IService<UserFeedback> {

    /**
     * 分页获取用户反馈列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户反馈列表
     */
    Page<UserFeedback> getFeedbacksPage(Integer current, Integer pageSize);

    /**
     * 根据反馈ID获取用户反馈详情
     *
     * @param feedbackId 反馈ID
     * @return 用户反馈详情
     */
    UserFeedback getFeedbackById(Long feedbackId);

    /**
     * 新增用户反馈
     *
     * @param userFeedback 用户反馈信息
     * @return 是否新增成功
     */
    boolean addFeedback(UserFeedback userFeedback);

    /**
     * 更新用户反馈
     *
     * @param feedbackId   反馈ID
     * @param userFeedback 用户反馈信息
     * @return 是否更新成功
     */
    boolean updateFeedback(Long feedbackId, UserFeedback userFeedback);

    /**
     * 删除用户反馈
     *
     * @param feedbackId 反馈ID
     * @return 是否删除成功
     */
    boolean deleteFeedback(Long feedbackId);

    /**
     * 根据用户ID获取用户反馈列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户反馈列表
     */
    Page<UserFeedback> getUserFeedbacksByUserId(Integer userId, Page<UserFeedback> page);

    /**
     * 根据用户ID获取用户反馈列表
     *
     * @param userId   用户ID
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 用户反馈列表
     */
    Page<UserFeedback> getUserFeedbacksByUserId(Integer userId, Integer current, Integer pageSize);

    /**
     * 批量添加用户反馈
     *
     * @param userFeedbackList 用户反馈列表
     * @return 是否添加成功
     */
    boolean batchAddFeedbacks(List<UserFeedback> userFeedbackList);

    /**
     * 批量更新用户反馈
     *
     * @param userFeedbackList 用户反馈列表
     * @return 是否更新成功
     */
    boolean batchUpdateFeedbacks(List<UserFeedback> userFeedbackList);

    /**
     * 批量删除用户反馈
     *
     * @param feedbackIds 反馈ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteFeedbacks(List<Long> feedbackIds);

}
