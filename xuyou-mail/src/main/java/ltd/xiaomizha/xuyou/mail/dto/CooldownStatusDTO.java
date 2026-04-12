package ltd.xiaomizha.xuyou.mail.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CooldownStatusDTO {

    /**
     * 是否可发送验证码
     * <p>
     * true: 可以发送
     * <p>
     * false: 冷却中或已达每日上限
     */
    private Boolean canSend;

    /**
     * 剩余冷却时间 (秒)
     * <p>
     * 0 表示无冷却
     */
    private Long remainingSeconds;

    /**
     * 今日已发送次数
     */
    private Integer dailyUsedCount;

    /**
     * 每日最大允许发送次数
     * <p>
     * 默认 20 次/天
     */
    private Integer dailyMaxLimit;

    /**
     * 今日剩余可发送次数
     * = dailyMaxLimit - dailyUsedCount
     */
    private Integer dailyRemainingCount;

    /**
     * 冷却信息
     *
     * @param canSend          是否可发送
     * @param remainingSeconds 剩余冷却时间 (秒)
     */
    public CooldownStatusDTO(Boolean canSend, Long remainingSeconds) {
        this.canSend = canSend;
        this.remainingSeconds = remainingSeconds;
    }

    /**
     * 冷却和每日限制信息
     *
     * @param canSend             是否可发送
     * @param remainingSeconds    剩余冷却时间 (秒)
     * @param dailyUsedCount      今日已用次数
     * @param dailyMaxLimit       每日上限
     * @param dailyRemainingCount 今日剩余次数
     */
    public CooldownStatusDTO(Boolean canSend, Long remainingSeconds, Integer dailyUsedCount, Integer dailyMaxLimit, Integer dailyRemainingCount) {
        this.canSend = canSend;
        this.remainingSeconds = remainingSeconds;
        this.dailyUsedCount = dailyUsedCount;
        this.dailyMaxLimit = dailyMaxLimit;
        this.dailyRemainingCount = dailyRemainingCount;
    }
}
