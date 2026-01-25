package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.user.entity.UserVipLog;

/**
 * @author xiaom
 * @description 针对表【user_vip_log(用户会员变更记录表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserVipLogService extends IService<UserVipLog> {

    /**
     * 根据用户ID获取用户VIP日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户VIP日志列表
     */
    Page<UserVipLog> getUserVipLogsByUserId(Integer userId, Page<UserVipLog> page);

}
