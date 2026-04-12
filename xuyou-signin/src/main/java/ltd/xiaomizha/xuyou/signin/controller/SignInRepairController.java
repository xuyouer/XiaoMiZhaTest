package ltd.xiaomizha.xuyou.signin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.signin.dto.BatchRepairRequest;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairCardService;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairRecordService;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("signin/repair")
@Tag(name = "签到补签服务", description = "签到补签API")
public class SignInRepairController {

    @Resource
    private SignInRepairService signInRepairService;

    @Resource
    private SignInRepairCardService signInRepairCardService;

    @Resource
    private SignInRepairRecordService signInRepairRecordService;

    @Operation(summary = "补签", description = "使用补签卡对指定日期进行补签")
    @PostMapping("/do")
    public ResponseResult<?> repair(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "补签日期(yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate repairDate) {
        Map<String, Object> result = signInRepairService.repair(userId, repairDate);
        return ResponseResult.ok(result);
    }

    @Operation(summary = "批量补签", description = "使用补签卡对多个日期进行批量补签")
    @PostMapping("/batch")
    public ResponseResult<?> repairMultiple(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "补签日期列表(yyyy-MM-dd)", required = true)
            @RequestBody BatchRepairRequest request) {
        if (request == null || request.getRepairDates() == null || request.getRepairDates().isEmpty()) {
            return ResponseResult.error(400, "补签日期列表不能为空");
        }
        Map<String, Object> result = signInRepairService.repairMultiple(userId, request.getRepairDates());
        return ResponseResult.ok(result);
    }

    @Operation(summary = "获取补签状态", description = "获取用户的补签卡数量和可补签日期")
    @GetMapping("/status")
    public ResponseResult<?> getRepairStatus(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        Map<String, Object> result = signInRepairService.getRepairStatus(userId);
        return ResponseResult.ok(result);
    }

    @Operation(summary = "获取可补签日期列表", description = "获取用户当前可补签的日期列表")
    @GetMapping("/available-dates")
    public ResponseResult<?> getAvailableRepairDates(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        List<LocalDate> dates = signInRepairService.getAvailableRepairDates(userId);
        return ResponseResult.ok(dates);
    }

    @Operation(summary = "补签预览", description = "预览补签效果,包括连续签到天数变化和积分奖励")
    @GetMapping("/preview")
    public ResponseResult<?> getRepairPreview(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "补签日期(yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate repairDate) {
        Map<String, Object> result = signInRepairService.getRepairPreview(userId, repairDate);
        return ResponseResult.ok(result);
    }

    @Operation(summary = "获取补签卡数量", description = "获取用户的补签卡库存")
    @GetMapping("/cards")
    public ResponseResult<?> getUserCards(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        Map<String, Object> result = signInRepairCardService.getUserAllCards(userId);
        return ResponseResult.ok(result);
    }

    @Operation(summary = "获取补签记录", description = "分页获取用户的补签历史记录")
    @GetMapping("/records")
    public ResponseResult<?> getRepairRecords(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseResultPage.ok(signInRepairRecordService.getRepairRecordList(userId, current, pageSize));
    }

    @Operation(summary = "领取免费补签卡", description = "每月可领取一次免费补签卡")
    @PostMapping("/claim-card")
    public ResponseResult<?> claimFreeCard(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "补签卡类型: 1-普通补签卡, 2-高级补签卡", required = true) @RequestParam Integer cardType) {
        try {
            Map<String, Object> result = signInRepairCardService.claimFreeCard(userId, cardType);
            return ResponseResult.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("领取补签卡参数错误: {}", e.getMessage());
            return ResponseResult.error(400, e.getMessage());
        } catch (RuntimeException e) {
            log.warn("领取补签卡失败: {}", e.getMessage());
            return ResponseResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("领取补签卡异常: {}", e.getMessage(), e);
            return ResponseResult.error(500, "领取补签卡失败,请稍后重试");
        }
    }

    @Operation(summary = "检查本月是否已领取补签卡", description = "检查用户本月是否已领取过指定类型的补签卡")
    @GetMapping("/claim-status")
    public ResponseResult<?> getClaimStatus(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "补签卡类型: 1-普通补签卡, 2-高级补签卡", required = true) @RequestParam Integer cardType) {
        Map<String, Object> result = new HashMap<>();
        boolean hasClaimed = signInRepairCardService.hasClaimedThisMonth(userId, cardType);
        result.put("hasClaimed", hasClaimed);
        result.put("cardType", cardType);
        result.put("userId", userId);
        return ResponseResult.ok(result);
    }

}
