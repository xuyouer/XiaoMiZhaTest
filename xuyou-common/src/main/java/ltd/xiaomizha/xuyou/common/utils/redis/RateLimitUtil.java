package ltd.xiaomizha.xuyou.common.utils.redis;

import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.CacheConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Redis 限流工具类
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RateLimitUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 检查冷却时间是否已过
     *
     * @param key             唯一标识, 如: 邮箱地址
     * @param cooldownSeconds 冷却时间 (秒), 如: 60秒
     * @return 冷却状态对象, 包含: 是否可执行、剩余秒数
     */
    public CooldownStatus checkCooldown(String key, int cooldownSeconds) {
        String redisKey = CacheConstant.REDIS_PREFIX_COOLDOWN + key;

        String remainingCooldown = stringRedisTemplate.opsForValue().get(redisKey);

        if (remainingCooldown != null) {
            long remainingSeconds = Long.parseLong(remainingCooldown);
            log.debug("冷却中: key={}, 剩余={}s", key, remainingSeconds);
            return new CooldownStatus(false, remainingSeconds);
        }

        return new CooldownStatus(true, 0L);
    }

    /**
     * 设置冷却时间
     * <p>
     * 操作成功后调用, 设置下次操作的冷却时间
     *
     * @param key             唯一标识
     * @param cooldownSeconds 冷却时间 (秒)
     */
    public void setCooldown(String key, int cooldownSeconds) {
        String redisKey = CacheConstant.REDIS_PREFIX_COOLDOWN + key;
        stringRedisTemplate.opsForValue().set(
                redisKey,
                String.valueOf(cooldownSeconds),
                cooldownSeconds,
                TimeUnit.SECONDS
        );
        log.debug("设置冷却时间: key={}, {}s", key, cooldownSeconds);
    }

    /**
     * 清除冷却时间
     *
     * @param key 唯一标识
     */
    public void clearCooldown(String key) {
        String redisKey = CacheConstant.REDIS_PREFIX_COOLDOWN + key;
        stringRedisTemplate.delete(redisKey);
        log.debug("清除冷却时间: key={}", key);
    }

    /**
     * 检查每日次数限制
     * <p>
     * 基于日期的计数器, 每天自动重置
     *
     * @param key           唯一标识, 如: 邮箱地址
     * @param maxDailyLimit 每日最大允许次数，如20次
     * @return 每日限制状态对象, 包含: 是否允许、今日已用次数、剩余次数
     */
    public DailyLimitStatus checkDailyLimit(String key, int maxDailyLimit) {
        String dateStr = LocalDate.now().toString(); // yyyy-MM-dd
        String redisKey = CacheConstant.REDIS_PREFIX_DAILY_LIMIT + dateStr + ":" + key;

        String countStr = stringRedisTemplate.opsForValue().get(redisKey);

        int currentCount = 0;
        if (countStr != null) {
            currentCount = Integer.parseInt(countStr);
        }

        if (currentCount >= maxDailyLimit) {
            log.warn("达到每日限制: key={}, 当前次数={}, 上限={}", key, currentCount, maxDailyLimit);
            return new DailyLimitStatus(false, currentCount, 0);
        }

        return new DailyLimitStatus(true, currentCount, maxDailyLimit - currentCount);
    }

    /**
     * 增加每日计数并设置过期时间
     * <p>
     * 操作成功后调用, 增加当日使用次数,
     * 自动设置过期时间为当天结束 (次日自动重置)
     *
     * @param key 唯一标识
     * @return 当前的总使用次数
     */
    public int incrementDailyCount(String key) {
        String dateStr = LocalDate.now().toString();
        String redisKey = CacheConstant.REDIS_PREFIX_DAILY_LIMIT + dateStr + ":" + key;

        Long count = stringRedisTemplate.opsForValue().increment(redisKey);

        // 如果第一次设置, 则设置过期时间为当天结束
        // 次日0点自动删除
        if (count != null && count == 1) {
            long secondsUntilMidnight = getSecondsUntilMidnight();
            stringRedisTemplate.expire(redisKey, secondsUntilMidnight, TimeUnit.SECONDS);
            log.debug("首次设置每日计数: key={}, 过期时间={}s后", key, secondsUntilMidnight);
        }

        log.info("增加每日计数: key={}, 当前次数={}", key, count);
        return count.intValue();
    }

    /**
     * 获取当前每日已使用次数
     *
     * @param key 唯一标识
     * @return 今日已使用次数
     */
    public int getDailyUsedCount(String key) {
        String dateStr = LocalDate.now().toString();
        String redisKey = CacheConstant.REDIS_PREFIX_DAILY_LIMIT + dateStr + ":" + key;

        String countStr = stringRedisTemplate.opsForValue().get(redisKey);

        if (countStr != null) {
            return Integer.parseInt(countStr);
        }

        return 0;
    }

    /**
     * 计算距离今天午夜还有多少秒
     * <p>
     * 设置 Redis Key 的过期时间 (次日自动重置)
     *
     * @return 距离午夜的总秒数
     */
    private long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight).getSeconds();
    }

    /**
     * 冷却时间状态 DTO
     */
    @Data
    public static class CooldownStatus {
        /**
         * 是否可以执行操作
         * <p>
         * true: 可以, false: 冷却中
         */
        private boolean allowed;

        /**
         * 剩余冷却时间 (秒)
         * <p>
         * 0表示无冷却
         */
        private long remainingSeconds;

        public CooldownStatus(boolean allowed, long remainingSeconds) {
            this.allowed = allowed;
            this.remainingSeconds = remainingSeconds;
        }
    }

    /**
     * 每日限制状态 DTO
     */
    @Data
    public static class DailyLimitStatus {
        /**
         * 是否允许执行操作
         * <p>
         * true: 允许, false: 已达上限
         */
        private boolean allowed;

        /**
         * 今日已使用次数
         */
        private int usedCount;

        /**
         * 今日剩余可用次数
         */
        private int remainingCount;

        public DailyLimitStatus(boolean allowed, int usedCount, int remainingCount) {
            this.allowed = allowed;
            this.usedCount = usedCount;
            this.remainingCount = remainingCount;
        }
    }

}
