package ltd.xiaomizha.xuyou.signin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_record(补签记录表)】的数据库操作Service
 * @createDate 2026-03-17 16:02:17
 */
public interface SignInRepairRecordService extends IService<SignInRepairRecord> {

    /**
     * 检查用户是否已在指定日期补签
     *
     * @param userId     用户ID
     * @param repairDate 补签日期
     * @return 是否已补签
     */
    boolean hasRepaired(Long userId, LocalDate repairDate);

    /**
     * 批量获取用户已补签的日期集合
     *
     * @param userId 用户ID
     * @param dates  待检查的日期列表
     * @return 已补签的日期集合
     */
    Set<LocalDate> batchGetRepairedDates(Long userId, List<LocalDate> dates);

    /**
     * 获取用户指定月份的补签日期列表
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份(1-12)
     * @return 补签日期列表
     */
    List<LocalDate> getRepairedDates(Long userId, int year, int month);

    /**
     * 分页获取用户补签记录列表
     *
     * @param userId   用户ID
     * @param current  当前页码
     * @param pageSize 每页数量
     * @return 补签记录分页数据
     */
    Page<Map<String, Object>> getRepairRecordList(Long userId, int current, int pageSize);

}
