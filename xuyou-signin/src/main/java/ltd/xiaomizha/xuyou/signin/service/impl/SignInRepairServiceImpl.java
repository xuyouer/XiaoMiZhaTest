package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.DateConstant;
import ltd.xiaomizha.xuyou.common.constant.SignInConstant;
import ltd.xiaomizha.xuyou.common.enums.entity.Source;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.signin.entity.SignIn;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairRecord;
import ltd.xiaomizha.xuyou.signin.entity.SignInStatus;
import ltd.xiaomizha.xuyou.signin.mapper.SignInMapper;
import ltd.xiaomizha.xuyou.signin.service.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SignInRepairServiceImpl implements SignInRepairService {

    @Resource
    private SignInRepairCardService repairCardService;

    @Resource
    private SignInRepairRecordService repairRecordService;

    @Resource
    private SignInRepairConfigService repairConfigService;

    @Resource
    private SignInConfigService signInConfigService;

    @Resource
    private SignInStatusService signInStatusService;

    @Resource
    private SignInMapper signInMapper;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 执行单日补签
     * 使用补签卡对指定日期进行补签
     *
     * @param userId     用户ID
     * @param repairDate 补签日期
     * @return 补签结果, 包含success、message、continuousDaysBefore、continuousDaysAfter、pointsReward等
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> repair(Long userId, LocalDate repairDate) {
        return doRepair(userId, repairDate, true);
    }

    /**
     * 批量补签
     * 对多个日期进行批量补签
     *
     * @param userId      用户ID
     * @param repairDates 补签日期列表
     * @return 批量补签结果, 包含successCount、failCount、results等
     */
    @Override
    public Map<String, Object> repairMultiple(Long userId, List<LocalDate> repairDates) {
        if (repairDates == null || repairDates.isEmpty()) {
            return buildBatchResult(false, "补签日期列表不能为空", 0, 0, new ArrayList<>());
        }

        int totalDates = repairDates.size();
        int availableCards = repairCardService.getAvailableCardCount(userId, SignInConstant.DEFAULT_CARD_TYPE);
        if (availableCards < totalDates) {
            return buildBatchResult(false,
                    String.format("补签卡数量不足, 需要%d张, 当前只有%d张", totalDates, availableCards),
                    0, 0, new ArrayList<>());
        }

        List<LocalDate> sortedDates = repairDates.stream()
                .sorted(LocalDate::compareTo)
                .collect(Collectors.toList());

        BatchValidationResult validationResult = validateBatchRepair(userId, sortedDates);

        if (validationResult.validDates.isEmpty()) {
            return buildBatchResult(false, "没有可补签的日期", 0, sortedDates.size(), validationResult.failedResults);
        }

        return processBatchRepair(userId, validationResult, sortedDates.size());
    }

    /**
     * 获取用户补签状态
     * 包含补签卡数量、可补签日期等信息
     *
     * @param userId 用户ID
     * @return 补签状态信息
     */
    @Override
    public Map<String, Object> getRepairStatus(Long userId) {
        Map<String, Object> status = new HashMap<>();

        status.put("cards", repairCardService.getUserAllCards(userId));

        List<LocalDate> availableDates = getAvailableRepairDates(userId);
        status.put("availableRepairDates", availableDates);
        status.put("availableRepairCount", availableDates.size());

        status.put("maxRepairDaysAgo", getMaxRepairDaysAgo());

        return status;
    }

    /**
     * 获取用户当前可补签的日期列表
     * 根据配置的最大可补签天数计算
     *
     * @param userId 用户ID
     * @return 可补签日期列表
     */
    @Override
    public List<LocalDate> getAvailableRepairDates(Long userId) {
        int maxDaysAgo = getMaxRepairDaysAgo();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(maxDaysAgo);

        List<LocalDate> allDates = startDate.datesUntil(today).collect(Collectors.toList());

        Set<LocalDate> existingSignDates = batchGetSignInDates(userId, allDates);
        Set<LocalDate> repairedDates = repairRecordService.batchGetRepairedDates(userId, allDates);

        return allDates.stream()
                .filter(date -> !existingSignDates.contains(date) && !repairedDates.contains(date))
                .collect(Collectors.toList());
    }

    /**
     * 获取补签预览
     * 预览补签后的连续签到天数和积分奖励
     *
     * @param userId     用户ID
     * @param repairDate 补签日期
     * @return 预览信息, 包含valid、continuousDaysBefore、continuousDaysAfter、pointsReward等
     */
    @Override
    public Map<String, Object> getRepairPreview(Long userId, LocalDate repairDate) {
        Map<String, Object> validation = validateRepair(userId, repairDate);
        Map<String, Object> preview = new HashMap<>();
        preview.put("valid", validation.get("valid"));
        preview.put("message", validation.get("message"));

        if (Boolean.TRUE.equals(validation.get("valid"))) {
            int continuousDaysBefore = calculateContinuousDaysBeforeRepair(userId, repairDate);
            int continuousDaysAfter = calculateContinuousDaysAfterRepair(userId, repairDate, continuousDaysBefore);

            int pointsReward = calculateRepairPointsReward(continuousDaysAfter);

            preview.put("continuousDaysBefore", continuousDaysBefore);
            preview.put("continuousDaysAfter", continuousDaysAfter);
            preview.put("pointsReward", pointsReward);
            preview.put("cardRequired", SignInConstant.DEFAULT_CARD_TYPE);
        }

        return preview;
    }

    /**
     * 给所有用户发放每月补签卡
     * 定时任务调用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantMonthlyCardsToAllUsers() {
        log.info("开始执行每月补签卡发放任务");

        int grantQuantity = getMonthlyGrantQuantity();

        List<Long> allUserIds = signInStatusService.getAllUserIds();

        for (Long userId : allUserIds) {
            try {
                repairCardService.addCards(userId, SignInConstant.DEFAULT_CARD_TYPE, grantQuantity, Source.MONTHLY_GRANT, "每月自动发放");
            } catch (Exception e) {
                log.error("用户{}补签卡发放失败: {}", userId, e.getMessage());
            }
        }

        log.info("每月补签卡发放任务完成, 共处理{}个用户", allUserIds.size());
    }

    /**
     * 给指定用户发放每月补签卡
     *
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantMonthlyCardsToUser(Long userId) {
        int grantQuantity = getMonthlyGrantQuantity();
        repairCardService.addCards(userId, SignInConstant.DEFAULT_CARD_TYPE, grantQuantity, Source.MONTHLY_GRANT, "每月自动发放");
        log.info("用户{}本月补签卡发放成功, 数量: {}", userId, grantQuantity);
    }

    private Map<String, Object> doRepair(Long userId, LocalDate repairDate, boolean validateCard) {
        String lockKey = SignInConstant.REPAIR_LOCK_KEY_PREFIX + userId + ":" + repairDate;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(SignInConstant.LOCK_WAIT_SECONDS, SignInConstant.LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                return buildRepairResult(false, "补签请求处理中, 请稍后再试", repairDate);
            }

            if (validateCard) {
                Map<String, Object> validationResult = validateRepair(userId, repairDate);
                if (!Boolean.TRUE.equals(validationResult.get("valid"))) {
                    return buildRepairResult(false, (String) validationResult.get("message"), repairDate);
                }

                if (repairCardService.getAvailableCardCount(userId, SignInConstant.DEFAULT_CARD_TYPE) <= 0) {
                    return buildRepairResult(false, "补签卡数量不足", repairDate);
                }
            }

            if (!repairCardService.useCard(userId, SignInConstant.DEFAULT_CARD_TYPE)) {
                return buildRepairResult(false, "补签卡使用失败", repairDate);
            }

            return executeRepairTransaction(userId, repairDate);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return buildRepairResult(false, "补签失败, 请稍后重试", repairDate);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> executeRepairTransaction(Long userId, LocalDate repairDate) {
        int continuousDaysBefore = calculateContinuousDaysBeforeRepair(userId, repairDate);
        int continuousDaysAfter = calculateContinuousDaysAfterRepair(userId, repairDate, continuousDaysBefore);
        int pointsReward = calculateRepairPointsReward(continuousDaysAfter);

        SignIn signIn = new SignIn();
        signIn.setUserId(userId);
        signIn.setSignInDate(repairDate.atStartOfDay());
        signIn.setContinuousDays(continuousDaysAfter);
        signIn.setPointsReward(pointsReward);
        signInMapper.insert(signIn);

        SignInRepairRecord repairRecord = new SignInRepairRecord();
        repairRecord.setUserId(userId);
        repairRecord.setRepairDate(repairDate);
        repairRecord.setCardType(SignInConstant.DEFAULT_CARD_TYPE);
        repairRecord.setContinuousDaysBefore(continuousDaysBefore);
        repairRecord.setContinuousDaysAfter(continuousDaysAfter);
        repairRecord.setPointsReward(pointsReward);
        repairRecord.setStatus(Status.SUCCESS);
        repairRecordService.save(repairRecord);

        updateSignInStatusAfterRepair(userId, continuousDaysAfter);

        Map<String, Object> result = buildRepairResult(true, "补签成功", repairDate);
        result.put("continuousDaysBefore", continuousDaysBefore);
        result.put("continuousDaysAfter", continuousDaysAfter);
        result.put("pointsReward", pointsReward);
        return result;
    }

    private Map<String, Object> validateRepair(Long userId, LocalDate repairDate) {
        LocalDate today = LocalDate.now();

        if (!repairDate.isBefore(today)) {
            return Map.of("valid", false, "message", "只能补签过去的日期");
        }

        LocalDate maxRepairDate = today.minusDays(getMaxRepairDaysAgo());
        if (repairDate.isBefore(maxRepairDate)) {
            return Map.of("valid", false, "message", "超过最大可补签天数限制");
        }

        if (hasSignInRecord(userId, repairDate)) {
            return Map.of("valid", false, "message", "该日期已有签到记录");
        }

        if (repairRecordService.hasRepaired(userId, repairDate)) {
            return Map.of("valid", false, "message", "该日期已补签过");
        }

        return Map.of("valid", true, "message", "可以补签");
    }

    private BatchValidationResult validateBatchRepair(Long userId, List<LocalDate> repairDates) {
        LocalDate today = LocalDate.now();
        LocalDate maxRepairDate = today.minusDays(getMaxRepairDaysAgo());

        Set<LocalDate> existingSignDates = batchGetSignInDates(userId, repairDates);
        Set<LocalDate> repairedDates = repairRecordService.batchGetRepairedDates(userId, repairDates);

        List<LocalDate> validDates = new ArrayList<>();
        List<Map<String, Object>> failedResults = new ArrayList<>();

        for (LocalDate date : repairDates) {
            if (!date.isBefore(today)) {
                failedResults.add(buildRepairResult(false, "只能补签过去的日期", date));
            } else if (date.isBefore(maxRepairDate)) {
                failedResults.add(buildRepairResult(false, "超过最大可补签天数限制", date));
            } else if (existingSignDates.contains(date)) {
                failedResults.add(buildRepairResult(false, "该日期已有签到记录", date));
            } else if (repairedDates.contains(date)) {
                failedResults.add(buildRepairResult(false, "该日期已补签过", date));
            } else {
                validDates.add(date);
            }
        }

        return new BatchValidationResult(validDates, failedResults);
    }

    private Map<String, Object> processBatchRepair(Long userId, BatchValidationResult validationResult, int totalSize) {
        List<Map<String, Object>> results = new ArrayList<>(validationResult.failedResults);
        int successCount = 0;
        int failCount = validationResult.failedResults.size();

        for (LocalDate date : validationResult.validDates) {
            Map<String, Object> repairResult = doRepair(userId, date, false);
            results.add(repairResult);
            if (Boolean.TRUE.equals(repairResult.get("success"))) {
                successCount++;
            } else {
                failCount++;
            }
        }

        return buildBatchResult(successCount > 0,
                String.format("补签完成: 成功%d次, 失败%d次", successCount, failCount),
                successCount, failCount, results);
    }

    private int calculateContinuousDaysBeforeRepair(Long userId, LocalDate repairDate) {
        SignInStatus status = signInStatusService.getSignInStatusByUserId(userId);
        if (status == null || status.getLastSignInDate() == null) {
            return 0;
        }

        LocalDate lastSignInDate = status.getLastSignInDate().toLocalDate();
        if (lastSignInDate.isBefore(repairDate)) {
            return 0;
        }

        LocalDate startDate = repairDate.minusDays(SignInConstant.MAX_CONTINUOUS_DAYS_CHECK);
        List<LocalDateTime> signInDates = signInMapper.selectRecentSignInDates(userId, startDate.atStartOfDay());

        Set<LocalDate> signedDates = signInDates.stream()
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.toSet());

        int continuousDays = 0;
        LocalDate checkDate = repairDate.minusDays(1);

        while (signedDates.contains(checkDate)) {
            continuousDays++;
            checkDate = checkDate.minusDays(1);
        }

        return continuousDays;
    }

    private int calculateContinuousDaysAfterRepair(Long userId, LocalDate repairDate, int continuousDaysBefore) {
        int continuousDays = continuousDaysBefore + 1;

        LocalDate startDate = repairDate.plusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(1);

        LambdaQueryWrapper<SignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignIn::getUserId, userId)
                .ge(SignIn::getSignInDate, startDate.atStartOfDay())
                .lt(SignIn::getSignInDate, endDate.atStartOfDay())
                .select(SignIn::getSignInDate);

        List<SignIn> signIns = signInMapper.selectList(wrapper);
        Set<LocalDate> signedDates = signIns.stream()
                .map(s -> s.getSignInDate().toLocalDate())
                .collect(Collectors.toSet());

        LocalDate checkDate = startDate;
        while (signedDates.contains(checkDate)) {
            continuousDays++;
            checkDate = checkDate.plusDays(1);
        }

        return continuousDays;
    }

    private boolean hasSignInRecord(Long userId, LocalDate date) {
        LambdaQueryWrapper<SignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignIn::getUserId, userId)
                .ge(SignIn::getSignInDate, date.atStartOfDay())
                .lt(SignIn::getSignInDate, date.plusDays(1).atStartOfDay());
        return signInMapper.selectCount(wrapper) > 0;
    }

    private Set<LocalDate> batchGetSignInDates(Long userId, List<LocalDate> dates) {
        if (dates.isEmpty()) {
            return Collections.emptySet();
        }

        LocalDate minDate = Collections.min(dates);
        LocalDate maxDate = Collections.max(dates);

        LambdaQueryWrapper<SignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignIn::getUserId, userId)
                .ge(SignIn::getSignInDate, minDate.atStartOfDay())
                .lt(SignIn::getSignInDate, maxDate.plusDays(1).atStartOfDay())
                .select(SignIn::getSignInDate);

        return signInMapper.selectList(wrapper).stream()
                .map(s -> s.getSignInDate().toLocalDate())
                .collect(Collectors.toSet());
    }

    private void updateSignInStatusAfterRepair(Long userId, int continuousDaysAfter) {
        SignInStatus status = signInStatusService.getSignInStatusByUserId(userId);
        if (status == null) {
            return;
        }

        status.setTotalSignIns(status.getTotalSignIns() + 1);
        if (continuousDaysAfter > status.getCurrentContinuousDays()) {
            status.setCurrentContinuousDays(continuousDaysAfter);
        }
        if (continuousDaysAfter > status.getMaxContinuousDays()) {
            status.setMaxContinuousDays(continuousDaysAfter);
        }
        status.setUpdatedAt(LocalDateTime.now());
        signInStatusService.updateById(status);
    }

    private int calculateRepairPointsReward(int continuousDaysAfter) {
        double pointsRatio = repairConfigService.getConfigValueAsDouble("repair_points_ratio", SignInConstant.DEFAULT_POINTS_RATIO);
        int normalReward = signInConfigService.calculateSignInReward(continuousDaysAfter);
        return (int) Math.ceil(normalReward * pointsRatio);
    }

    private int getMaxRepairDaysAgo() {
        return repairConfigService.getConfigValueAsInt("max_repair_days_ago", SignInConstant.DEFAULT_MAX_REPAIR_DAYS);
    }

    private int getMonthlyGrantQuantity() {
        return repairConfigService.getConfigValueAsInt("monthly_grant_quantity", SignInConstant.DEFAULT_MONTHLY_GRANT_QUANTITY);
    }

    private Map<String, Object> buildRepairResult(boolean success, String message, LocalDate repairDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        result.put("repairDate", DateConstant.DATE_FORMATTER.format(repairDate));
        return result;
    }

    private Map<String, Object> buildBatchResult(boolean success, String message,
                                                 int successCount, int failCount,
                                                 List<Map<String, Object>> results) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("results", results);
        return result;
    }

    private record BatchValidationResult(List<LocalDate> validDates, List<Map<String, Object>> failedResults) {
    }
}
