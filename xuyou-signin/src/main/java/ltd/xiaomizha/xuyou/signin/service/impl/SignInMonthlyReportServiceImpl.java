package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import ltd.xiaomizha.xuyou.common.constant.DateConstant;
import ltd.xiaomizha.xuyou.common.utils.date.DateUtils;
import ltd.xiaomizha.xuyou.signin.entity.SignIn;
import ltd.xiaomizha.xuyou.signin.entity.SignInMonthlyReport;
import ltd.xiaomizha.xuyou.signin.mapper.SignInMapper;
import ltd.xiaomizha.xuyou.signin.mapper.SignInMonthlyReportMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInMonthlyReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author xiaom
 * @description 针对表【sign_in_monthly_report(月度签到报告表)】的数据库操作Service实现
 * @createDate 2026-02-25 20:56:46
 */
@Service
public class SignInMonthlyReportServiceImpl extends ServiceImpl<SignInMonthlyReportMapper, SignInMonthlyReport>
        implements SignInMonthlyReportService {

    @Resource
    private SignInMapper signInMapper;

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
    @Override
    public SignInMonthlyReport generateMonthlyReport(Long userId, int year, int month, int totalSignIns, int continuousDays, int pointsEarned) {
        String reportMonth = String.format("%04d-%02d", year, month);

        // 检查是否已存在月度报告
        SignInMonthlyReport existingReport = getMonthlyReport(userId, year, month);

        if (existingReport != null) {
            // 更新现有报告
            existingReport.setTotalSignIns(totalSignIns);
            existingReport.setContinuousDays(continuousDays);
            existingReport.setPointsEarned(pointsEarned);
            baseMapper.updateById(existingReport);
            return existingReport;
        }

        SignInMonthlyReport report = new SignInMonthlyReport();
        report.setReportMonth(reportMonth);
        report.setUserId(userId);
        report.setTotalSignIns(totalSignIns);
        report.setContinuousDays(continuousDays);
        report.setPointsEarned(pointsEarned);
        baseMapper.insert(report);
        return report;
    }

    /**
     * 获取用户月度签到报告
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 月度签到报告对象
     */
    @Override
    public SignInMonthlyReport getMonthlyReport(Long userId, int year, int month) {
        String reportMonth = String.format("%04d-%02d", year, month);
        LambdaQueryWrapper<SignInMonthlyReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInMonthlyReport::getUserId, userId)
                .eq(SignInMonthlyReport::getReportMonth, reportMonth);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 获取用户月度签到记录统计
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 月度签到记录统计
     */
    @Override
    public Map<String, Object> getMonthlySignInRecord(Long userId, int year, int month) {
        Map<String, Object> record = new HashMap<>();
        // LocalDate startDate = LocalDate.of(year, month, 1);
        // LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        LocalDateTime startOfMonth = DateUtils.getStartOfMonth(year, month);
        LocalDateTime endOfMonth = DateUtils.getEndOfMonth(year, month);

        // 获取月度签到记录
        LambdaQueryWrapper<SignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignIn::getUserId, userId)
                // .ge(SignIn::getSignInDate, startDate)
                // .le(SignIn::getSignInDate, endDate)
                .ge(SignIn::getSignInDate, startOfMonth)
                .le(SignIn::getSignInDate, endOfMonth)
                .orderByAsc(SignIn::getSignInDate);
        List<SignIn> signInList = signInMapper.selectList(wrapper);

        // 提取签到日期
        Set<Integer> signedDays = new HashSet<>();
        int totalSignIns = signInList.size();
        int totalPoints = 0;
        int maxContinuousDays = 0;
        int currentContinuous = 0;
        LocalDate lastSignDate = null;

        for (SignIn signIn : signInList) {
            signedDays.add(signIn.getSignInDate().getDayOfMonth());
            totalPoints += signIn.getPointsReward();

            // 计算月度最大连续签到天数
            LocalDate currentSignDate = signIn.getSignInDate().toLocalDate();
            if (lastSignDate == null || ChronoUnit.DAYS.between(lastSignDate, currentSignDate) == 1) {
                currentContinuous = lastSignDate == null ? 1 : currentContinuous + 1;
            } else {
                currentContinuous = 1;
            }
            maxContinuousDays = Math.max(maxContinuousDays, currentContinuous);
            lastSignDate = currentSignDate;
        }

        record.put("year", year);
        record.put("month", month);
        record.put("signedDays", signedDays);
        record.put("totalSignIns", totalSignIns);
        record.put("totalPoints", totalPoints);
        record.put("maxContinuousDays", maxContinuousDays);
        // record.put("startDate", startDate);
        // record.put("endDate", endDate);
        // record.put("startDate", DateConstant.DATE_TIME_FORMATTER.format(startDate.atStartOfDay()));
        // record.put("endDate", DateConstant.DATE_TIME_FORMATTER.format(endDate.atTime(23, 59, 59)));
        record.put("startDate", DateConstant.DATE_TIME_FORMATTER.format(startOfMonth));
        record.put("endDate", DateConstant.DATE_TIME_FORMATTER.format(endOfMonth));

        return record;
    }
}
