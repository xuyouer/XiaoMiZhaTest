package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserNameHistory;
import ltd.xiaomizha.xuyou.user.service.UserNameHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("name-history")
@Tag(name = "用户名变更历史管理", description = "用户名变更历史管理API")
public class UserNameHistoryController {

    @Resource
    private UserNameHistoryService userNameHistoryService;

    @Operation(summary = "获取用户名变更历史列表", description = "分页获取用户名变更历史列表")
    @GetMapping("/list")
    public ResponseResult<?> getHistoriesPage(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserNameHistory> historiesPage = userNameHistoryService.getHistoriesPage(current, pageSize);
            return ResponseResultPage.ok(historiesPage.getRecords(), historiesPage.getCurrent(), historiesPage.getSize(), historiesPage.getTotal());
        } catch (Exception e) {
            log.error("获取用户名变更历史列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取用户名变更历史详情", description = "根据历史ID获取用户名变更历史详情")
    @GetMapping("/{historyId}")
    public ResponseResult<?> getHistoryById(@Parameter(description = "历史ID") @PathVariable Integer historyId) {
        try {
            UserNameHistory history = userNameHistoryService.getHistoryById(historyId);
            return ResponseResult.success(history);
        } catch (Exception e) {
            log.error("获取用户名变更历史详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增用户名变更历史", description = "新增用户名变更历史")
    @PostMapping
    public ResponseResult<?> addHistory(@RequestBody UserNameHistory userNameHistory) {
        try {
            boolean result = userNameHistoryService.addHistory(userNameHistory);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增用户名变更历史失败");
            }
        } catch (Exception e) {
            log.error("新增用户名变更历史失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新用户名变更历史", description = "更新用户名变更历史信息")
    @PutMapping("/{historyId}")
    public ResponseResult<?> updateHistory(@Parameter(description = "历史ID") @PathVariable Integer historyId,
                                          @RequestBody UserNameHistory userNameHistory) {
        try {
            boolean result = userNameHistoryService.updateHistory(historyId, userNameHistory);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新用户名变更历史失败");
            }
        } catch (Exception e) {
            log.error("更新用户名变更历史失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除用户名变更历史", description = "根据历史ID删除用户名变更历史")
    @DeleteMapping("/{historyId}")
    public ResponseResult<?> deleteHistory(@Parameter(description = "历史ID") @PathVariable Integer historyId) {
        try {
            boolean result = userNameHistoryService.deleteHistory(historyId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除用户名变更历史失败");
            }
        } catch (Exception e) {
            log.error("删除用户名变更历史失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取用户名变更历史", description = "根据用户ID获取用户名变更历史列表")
    @GetMapping("/user/{userId}")
    public ResponseResult<?> getHistoriesByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            List<UserNameHistory> histories = userNameHistoryService.getUserNameHistoriesByUserId(userId);
            return ResponseResult.success(histories);
        } catch (Exception e) {
            log.error("根据用户ID获取用户名变更历史失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取用户名变更历史（分页）", description = "根据用户ID获取用户名变更历史列表（分页）")
    @GetMapping("/user/{userId}/page")
    public ResponseResult<?> getHistoriesByUserIdWithPage(@Parameter(description = "用户ID") @PathVariable Integer userId,
                                                         @RequestParam(defaultValue = "1") Integer current,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserNameHistory> historiesPage = userNameHistoryService.getUserNameHistoriesByUserId(userId, current, pageSize);
            return ResponseResultPage.ok(historiesPage.getRecords(), historiesPage.getCurrent(), historiesPage.getSize(), historiesPage.getTotal());
        } catch (Exception e) {
            log.error("根据用户ID获取用户名变更历史（分页）失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加用户名变更历史", description = "批量添加用户名变更历史")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddHistories(@RequestBody List<UserNameHistory> userNameHistoryList) {
        try {
            boolean result = userNameHistoryService.batchAddHistories(userNameHistoryList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加用户名变更历史失败");
            }
        } catch (Exception e) {
            log.error("批量添加用户名变更历史失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新用户名变更历史", description = "批量更新用户名变更历史信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateHistories(@RequestBody List<UserNameHistory> userNameHistoryList) {
        try {
            boolean result = userNameHistoryService.batchUpdateHistories(userNameHistoryList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新用户名变更历史失败");
            }
        } catch (Exception e) {
            log.error("批量更新用户名变更历史失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除用户名变更历史", description = "批量删除用户名变更历史")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteHistories(@RequestBody List<Integer> historyIds) {
        try {
            boolean result = userNameHistoryService.batchDeleteHistories(historyIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除用户名变更历史失败");
            }
        } catch (Exception e) {
            log.error("批量删除用户名变更历史失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}
