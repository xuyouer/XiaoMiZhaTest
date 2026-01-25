package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserLogs;

/**
 * @author xiaom
 * @description 针对表【user_logs(用户操作日志表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserLogsService extends IService<UserLogs> {

    /**
     * 根据用户ID获取用户日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户日志列表
     */
    Page<UserLogs> getUserLogsByUserId(Integer userId, Page<UserLogs> page);

}
