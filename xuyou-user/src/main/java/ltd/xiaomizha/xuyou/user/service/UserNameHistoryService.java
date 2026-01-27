package ltd.xiaomizha.xuyou.user.service;

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
     * 根据用户ID获取用户名变更历史
     *
     * @param userId 用户ID
     * @return 用户名变更历史列表
     */
    List<UserNameHistory> getUserNameHistoriesByUserId(Integer userId);

}
