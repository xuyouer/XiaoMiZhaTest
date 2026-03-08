package ltd.xiaomizha.xuyou.signin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.xiaomizha.xuyou.signin.entity.SignIn;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author xiaom
 * @description 针对表【sign_in(签到表)】的数据库操作Mapper
 * @createDate 2026-02-25 18:44:03
 * @Entity ltd.xiaomizha.xuyou.signin.entity.SignIn
 */
public interface SignInMapper extends BaseMapper<SignIn> {

    /**
     * 查询本月签到次数排行榜
     *
     * @param startOfMonth 本月开始时间
     * @param endOfMonth   本月结束时间
     * @param limit        返回数量
     * @return 本月签到次数排行榜
     */
    @MapKey("userId")
    List<Map<String, Object>> selectMonthlySignInsRanking(
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth,
            @Param("limit") int limit
    );

    /**
     * 分页查询用户签到列表
     *
     * @param userId    用户ID
     * @param userName  用户名
     * @param year      年份
     * @param month     月份
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @param offset    偏移量
     * @param pageSize  每页数量
     * @return 用户签到列表
     */
    List<Map<String, Object>> selectUserSignInList(
            @Param("userId") Long userId,
            @Param("userName") String userName,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("offset") long offset,
            @Param("pageSize") long pageSize
    );

    /**
     * 查询用户签到列表总数
     *
     * @param userId   用户ID
     * @param userName 用户名
     * @param year     年份
     * @param month    月份
     * @return 总数
     */
    long countUserSignInList(
            @Param("userId") Long userId,
            @Param("userName") String userName,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

}




