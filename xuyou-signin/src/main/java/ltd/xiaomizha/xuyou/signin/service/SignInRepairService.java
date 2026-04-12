package ltd.xiaomizha.xuyou.signin.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 签到补签服务接口
 */
public interface SignInRepairService {

    /**
     * 执行单日补签
     * 使用补签卡对指定日期进行补签
     *
     * @param userId     用户ID
     * @param repairDate 补签日期
     * @return 补签结果, 包含success、message、continuousDaysBefore、continuousDaysAfter、pointsReward等
     */
    Map<String, Object> repair(Long userId, LocalDate repairDate);

    /**
     * 批量补签
     * 对多个日期进行批量补签
     *
     * @param userId      用户ID
     * @param repairDates 补签日期列表
     * @return 批量补签结果, 包含successCount、failCount、results等
     */
    Map<String, Object> repairMultiple(Long userId, List<LocalDate> repairDates);

    /**
     * 获取用户补签状态
     * 包含补签卡数量、可补签日期等信息
     *
     * @param userId 用户ID
     * @return 补签状态信息
     */
    Map<String, Object> getRepairStatus(Long userId);

    /**
     * 获取用户当前可补签的日期列表
     * 根据配置的最大可补签天数计算
     *
     * @param userId 用户ID
     * @return 可补签日期列表
     */
    List<LocalDate> getAvailableRepairDates(Long userId);

    /**
     * 获取补签预览
     * 预览补签后的连续签到天数和积分奖励
     *
     * @param userId     用户ID
     * @param repairDate 补签日期
     * @return 预览信息, 包含valid、continuousDaysBefore、continuousDaysAfter、pointsReward等
     */
    Map<String, Object> getRepairPreview(Long userId, LocalDate repairDate);

    /**
     * 给所有用户发放每月补签卡
     * 定时任务调用
     */
    void grantMonthlyCardsToAllUsers();

    /**
     * 给指定用户发放每月补签卡
     *
     * @param userId 用户ID
     */
    void grantMonthlyCardsToUser(Long userId);

}
