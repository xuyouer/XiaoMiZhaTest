package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserFeedback;
import ltd.xiaomizha.xuyou.user.service.UserFeedbackService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("feedback")
@Tag(name = "用户反馈管理", description = "用户反馈管理API")
public class UserFeedbackController {

    @Resource
    private UserFeedbackService userFeedbackService;

    @Operation(summary = "获取反馈列表", description = "分页获取反馈列表")
    @GetMapping("/list")
    public ResponseResult<?> getFeedbackPage(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserFeedback> feedbackPage = userFeedbackService.getFeedbacksPage(current, pageSize);
            return ResponseResultPage.ok(feedbackPage.getRecords(), feedbackPage.getCurrent(), feedbackPage.getSize(), feedbackPage.getTotal());
        } catch (Exception e) {
            log.error("获取反馈列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取反馈详情", description = "根据反馈ID获取反馈详情")
    @GetMapping("/{id}")
    public ResponseResult<?> getFeedbackById(@Parameter(description = "反馈ID") @PathVariable Long id) {
        try {
            UserFeedback feedback = userFeedbackService.getFeedbackById(id);
            return ResponseResult.success(feedback);
        } catch (Exception e) {
            log.error("获取反馈详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增反馈", description = "新增用户反馈")
    @PostMapping
    public ResponseResult<?> addFeedback(@RequestBody UserFeedback userFeedback) {
        try {
            boolean result = userFeedbackService.addFeedback(userFeedback);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增反馈失败");
            }
        } catch (Exception e) {
            log.error("新增反馈失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新反馈", description = "更新用户反馈信息")
    @PutMapping("/{id}")
    public ResponseResult<?> updateFeedback(@Parameter(description = "反馈ID") @PathVariable Long id,
                                            @RequestBody UserFeedback userFeedback) {
        try {
            boolean result = userFeedbackService.updateFeedback(id, userFeedback);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新反馈失败");
            }
        } catch (Exception e) {
            log.error("更新反馈失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除反馈", description = "根据反馈ID删除反馈")
    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteFeedback(@Parameter(description = "反馈ID") @PathVariable Long id) {
        try {
            boolean result = userFeedbackService.deleteFeedback(id);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除反馈失败");
            }
        } catch (Exception e) {
            log.error("删除反馈失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取反馈列表", description = "根据用户ID分页获取反馈列表")
    @GetMapping("/list/user/{userId}")
    public ResponseResult<?> getFeedbacksByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId,
                                                  @RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserFeedback> feedbackPage = userFeedbackService.getUserFeedbacksByUserId(userId, current, pageSize);
            return ResponseResultPage.ok(feedbackPage.getRecords(), feedbackPage.getCurrent(), feedbackPage.getSize(), feedbackPage.getTotal());
        } catch (Exception e) {
            log.error("根据用户ID获取反馈列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加反馈", description = "批量添加用户反馈")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddFeedback(@RequestBody List<UserFeedback> userFeedbackList) {
        try {
            boolean result = userFeedbackService.batchAddFeedbacks(userFeedbackList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加反馈失败");
            }
        } catch (Exception e) {
            log.error("批量添加反馈失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新反馈", description = "批量更新用户反馈信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateFeedback(@RequestBody List<UserFeedback> userFeedbackList) {
        try {
            boolean result = userFeedbackService.batchUpdateFeedbacks(userFeedbackList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新反馈失败");
            }
        } catch (Exception e) {
            log.error("批量更新反馈失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除反馈", description = "批量删除用户反馈")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteFeedback(@RequestBody List<Long> ids) {
        try {
            boolean result = userFeedbackService.batchDeleteFeedbacks(ids);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除反馈失败");
            }
        } catch (Exception e) {
            log.error("批量删除反馈失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}