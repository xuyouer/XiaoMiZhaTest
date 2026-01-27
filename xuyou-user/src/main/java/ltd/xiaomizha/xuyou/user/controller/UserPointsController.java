package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserPoints;
import ltd.xiaomizha.xuyou.user.service.UserPointsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("points")
@Tag(name = "用户积分管理", description = "用户积分管理API")
public class UserPointsController {

    @Resource
    private UserPointsService userPointsService;

    @Operation(summary = "获取积分列表", description = "分页获取所有用户积分信息")
    @GetMapping("/list")
    public ResponseResult<?> getPointsPage(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserPoints> pointsPage = userPointsService.getPointsPage(current, pageSize);
            return ResponseResultPage.ok(pointsPage.getRecords(), pointsPage.getCurrent(), pointsPage.getSize(), pointsPage.getTotal());
        } catch (Exception e) {
            log.error("获取积分列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取积分详情", description = "根据积分ID获取积分详情")
    @GetMapping("/{pointsId}")
    public ResponseResult<?> getPointsById(@Parameter(description = "积分ID") @PathVariable Integer pointsId) {
        try {
            UserPoints points = userPointsService.getPointsById(pointsId);
            return ResponseResult.success(points);
        } catch (Exception e) {
            log.error("获取积分详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取积分信息", description = "根据用户ID获取该用户的积分信息")
    @GetMapping("/user/{userId}")
    public ResponseResult<?> getPointsByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            UserPoints points = userPointsService.getUserPointsByUserId(userId);
            return ResponseResult.success(points);
        } catch (Exception e) {
            log.error("根据用户ID获取积分信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增积分记录", description = "新增用户积分记录")
    @PostMapping
    public ResponseResult<?> addPoints(@RequestBody UserPoints userPoints) {
        try {
            boolean result = userPointsService.addPoints(userPoints);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增积分记录失败");
            }
        } catch (Exception e) {
            log.error("新增积分记录失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新积分记录", description = "更新用户积分信息")
    @PutMapping("/{pointsId}")
    public ResponseResult<?> updatePoints(@Parameter(description = "积分ID") @PathVariable Integer pointsId,
                                          @RequestBody UserPoints userPoints) {
        try {
            boolean result = userPointsService.updatePoints(pointsId, userPoints);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新积分记录失败");
            }
        } catch (Exception e) {
            log.error("更新积分记录失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除积分记录", description = "根据积分ID删除积分记录")
    @DeleteMapping("/{pointsId}")
    public ResponseResult<?> deletePoints(@Parameter(description = "积分ID") @PathVariable Integer pointsId) {
        try {
            boolean result = userPointsService.deletePoints(pointsId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除积分记录失败");
            }
        } catch (Exception e) {
            log.error("删除积分记录失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加积分记录", description = "批量添加用户积分记录")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddPoints(@RequestBody List<UserPoints> userPointsList) {
        try {
            boolean result = userPointsService.batchAddPoints(userPointsList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加积分记录失败");
            }
        } catch (Exception e) {
            log.error("批量添加积分记录失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新积分记录", description = "批量更新用户积分信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdatePoints(@RequestBody List<UserPoints> userPointsList) {
        try {
            boolean result = userPointsService.batchUpdatePoints(userPointsList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新积分记录失败");
            }
        } catch (Exception e) {
            log.error("批量更新积分记录失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除积分记录", description = "批量删除用户积分记录")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeletePoints(@RequestBody List<Integer> pointsIds) {
        try {
            boolean result = userPointsService.batchDeletePoints(pointsIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除积分记录失败");
            }
        } catch (Exception e) {
            log.error("批量删除积分记录失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}