package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.DateConstant;
import ltd.xiaomizha.xuyou.common.constant.SignInConstant;
import ltd.xiaomizha.xuyou.common.utils.date.DateUtils;
import ltd.xiaomizha.xuyou.common.utils.redis.RedisSignInUtils;
import ltd.xiaomizha.xuyou.signin.config.RabbitMQConfig;
import ltd.xiaomizha.xuyou.signin.entity.SignIn;
import ltd.xiaomizha.xuyou.signin.entity.SignInStatus;
import ltd.xiaomizha.xuyou.signin.mapper.SignInMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInConfigService;
import ltd.xiaomizha.xuyou.signin.service.SignInMonthlyReportService;
import ltd.xiaomizha.xuyou.signin.service.SignInService;
import ltd.xiaomizha.xuyou.signin.service.SignInStatusService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiaom
 * @description 针对表【sign_in(签到表)】的数据库操作Service实现
 * @createDate 2026-02-25 18:44:03
 */
@Slf4j
@Service
public class SignInServiceImpl extends ServiceImpl<SignInMapper, SignIn>
        implements SignInService {

    @Resource
    private SignInStatusService signInStatusService;

    @Resource
    private SignInConfigService signInConfigService;

    @Resource
    private SignInMonthlyReportService signInMonthlyReportService;

    @Resource
    private RedisSignInUtils redisSignInUtils;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 用户签到
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    @Override
    public Map<String, Object> signIn(Long userId) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime today = DateUtils.getCurrentDateTime();
        String lockKey = SignInConstant.LOCK_KEY_PREFIX + userId + ":" + today.toLocalDate();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(SignInConstant.LOCK_WAIT_SECONDS, SignInConstant.LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                result.put("success", false);
                result.put("message", "签到请求处理中, 请稍后再试");
                return result;
            }

            // 检查今日是否已签到
            if (isAlreadySignedIn(userId, today)) {
                result.put("success", false);
                result.put("message", "今日已签到");
                return result;
            }

            // 获取连续签到天数
            int continuousDays = getContinuousSignInDays(userId);

            // 计算积分奖励
            int pointsReward = signInConfigService.calculateSignInReward(continuousDays);

            // 创建签到记录
            SignIn signIn = new SignIn();
            signIn.setUserId(userId);
            signIn.setSignInDate(today);
            signIn.setContinuousDays(continuousDays);
            signIn.setPointsReward(pointsReward);
            save(signIn);

            // 更新或创建签到状态
            signInStatusService.updateOrCreateSignInStatus(userId, continuousDays, today);

            // 同步到 Redis
            // 使用 Bitmap 和 HyperLogLog
            redisSignInUtils.syncSignInToRedis(userId, today);

            result.put("success", true);
            result.put("message", "签到成功");
            result.put("continuousDays", continuousDays);
            result.put("pointsReward", pointsReward);
            // result.put("signInDate", today);
            result.put("signInDate", DateConstant.DATE_TIME_FORMATTER.format(today));

            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.put("success", false);
            result.put("message", "签到失败, 请稍后重试");
            return result;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private boolean isAlreadySignedIn(Long userId, LocalDateTime today) {
        // 检查 Redis 中的签到记录
        if (redisSignInUtils.checkSignInWithBitmap(userId, today)) {
            return true;
        }
        // 检查 MySQL 中的签到记录
        if (checkTodaySignIn(userId)) {
            redisSignInUtils.syncSignInToRedis(userId, today);
            return true;
        }
        return false;
    }

    /**
     * 发送签到积分奖励消息到 RabbitMQ
     *
     * @param userId         用户ID
     * @param pointsReward   积分奖励
     * @param continuousDays 连续签到天数
     */
    private void sendSignInRewardMessage(Long userId, int pointsReward, int continuousDays) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("pointsReward", pointsReward);
        message.put("continuousDays", continuousDays);
        message.put("timestamp", System.currentTimeMillis());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SIGN_IN_EXCHANGE,
                RabbitMQConfig.SIGN_IN_REWARD_ROUTING_KEY,
                message
        );
    }

    /**
     * 检查用户今日是否已签到
     *
     * @param userId 用户ID
     * @return 是否已签到
     */
    @Override
    public boolean checkTodaySignIn(Long userId) {
        LambdaQueryWrapper<SignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignIn::getUserId, userId)
                .ge(SignIn::getSignInDate, DateUtils.getStartOfDay())
                .le(SignIn::getSignInDate, DateUtils.getEndOfDay());
        return count(wrapper) > 0;
    }

    /**
     * 获取用户连续签到天数
     *
     * @param userId 用户ID
     * @return 连续签到天数
     */
    @Override
    public int getContinuousSignInDays(Long userId) {
        LocalDate today = DateUtils.getCurrentDate();
        LocalDateTime startDate = today.minusDays(SignInConstant.MAX_CONTINUOUS_DAYS_CHECK).atStartOfDay();

        List<LocalDateTime> signInDates = baseMapper.selectRecentSignInDates(userId, startDate);

        if (signInDates.isEmpty()) {
            return 0;
        }

        Set<LocalDate> signedDates = signInDates.stream()
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.toSet());

        int continuousDays = 0;
        LocalDate checkDate = today.minusDays(1);

        while (signedDates.contains(checkDate)) {
            continuousDays++;
            checkDate = checkDate.minusDays(1);
        }

        return continuousDays;
    }

    /**
     * 获取用户签到状态
     *
     * @param userId 用户ID
     * @return 签到状态
     */
    @Override
    public Map<String, Object> getSignInStatus(Long userId) {
        Map<String, Object> status = new HashMap<>();

        // 检查今日是否已签到
        status.put("todaySigned", checkTodaySignIn(userId));

        // 获取连续签到天数
        int continuousDays = getContinuousSignInDays(userId);
        status.put("continuousDays", continuousDays);

        // 获取签到状态记录
        SignInStatus signInStatus = signInStatusService.getSignInStatusByUserId(userId);

        if (signInStatus != null) {
            status.put("totalSignIns", signInStatus.getTotalSignIns());
            status.put("maxContinuousDays", signInStatus.getMaxContinuousDays());
            // status.put("lastSignInDate", DateConstant.DATE_TIME_FORMATTER.format(signInStatus.getLastSignInDate()));
            status.put("lastSignInDate", signInStatus.getLastSignInDate() != null
                    ? DateConstant.DATE_TIME_FORMATTER.format(signInStatus.getLastSignInDate())
                    : null);
            status.put("isContinuous", signInStatus.getIsContinuous() == 1);
        } else {
            status.put("totalSignIns", 0);
            status.put("maxContinuousDays", 0);
            status.put("lastSignInDate", null);
            status.put("isContinuous", false);
        }

        // 计算明日签到奖励
        status.put("tomorrowReward", signInConfigService.calculateSignInReward(continuousDays));

        return status;
    }

    /**
     * 获取用户月度签到记录
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 月度签到记录
     */
    @Override
    public Map<String, Object> getMonthlySignInRecord(Long userId, int year, int month) {
        return signInMonthlyReportService.getMonthlySignInRecord(userId, year, month);
    }

    /**
     * 获取签到排行榜
     *
     * @param type  排行榜类型: 1-总签到次数, 2-连续签到天数, 3-本月签到次数
     * @param limit 返回数量
     * @return 签到排行榜
     */
    @Override
    public Map<String, Object> getSignInRanking(int type, int limit) {
        Map<String, Object> result = new HashMap<>();
        int safeLimit = Math.max(1, Math.min(limit, 100));

        List<Map<String, Object>> rankingList = switch (type) {
            case 1 -> buildTotalSignInsRanking(safeLimit);
            case 2 -> buildContinuousDaysRanking(safeLimit);
            case 3 -> buildMonthlySignInsRanking(safeLimit);
            default -> {
                result.put("success", false);
                result.put("message", "无效的排行榜类型");
                yield null;
            }
        };

        if (rankingList == null) {
            return result;
        }

        result.put("success", true);
        result.put("rankingList", rankingList);
        result.put("type", type);
        result.put("limit", safeLimit);

        return result;
    }

    private List<Map<String, Object>> buildTotalSignInsRanking(int limit) {
        List<SignInStatus> statusList = signInStatusService.list(
                new LambdaQueryWrapper<SignInStatus>()
                        .orderByDesc(SignInStatus::getTotalSignIns)
                        .last("LIMIT " + limit));
        return buildRankingList(statusList, SignInStatus::getTotalSignIns, "totalSignIns");
    }

    private List<Map<String, Object>> buildContinuousDaysRanking(int limit) {
        List<SignInStatus> statusList = signInStatusService.list(
                new LambdaQueryWrapper<SignInStatus>()
                        .orderByDesc(SignInStatus::getCurrentContinuousDays)
                        .last("LIMIT " + limit));
        return buildRankingList(statusList, SignInStatus::getCurrentContinuousDays, "continuousDays");
    }

    private List<Map<String, Object>> buildMonthlySignInsRanking(int limit) {
        List<Map<String, Object>> monthlySignInsList = baseMapper.selectMonthlySignInsRanking(
                DateUtils.getStartOfMonth(), DateUtils.getEndOfMonth(), limit);

        List<Map<String, Object>> rankingList = new ArrayList<>();
        int rank = 1;
        for (Map<String, Object> item : monthlySignInsList) {
            Map<String, Object> rankingItem = new HashMap<>();
            rankingItem.put("userId", item.get("userId"));
            rankingItem.put("monthlySignIns", item.get("count"));
            rankingItem.put("rank", rank++);
            rankingList.add(rankingItem);
        }
        return rankingList;
    }

    private List<Map<String, Object>> buildRankingList(List<SignInStatus> statusList, Function<SignInStatus, Object> valueExtractor, String valueKey) {
        List<Map<String, Object>> rankingList = new ArrayList<>();
        int rank = 1;
        for (SignInStatus status : statusList) {
            Map<String, Object> item = new HashMap<>();
            item.put("userId", status.getUserId());
            item.put(valueKey, valueExtractor.apply(status));
            item.put("rank", rank++);
            rankingList.add(item);
        }
        return rankingList;
    }

    /**
     * 获取所有用户签到记录列表
     *
     * @param current   当前页码
     * @param pageSize  每页数量
     * @param userId    用户ID
     * @param userName  用户名
     * @param year      年份
     * @param month     月份
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 分页用户签到列表
     */
    @Override
    public Page<Map<String, Object>> getUserSignInList(long current, long pageSize, Long userId, String userName, Integer year, Integer month, String sortField, String sortOrder) {
        long offset = (current - 1) * pageSize;

        List<Map<String, Object>> dataList = baseMapper.selectUserSignInList(userId, userName, year, month, sortField, sortOrder, offset, pageSize);
        long total = baseMapper.countUserSignInList(userId, userName, year, month);

        for (Map<String, Object> item : dataList) {
            if (item.get("lastSignInDate") != null) {
                item.put("lastSignInDate", DateConstant.DATE_TIME_FORMATTER.format((LocalDateTime) item.get("lastSignInDate")));
            }
            Object todaySignedObj = item.get("todaySigned");
            item.put("todaySigned", todaySignedObj != null && ((Number) todaySignedObj).intValue() == 1);
        }

        Page<Map<String, Object>> page = new Page<>(current, pageSize, total);
        page.setRecords(dataList);
        return page;
    }

}
