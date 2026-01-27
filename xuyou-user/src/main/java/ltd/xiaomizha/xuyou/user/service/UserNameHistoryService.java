package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserNameHistory;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_name_history(用户名变更历史表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserNameHistoryService extends IService<UserNameHistory> {

    /**
     * 分页获取用户名变更历史列表
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 分页用户名变更历史列表
     */
    Page<UserNameHistory> getHistoriesPage(Integer current, Integer pageSize);

    /**
     * 根据历史ID获取用户名变更历史详情
     *
     * @param historyId 历史ID
     * @return 用户名变更历史详情
     */
    UserNameHistory getHistoryById(Integer historyId);

    /**
     * 新增用户名变更历史
     *
     * @param userNameHistory 用户名变更历史信息
     * @return 是否新增成功
     */
    boolean addHistory(UserNameHistory userNameHistory);

    /**
     * 更新用户名变更历史
     *
     * @param historyId       历史ID
     * @param userNameHistory 用户名变更历史信息
     * @return 是否更新成功
     */
    boolean updateHistory(Integer historyId, UserNameHistory userNameHistory);

    /**
     * 删除用户名变更历史
     *
     * @param historyId 历史ID
     * @return 是否删除成功
     */
    boolean deleteHistory(Integer historyId);

    /**
     * 根据用户ID获取用户名变更历史
     *
     * @param userId 用户ID
     * @return 用户名变更历史列表
     */
    List<UserNameHistory> getUserNameHistoriesByUserId(Integer userId);

    /**
     * 根据用户ID获取用户名变更历史
     *
     * @param userId   用户ID
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 用户名变更历史列表
     */
    Page<UserNameHistory> getUserNameHistoriesByUserId(Integer userId, Integer current, Integer pageSize);

    /**
     * 批量添加用户名变更历史
     *
     * @param userNameHistoryList 用户名变更历史列表
     * @return 是否添加成功
     */
    boolean batchAddHistories(List<UserNameHistory> userNameHistoryList);

    /**
     * 批量更新用户名变更历史
     *
     * @param userNameHistoryList 用户名变更历史列表
     * @return 是否更新成功
     */
    boolean batchUpdateHistories(List<UserNameHistory> userNameHistoryList);

    /**
     * 批量删除用户名变更历史
     *
     * @param historyIds 历史ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteHistories(List<Integer> historyIds);

}
