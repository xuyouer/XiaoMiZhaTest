package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.entity.UserFeedback;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserFeedbackMapper;
import ltd.xiaomizha.xuyou.user.service.UserFeedbackService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_feedback(用户反馈表)】的数据库操作Service实现
 * @createDate 2026-01-23 21:12:33
 */
@Service
@Slf4j
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback>
        implements UserFeedbackService {

    @Resource
    private UsersService usersService;

    @Override
    public Page<UserFeedback> getFeedbacksPage(Integer current, Integer pageSize) {
        Page<UserFeedback> page = new Page<>(current, pageSize);
        QueryWrapper<UserFeedback> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }

    @Override
    public UserFeedback getFeedbackById(Long feedbackId) {
        if (feedbackId == null || feedbackId <= 0) {
            throw new IllegalArgumentException("反馈ID不能为空且必须大于0");
        }
        UserFeedback feedback = this.getById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("用户反馈不存在");
        }
        return feedback;
    }

    @Override
    public boolean addFeedback(UserFeedback userFeedback) {
        if (userFeedback == null) {
            throw new IllegalArgumentException("用户反馈信息不能为空");
        }
        // 验证用户是否存在
        Integer userId = userFeedback.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证反馈内容
        if (userFeedback.getContent() == null || userFeedback.getContent().isEmpty()) {
            throw new IllegalArgumentException("反馈内容不能为空");
        }
        // 验证反馈类型
        if (userFeedback.getType() == null) {
            throw new IllegalArgumentException("反馈类型不能为空");
        }
        // 验证反馈类型范围(1-系统问题, 2-功能建议, 3-BUG反馈, 4-其他)
        if (userFeedback.getType() < 1 || userFeedback.getType() > 4) {
            throw new IllegalArgumentException("反馈类型必须在1-4之间");
        }
        // 验证联系方式
        if (userFeedback.getContactInfo() != null) {
            if (userFeedback.getContactInfo().length() > 100) {
                throw new IllegalArgumentException("联系方式长度不能超过100个字符");
            }
        }
        // 验证状态
        if (userFeedback.getStatus() != null) {
            if (userFeedback.getStatus() < 1 || userFeedback.getStatus() > 4) {
                throw new IllegalArgumentException("状态必须在1-4之间(1-待处理, 2-已受理, 3-已解决, 4-已关闭)");
            }
        }
        return this.save(userFeedback);
    }

    @Override
    public boolean updateFeedback(Long feedbackId, UserFeedback userFeedback) {
        if (feedbackId == null || feedbackId <= 0) {
            throw new IllegalArgumentException("反馈ID不能为空且必须大于0");
        }
        if (userFeedback == null) {
            throw new IllegalArgumentException("用户反馈信息不能为空");
        }
        // 验证反馈是否存在
        if (this.getById(feedbackId) == null) {
            throw new RuntimeException("用户反馈不存在");
        }
        // 验证用户是否存在
        Integer userId = userFeedback.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
        }
        // 验证反馈内容
        if (userFeedback.getContent() != null && userFeedback.getContent().isEmpty()) {
            throw new IllegalArgumentException("反馈内容不能为空");
        }
        // 验证反馈类型范围(1-系统问题, 2-功能建议, 3-BUG反馈, 4-其他)
        if (userFeedback.getType() != null) {
            if (userFeedback.getType() < 1 || userFeedback.getType() > 4) {
                throw new IllegalArgumentException("反馈类型必须在1-4之间");
            }
        }
        // 验证联系方式
        if (userFeedback.getContactInfo() != null) {
            if (userFeedback.getContactInfo().length() > 100) {
                throw new IllegalArgumentException("联系方式长度不能超过100个字符");
            }
        }
        // 验证状态
        if (userFeedback.getStatus() != null) {
            if (userFeedback.getStatus() < 1 || userFeedback.getStatus() > 4) {
                throw new IllegalArgumentException("状态必须在1-4之间(1-待处理, 2-已受理, 3-已解决, 4-已关闭)");
            }
        }
        userFeedback.setId(feedbackId);
        return this.updateById(userFeedback);
    }

    @Override
    public boolean deleteFeedback(Long feedbackId) {
        if (feedbackId == null || feedbackId <= 0) {
            throw new IllegalArgumentException("反馈ID不能为空且必须大于0");
        }
        // 验证反馈是否存在
        if (this.getById(feedbackId) == null) {
            throw new RuntimeException("用户反馈不存在");
        }
        return this.removeById(feedbackId);
    }

    @Override
    public Page<UserFeedback> getUserFeedbacksByUserId(Integer userId, Page<UserFeedback> page) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserFeedback> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }

    @Override
    public Page<UserFeedback> getUserFeedbacksByUserId(Integer userId, Integer current, Integer pageSize) {
        Page<UserFeedback> page = new Page<>(current, pageSize);
        return getUserFeedbacksByUserId(userId, page);
    }

    @Override
    public boolean batchAddFeedbacks(List<UserFeedback> userFeedbackList) {
        if (userFeedbackList == null || userFeedbackList.isEmpty()) {
            throw new IllegalArgumentException("用户反馈列表不能为空");
        }
        for (UserFeedback feedback : userFeedbackList) {
            if (feedback == null) {
                throw new IllegalArgumentException("用户反馈列表中包含空记录");
            }
            // 验证用户是否存在
            Integer userId = feedback.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
            // 验证反馈内容
            if (feedback.getContent() == null || feedback.getContent().isEmpty()) {
                throw new IllegalArgumentException("反馈内容不能为空: 用户ID " + userId);
            }
            // 验证反馈类型
            if (feedback.getType() == null) {
                throw new IllegalArgumentException("反馈类型不能为空: 用户ID " + userId);
            }
        }
        return this.saveBatch(userFeedbackList);
    }

    @Override
    public boolean batchUpdateFeedbacks(List<UserFeedback> userFeedbackList) {
        if (userFeedbackList == null || userFeedbackList.isEmpty()) {
            throw new IllegalArgumentException("用户反馈列表不能为空");
        }
        for (UserFeedback feedback : userFeedbackList) {
            if (feedback == null || feedback.getId() == null || feedback.getId() <= 0) {
                throw new IllegalArgumentException("用户反馈列表中包含无效记录");
            }
            // 验证反馈是否存在
            if (this.getById(feedback.getId()) == null) {
                throw new RuntimeException("用户反馈不存在: " + feedback.getId());
            }
            // 验证用户是否存在
            Integer userId = feedback.getUserId();
            if (userId != null) {
                if (userId <= 0) {
                    throw new IllegalArgumentException("用户ID必须大于0");
                }
                Users user = usersService.getById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在: " + userId);
                }
            }
            // 验证反馈内容
            if (feedback.getContent() != null && feedback.getContent().isEmpty()) {
                throw new IllegalArgumentException("反馈内容不能为空");
            }
        }
        return this.updateBatchById(userFeedbackList);
    }

    @Override
    public boolean batchDeleteFeedbacks(List<Long> feedbackIds) {
        if (feedbackIds == null || feedbackIds.isEmpty()) {
            throw new IllegalArgumentException("反馈ID列表不能为空");
        }
        // 验证列表中的反馈是否都存在
        for (Long feedbackId : feedbackIds) {
            if (feedbackId == null || feedbackId <= 0) {
                throw new IllegalArgumentException("反馈ID列表中包含无效ID");
            }
            if (this.getById(feedbackId) == null) {
                throw new RuntimeException("用户反馈不存在: " + feedbackId);
            }
        }
        return this.removeByIds(feedbackIds);
    }

}