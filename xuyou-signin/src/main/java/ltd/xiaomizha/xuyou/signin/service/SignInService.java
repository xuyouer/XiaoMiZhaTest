package ltd.xiaomizha.xuyou.signin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.signin.entity.SignIn;

import java.util.Map;

/**
 * @author xiaom
 * @description 针对表【sign_in(签到表)】的数据库操作Service
 * @createDate 2026-02-25 18:44:03
 */
public interface SignInService extends IService<SignIn> {

    /**
     * 用户签到
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    Map<String, Object> signIn(Long userId);

    /**
     * 检查用户今日是否已签到
     *
     * @param userId 用户ID
     * @return 是否已签到
     */
    boolean checkTodaySignIn(Long userId);

    /**
     * 获取用户连续签到天数
     *
     * @param userId 用户ID
     * @return 连续签到天数
     */
    int getContinuousSignInDays(Long userId);

    /**
     * 获取用户签到状态
     *
     * @param userId 用户ID
     * @return 签到状态
     */
    Map<String, Object> getSignInStatus(Long userId);

    /**
     * 获取用户月度签到记录
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 月度签到记录
     */
    Map<String, Object> getMonthlySignInRecord(Long userId, int year, int month);

    /**
     * 获取签到排行榜
     *
     * @param type  排行榜类型: 1-总签到次数, 2-连续签到天数, 3-本月签到次数
     * @param limit 返回数量
     * @return 签到排行榜
     */
    Map<String, Object> getSignInRanking(int type, int limit);

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
    Page<Map<String, Object>> getUserSignInList(long current, long pageSize, Long userId, String userName, Integer year, Integer month, String sortField, String sortOrder);

}
