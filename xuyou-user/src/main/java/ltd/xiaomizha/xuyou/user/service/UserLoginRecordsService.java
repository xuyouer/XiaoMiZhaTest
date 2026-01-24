package ltd.xiaomizha.xuyou.user.service;

import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.user.entity.UserLoginRecords;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author xiaom
 * @description 针对表【user_login_records(用户登录记录表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UserLoginRecordsService extends IService<UserLoginRecords> {

    /**
     * 添加登录记录
     *
     * @param userId        用户ID
     * @param ipAddress     登录IP地址
     * @param userAgent     用户代理
     * @param deviceInfo    设备信息
     * @param loginType     登录类型
     * @param loginStatus   登录状态(1成功/0失败)
     * @param failureReason 失败原因
     * @return 是否添加成功
     */
    boolean addLoginRecord(Integer userId, String ipAddress, String userAgent, String deviceInfo, LoginType loginType, Integer loginStatus, String failureReason);

    /**
     * 检查是否首次登录
     *
     * @param userId 用户ID
     * @return 是否首次登录
     */
    boolean isFirstLogin(Integer userId);

}
