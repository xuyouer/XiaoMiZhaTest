package ltd.xiaomizha.xuyou.signin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.signin.service.SignInConfigService;
import ltd.xiaomizha.xuyou.signin.service.SignInService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("signin")
@Tag(name = "签到服务管理", description = "签到服务API")
public class SignInController {

    @Resource
    private SignInService signInService;

    @Resource
    private SignInConfigService signInConfigService;

    /**
     * 用户签到
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    @Operation(summary = "用户签到", description = "用户执行签到操作，返回签到结果和奖励信息")
    @PostMapping("/sign")
    public ResponseResult<Map<String, Object>> signIn(@Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        Map<String, Object> result = signInService.signIn(userId);
        return ResponseResult.ok(result);
    }

    /**
     * 检查今日是否已签到
     *
     * @param userId 用户ID
     * @return 是否已签到
     */
    @GetMapping("/check")
    @Operation(summary = "检查今日签到状态", description = "检查用户今日是否已签到")
    public ResponseResult<Map<String, Object>> checkTodaySignIn(@Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        boolean signed = signInService.checkTodaySignIn(userId);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("signed", signed);
        result.put("message", signed ? "今日已签到" : "今日未签到");
        return ResponseResult.ok(result);
    }

    /**
     * 获取用户签到状态
     *
     * @param userId 用户ID
     * @return 签到状态
     */
    @Operation(summary = "获取用户签到状态", description = "获取用户的签到状态信息，包括连续签到天数、总签到次数等")
    @GetMapping("/status")
    public ResponseResult<Map<String, Object>> getSignInStatus(@Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        Map<String, Object> result = signInService.getSignInStatus(userId);
        return ResponseResult.ok(result);
    }

    /**
     * 获取用户月度签到记录
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 月度签到记录monthly
     */
    @Operation(summary = "获取用户月度签到记录", description = "获取指定月份的签到记录，包括签到的日期列表")
    @GetMapping("/monthly")
    public ResponseResult<Map<String, Object>> getMonthlySignInRecord(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "年份") @RequestParam(required = false, defaultValue = "2026") Integer year,
            @Parameter(description = "月份(1-12)") @RequestParam(required = false, defaultValue = "2") Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();
        Map<String, Object> result = signInService.getMonthlySignInRecord(userId, targetYear, targetMonth);
        return ResponseResult.ok(result);
    }

    /**
     * 获取连续签到天数
     *
     * @param userId 用户ID
     * @return 连续签到天数
     */
    @Operation(summary = "获取连续签到天数", description = "获取用户当前的连续签到天数")
    @GetMapping("/continuous")
    public ResponseResult<Map<String, Object>> getContinuousSignInDays(@Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        int days = signInService.getContinuousSignInDays(userId);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("continuousDays", days);
        result.put("message", "连续签到" + days + "天");
        return ResponseResult.ok(result);
    }

    /**
     * 计算签到积分奖励
     *
     * @param userId 用户ID
     * @return 积分奖励
     */
    @Operation(summary = "计算签到积分奖励", description = "根据用户连续签到天数计算签到可获得的积分奖励")
    @GetMapping("/reward")
    public ResponseResult<Map<String, Object>> calculateSignInReward(@Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        int continuousDays = signInService.getContinuousSignInDays(userId);
        int reward = signInConfigService.calculateSignInReward(continuousDays);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("pointsReward", reward);
        result.put("message", "预计签到奖励" + reward + "积分");
        return ResponseResult.ok(result);
    }

    /**
     * 获取签到排行榜
     *
     * @param type  排行榜类型: 1-总签到次数, 2-连续签到天数, 3-本月签到次数
     * @param limit 返回数量
     * @return 签到排行榜
     */
    @Operation(summary = "获取签到排行榜", description = "获取签到排行榜，支持按总签到次数、连续签到天数、本月签到次数排序")
    @GetMapping("/ranking")
    public ResponseResult<Map<String, Object>> getSignInRanking(
            @Parameter(description = "排行榜类型: 1-总签到次数, 2-连续签到天数, 3-本月签到次数") @RequestParam(required = false, defaultValue = "1") int type,
            @Parameter(description = "返回数量") @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        Map<String, Object> result = signInService.getSignInRanking(type, limit);
        return ResponseResult.ok(result);
    }

    /**
     * 获取所有用户签到记录列表
     *
     * @param current   当前页码
     * @param pageSize  每页数量
     * @param userId    用户ID
     * @param userName  用户名
     * @param year      年份
     * @param month     月份(1-12)
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 所有用户的签到记录
     */
    @Operation(summary = "获取所有用户签到记录列表", description = "分页获取所有用户的签到记录，支持按用户ID、用户名、月份筛选，支持排序")
    @GetMapping("/users")
    public ResponseResultPage<?> getUserSignInList(
            @Parameter(description = "当前页码") @RequestParam(required = false, defaultValue = "1") long current,
            @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "10") long pageSize,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "用户名") @RequestParam(required = false) String userName,
            @Parameter(description = "年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份(1-12)") @RequestParam(required = false) Integer month,
            @Parameter(description = "排序字段(totalSignIns/continuousDays/pointsEarned)") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式(ascend/descend)") @RequestParam(required = false) String sortOrder
    ) {
        return ResponseResultPage.ok(signInService.getUserSignInList(current, pageSize, userId, userName, year, month, sortField, sortOrder));
    }

}
