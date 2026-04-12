package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairRecord;
import ltd.xiaomizha.xuyou.signin.mapper.SignInRepairRecordMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_record(补签记录表)】的数据库操作Service实现
 * @createDate 2026-03-17 16:02:17
 */
@Service
public class SignInRepairRecordServiceImpl extends ServiceImpl<SignInRepairRecordMapper, SignInRepairRecord>
        implements SignInRepairRecordService {

    /**
     * 检查用户是否已在指定日期补签
     *
     * @param userId     用户ID
     * @param repairDate 补签日期
     * @return 是否已补签
     */
    @Override
    public boolean hasRepaired(Long userId, LocalDate repairDate) {
        LambdaQueryWrapper<SignInRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairRecord::getUserId, userId)
                .eq(SignInRepairRecord::getRepairDate, repairDate)
                .eq(SignInRepairRecord::getStatus, Status.SUCCESS);
        return count(wrapper) > 0;
    }

    /**
     * 批量获取用户已补签的日期集合
     *
     * @param userId 用户ID
     * @param dates  待检查的日期列表
     * @return 已补签的日期集合
     */
    @Override
    public Set<LocalDate> batchGetRepairedDates(Long userId, List<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            return Collections.emptySet();
        }

        LocalDate minDate = dates.stream().min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate maxDate = dates.stream().max(LocalDate::compareTo).orElse(LocalDate.now());

        LambdaQueryWrapper<SignInRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairRecord::getUserId, userId)
                .ge(SignInRepairRecord::getRepairDate, minDate)
                .le(SignInRepairRecord::getRepairDate, maxDate)
                .eq(SignInRepairRecord::getStatus, Status.SUCCESS)
                .select(SignInRepairRecord::getRepairDate);

        return list(wrapper).stream()
                .map(SignInRepairRecord::getRepairDate)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户指定月份的补签日期列表
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份(1-12)
     * @return 补签日期列表
     */
    @Override
    public List<LocalDate> getRepairedDates(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        LambdaQueryWrapper<SignInRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairRecord::getUserId, userId)
                .ge(SignInRepairRecord::getRepairDate, startDate)
                .le(SignInRepairRecord::getRepairDate, endDate)
                .eq(SignInRepairRecord::getStatus, Status.SUCCESS)
                .select(SignInRepairRecord::getRepairDate);

        return list(wrapper).stream()
                .map(SignInRepairRecord::getRepairDate)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取用户补签记录列表
     *
     * @param userId   用户ID
     * @param current  当前页码
     * @param pageSize 每页数量
     * @return 补签记录分页数据
     */
    @Override
    public Page<Map<String, Object>> getRepairRecordList(Long userId, int current, int pageSize) {
        LambdaQueryWrapper<SignInRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairRecord::getUserId, userId)
                .orderByDesc(SignInRepairRecord::getCreatedAt);

        Page<SignInRepairRecord> resultPage = page(new Page<>(current, pageSize), wrapper);

        List<Map<String, Object>> records = resultPage.getRecords().stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("recordId", record.getRecordId());
                    map.put("repairDate", record.getRepairDate());
                    map.put("cardType", record.getCardType());
                    map.put("continuousDaysBefore", record.getContinuousDaysBefore());
                    map.put("continuousDaysAfter", record.getContinuousDaysAfter());
                    map.put("pointsReward", record.getPointsReward());
                    map.put("status", record.getStatus());
                    map.put("createdAt", record.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> mapPage = new Page<>(current, pageSize, resultPage.getTotal());
        mapPage.setRecords(records);
        return mapPage;
    }
}




