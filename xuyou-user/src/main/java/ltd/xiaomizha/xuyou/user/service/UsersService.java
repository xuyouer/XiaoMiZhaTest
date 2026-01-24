package ltd.xiaomizha.xuyou.user.service;

import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.user.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
