package ltd.xiaomizha.xuyou.signin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.signin.entity.SignInMonthlyReport;

import java.util.Map;

/**
 * @author xiaom
 * @description 针对表【sign_in_monthly_report(月度签到报告表)】的数据库操作Service
 * @createDate 2026-02-25 20:56:46
 */
public interface SignInMonthlyReportService extends IService<SignInMonthlyReport> {

    /**
     * 生成用户月度签到报告
     *
     * @param userId         用户ID
     * @param year           年份
     * @param month          月份
     * @param totalSignIns   月度签到次数
     * @param continuousDays 最大连续签到天数
     * @param pointsEarned   获得的积分
     * @return 月度签到报告对象
     */
    SignInMonthlyReport generateMonthlyReport(Long userId, int year, int month, int totalSignIns, int continuousDays, int pointsEarned);

    /**
     * 获取用户月度签到报告
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 月度签到报告对象
     */
    SignInMonthlyReport getMonthlyReport(Long userId, int year, int month);

    /**
     * 获取用户月度签到记录统计
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 月度签到记录统计
     */
    Map<String, Object> getMonthlySignInRecord(Long userId, int year, int month);

}
