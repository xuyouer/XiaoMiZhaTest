package ltd.xiaomizha.xuyou.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;
import ltd.xiaomizha.xuyou.user.entity.*;

/**
 * @author xiaom
 * @description 针对表【users(用户表)】的数据库操作Service
 * @createDate 2026-01-21 19:16:15
 */
public interface UsersService extends IService<Users> {

    /**
     * 注册用户
     *
     * @param users 用户信息
     * @return 注册结果
     */
    boolean registerUser(Users users);

    /**
     *
     * 用户登录
     *
     * @param username   用户名
     * @param password   密码
     * @param ipAddress  登录IP地址
     * @param userAgent  用户代理(浏览器信息)
     * @param deviceInfo 设备信息
     * @param loginType  登录类型
     * @return 登录结果
     */
    boolean loginUser(String username, String password, String ipAddress, String userAgent, String deviceInfo, LoginType loginType);

    /**
     * 修改用户
     *
     * @param users 用户信息
     * @return 修改结果
     */
    boolean updateUser(Users users);

    /**
     * 注销用户
     *
     * @param userId 用户ID
     * @return 注销结果
     */
    boolean logoutUser(Integer userId);

    /**
     * 获取用户账户状态
     *
     * @param userId 用户ID
     * @return 账户状态
     */
    Integer getAccountStatus(Integer userId);

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户详细信息DTO
     */
    UserDetailDTO getUserDetailById(Integer userId);

    /**
     * 获取用户反馈列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户反馈列表
     */
    Page<UserFeedback> getUserFeedbacks(Integer userId, Page<UserFeedback> page);

    /**
     * 获取用户日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户日志列表
     */
    Page<UserLogs> getUserLogs(Integer userId, Page<UserLogs> page);

    /**
     * 获取用户VIP日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户VIP日志列表
     */
    Page<UserVipLog> getUserVipLogs(Integer userId, Page<UserVipLog> page);

    /**
     * 获取用户VIP积分日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户VIP积分日志列表
     */
    Page<UserVipPointsLog> getUserVipPointsLogs(Integer userId, Page<UserVipPointsLog> page);

    /**
     * 获取用户登录记录列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户登录记录列表
     */
    Page<UserLoginRecords> getUserLoginRecords(Integer userId, Page<UserLoginRecords> page);

}
