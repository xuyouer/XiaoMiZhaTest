package ltd.xiaomizha.xuyou.common.utils.redis;

import ltd.xiaomizha.xuyou.common.constant.DateConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Redis 签到工具类
 * <p>
 * 用于处理 Bitmap 和 HyperLogLog 相关操作
 */
@Component
public class RedisSignInUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成用户签到 Bitmap 的 key
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return Bitmap key
     */
    public String getSignInBitmapKey(Long userId, int year, int month) {
        return String.format("signin:bitmap:%d:%s", userId, String.format("%04d%02d", year, month));
    }

    /**
     * 生成用户签到统计的 HyperLogLog key
     *
     * @param year  年份
     * @param month 月份
     * @return HyperLogLog key
     */
    public String getSignInHLLKey(int year, int month) {
        return String.format("signin:hll:%s", String.format("%04d%02d", year, month));
    }

    /**
     * 用户签到(使用 Bitmap)
     *
     * @param userId   用户ID
     * @param dateTime 签到日期时间
     * @return 是否签到成功
     */
    public boolean signInWithBitmap(Long userId, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        int year = date.getYear();
        int month = date.getMonthValue();
        int dayOfMonth = date.getDayOfMonth();

        // 生成 Bitmap key
        String bitmapKey = getSignInBitmapKey(userId, year, month);

        // 检查是否已签到
        if (redisTemplate.opsForValue().getBit(bitmapKey, dayOfMonth - 1)) {
            return false; // 已签到
        }

        // 执行签到
        redisTemplate.opsForValue().setBit(bitmapKey, dayOfMonth - 1, true);

        // 将用户添加到月度签到统计(使用 HyperLogLog)
        String hllKey = getSignInHLLKey(year, month);
        redisTemplate.opsForHyperLogLog().add(hllKey, userId.toString());

        return true;
    }

    /**
     * 检查用户是否已签到(使用 Bitmap)
     *
     * @param userId   用户ID
     * @param dateTime 签到日期时间
     * @return 是否已签到
     */
    public boolean checkSignInWithBitmap(Long userId, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        int year = date.getYear();
        int month = date.getMonthValue();
        int dayOfMonth = date.getDayOfMonth();

        String bitmapKey = getSignInBitmapKey(userId, year, month);
        return redisTemplate.opsForValue().getBit(bitmapKey, dayOfMonth - 1);
    }

    /**
     * 获取用户月度签到天数(使用 Bitmap)
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 签到天数
     */
    public long getMonthlySignInDays(Long userId, int year, int month) {
        String bitmapKey = getSignInBitmapKey(userId, year, month);
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(bitmapKey.getBytes()));
    }

    /**
     * 获取月度签到用户数(使用 HyperLogLog)
     *
     * @param year  年份
     * @param month 月份
     * @return 签到用户数
     */
    public long getMonthlySignInUserCount(int year, int month) {
        String hllKey = getSignInHLLKey(year, month);
        return redisTemplate.opsForValue().size(hllKey);
    }

    /**
     * 获取用户连续签到天数(使用 Bitmap)
     *
     * @param userId   用户ID
     * @param dateTime 结束日期时间
     * @return 连续签到天数
     */
    public int getContinuousSignInDays(Long userId, LocalDateTime dateTime) {
        int continuousDays = 0;
        LocalDate checkDate = dateTime.toLocalDate();

        while (true) {
            if (!checkSignInWithBitmap(userId, checkDate.atStartOfDay())) {
                break;
            }
            continuousDays++;
            checkDate = checkDate.minusDays(1);
        }

        return continuousDays;
    }

    /**
     * 获取用户月度签到记录(使用 Bitmap)
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 签到日期集合
     */
    public Set<Integer> getMonthlySignInRecord(Long userId, int year, int month) {
        String bitmapKey = getSignInBitmapKey(userId, year, month);
        Set<Integer> signedDays = new java.util.HashSet<>();

        // 遍历当月所有天数
        LocalDate startDate = LocalDate.of(year, month, 1);
        int daysInMonth = startDate.lengthOfMonth();

        for (int i = 0; i < daysInMonth; i++) {
            if (redisTemplate.opsForValue().getBit(bitmapKey, i)) {
                signedDays.add(i + 1); // 转换为实际日期
            }
        }

        return signedDays;
    }

    /**
     * 清理过期的签到数据
     *
     * @param monthsToKeep 保留的月数
     */
    public void cleanExpiredSignInData(int monthsToKeep) {
        LocalDate now = LocalDate.now();
        LocalDate expireDate = now.minusMonths(monthsToKeep);

        // 清理过期的 Bitmap 数据
        String pattern = String.format("signin:bitmap:*:%s*", expireDate.format(DateConstant.MONTH_FORMATTER_NU).substring(0, 6));
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        // 清理过期的 HyperLogLog 数据
        pattern = String.format("signin:hll:%s*", expireDate.format(DateConstant.MONTH_FORMATTER_NU).substring(0, 6));
        keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 同步 MySQL 签到记录到 Redis
     *
     * @param userId   用户ID
     * @param dateTime 签到日期时间
     */
    public void syncSignInToRedis(Long userId, LocalDateTime dateTime) {
        signInWithBitmap(userId, dateTime);
    }

    /**
     * 从 Redis 同步签到记录到 MySQL
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 同步的签到天数
     */
    public int syncSignInFromRedis(Long userId, int year, int month) {
        Set<Integer> signedDays = getMonthlySignInRecord(userId, year, month);
        return signedDays.size();
    }

}
