package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.SystemConfigEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.common.utils.user.UserUtils;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UsersMapper;
import ltd.xiaomizha.xuyou.user.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiaom
 * @description 针对表【users(用户表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Resource
    private UserNamesService userNamesService;

    @Resource
    private UserPointsService userPointsService;

    @Resource
    private UserProfilesService userProfilesService;

    @Resource
    private UserVipInfoService userVipInfoService;

    @Resource
    private UserRoleRelationsService userRoleRelationsService;

    @Resource
    private SystemConfigsService systemConfigsService;

    @Resource
    private UserLoginRecordsService userLoginRecordsService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean registerUser(Users users) {
        // 验证用户名唯一性
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", users.getUsername());
        if (this.count(queryWrapper) > 0) return false;

        // 密码加密
        String passwordHash = UserUtils.encryptPassword(users.getPasswordHash());
        users.setPasswordHash(passwordHash);
        users.setAccountStatus(UserConstants.ACCOUNT_STATUS_NORMAL); // 账户状态默认正常

        // 保存用户信息
        boolean result = this.save(users);
        if (!result) return false;

        // 调用各Service的createDefault方法添加对应记录
        if (!userNamesService.createDefaultUserName(users.getUserId(), users.getUsername())) {
            throw new RuntimeException("添加用户名信息失败");
        }

        if (!userPointsService.createDefaultUserPoints(users.getUserId())) {
            throw new RuntimeException("添加用户积分信息失败");
        }

        if (!userProfilesService.createDefaultUserProfile(users.getUserId(), users.getUsername())) {
            throw new RuntimeException("添加用户资料信息失败");
        }

        if (!userVipInfoService.createDefaultUserVipInfo(users.getUserId())) {
            throw new RuntimeException("添加用户会员信息失败");
        }

        if (!userRoleRelationsService.createDefaultUserRoleRelation(users.getUserId())) {
            throw new RuntimeException("添加用户角色信息失败");
        }

        return true;
    }

    @Override
    public boolean loginUser(String username, String password, String ipAddress, String userAgent, String deviceInfo, LoginType loginType) {
        // 查询用户
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Users users = this.getOne(queryWrapper);
        if (users == null) {
            // 记录登录失败
            // userLoginRecordsService.addLoginRecord(null, ipAddress, userAgent, deviceInfo, loginType, 0, ResultEnum.USER_NOT_FOUND.getZhMsg());
            return false;
        }

        // 检查账户状态
        if (!UserConstants.ACCOUNT_STATUS_NORMAL.equals(users.getAccountStatus())) {
            // 记录登录失败
            userLoginRecordsService.addLoginRecord(users.getUserId(), ipAddress, userAgent, deviceInfo, loginType, 0, ResultEnum.USER_DISABLED.getZhMsg());
            return false;
        }

        // 验证密码
        boolean passwordMatch = UserUtils.verifyPassword(password, users.getPasswordHash());

        if (passwordMatch) {
            // 检查是否首次登录
            if (userLoginRecordsService.isFirstLogin(users.getUserId())) {
                // 激活user_vip_info
                userVipInfoService.activateUserVipInfo(users.getUserId());

                // 激活user_role_relations
                userRoleRelationsService.activateUserRoleRelation(users.getUserId());
            }

            // 记录登录成功
            userLoginRecordsService.addLoginRecord(users.getUserId(), ipAddress, userAgent, deviceInfo, loginType, 1, null);
        } else {
            // 记录登录失败
            userLoginRecordsService.addLoginRecord(users.getUserId(), ipAddress, userAgent, deviceInfo, loginType, 0, ResultEnum.PASSWORD_ERROR.getZhMsg());
        }

        return passwordMatch;
    }

    /**
     * 修改用户
     *
     * @param users 用户信息
     * @return 修改结果
     */
    @Override
    public boolean updateUser(Users users) {
        // 从系统配置中获取是否允许更新登录名
        boolean allowUpdateUsername = Boolean.parseBoolean(systemConfigsService.getConfigValueByKey(
                SystemConfigEnum.ALLOW_UPDATE_USERNAME.getKey(),
                SystemConfigEnum.ALLOW_UPDATE_USERNAME.getDefaultValue()
        ));

        if (allowUpdateUsername) {
            // 允许更新所有字段
            return this.updateById(users);
        } else {
            // 不允许更新登录名, 只能修改密码
            if (users.getPasswordHash() != null) {
                // 密码加密
                String passwordHash = UserUtils.encryptPassword(users.getPasswordHash());
                users.setPasswordHash(passwordHash);

                // 只更新密码字段
                return this.updateById(users);
            }
            return false;
        }
    }

    /**
     * 注销用户
     *
     * @param userId 用户ID
     * @return 注销结果
     */
    @Override
    public boolean logoutUser(Integer userId) {
        Users user = new Users();
        user.setUserId(userId);
        user.setAccountStatus(UserConstants.ACCOUNT_STATUS_DISABLED); // 账户状态设置为禁用
        return this.updateById(user);
    }
}




