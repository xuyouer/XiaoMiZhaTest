package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.enums.SystemConfigEnum;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.common.utils.user.UserUtils;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;
import ltd.xiaomizha.xuyou.user.entity.*;
import ltd.xiaomizha.xuyou.user.mapper.UsersMapper;
import ltd.xiaomizha.xuyou.user.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【users(用户表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Resource
    @Lazy
    private UserNamesService userNamesService;

    @Resource
    @Lazy
    private UserPointsService userPointsService;

    @Resource
    @Lazy
    private UserProfilesService userProfilesService;

    @Resource
    @Lazy
    private UserVipInfoService userVipInfoService;

    @Resource
    @Lazy
    private UserRoleRelationsService userRoleRelationsService;

    @Resource
    @Lazy
    private SystemConfigsService systemConfigsService;

    @Resource
    @Lazy
    private UserLoginRecordsService userLoginRecordsService;

    @Resource
    @Lazy
    private UserNameHistoryService userNameHistoryService;

    @Resource
    @Lazy
    private UserRolesService userRolesService;

    @Resource
    @Lazy
    private RoleResourceRelationsService roleResourceRelationsService;

    @Resource
    @Lazy
    private UserResourcesService userResourcesService;

    @Resource
    @Lazy
    private UserFeedbackService userFeedbackService;

    @Resource
    @Lazy
    private UserLogsService userLogsService;

    @Resource
    @Lazy
    private UserVipLogService userVipLogService;

    @Resource
    @Lazy
    private UserVipPointsLogService userVipPointsLogService;

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
        // 获取用户当前账户状态
        Integer currentStatus = getAccountStatus(userId);
        if (currentStatus == null) {
            log.error("用户不存在: userId = {}", userId);
            return false;
        }

        // 如果用户已经是禁用状态, 直接返回成功
        if (UserConstants.ACCOUNT_STATUS_DISABLED.equals(currentStatus)) {
            return true;
        }

        Users user = new Users();
        user.setUserId(userId);
        user.setAccountStatus(UserConstants.ACCOUNT_STATUS_DISABLED); // 账户状态设置为禁用
        return this.updateById(user);
    }

    /**
     * 获取用户账户状态
     *
     * @param userId 用户ID
     * @return 账户状态
     */
    @Override
    public Integer getAccountStatus(Integer userId) {
        Users user = this.getById(userId);
        return user != null ? user.getAccountStatus() : null;
    }

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return 用户详细信息DTO
     */
    @Override
    public UserDetailDTO getUserDetailById(Integer userId) {
        UserDetailDTO userDetailDTO = new UserDetailDTO();

        // 获取用户基本信息
        Users user = this.getById(userId);
        if (user == null) {
            return null;
        }
        userDetailDTO.setUser(user);

        // 获取用户资料
        UserProfiles userProfile = userProfilesService.getUserProfileByUserId(userId);
        userDetailDTO.setUserProfile(userProfile);

        // 获取用户名信息
        UserNames userNames = userNamesService.getUserNameByUserId(userId);
        userDetailDTO.setUserNames(userNames);

        // 获取用户名变更历史
        List<UserNameHistory> userNameHistories = userNameHistoryService.getUserNameHistoriesByUserId(userId);
        userDetailDTO.setUserNameHistories(userNameHistories);

        // 获取用户积分信息
        UserPoints userPoints = userPointsService.getUserPointsByUserId(userId);
        userDetailDTO.setUserPoints(userPoints);

        // 获取用户VIP信息
        UserVipInfo userVipInfo = userVipInfoService.getUserVipInfoByUserId(userId);
        userDetailDTO.setUserVipInfo(userVipInfo);

        // 获取用户角色列表
        List<UserRoles> userRoles = userRoleRelationsService.getUserRolesByUserId(userId);
        userDetailDTO.setUserRoles(userRoles);

        // 获取用户角色下的资源列表
        List<UserResources> userResources = userResourcesService.getUserResourcesByUserId(userId);
        userDetailDTO.setUserResources(userResources);

        return userDetailDTO;
    }

    /**
     * 获取用户反馈列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户反馈列表
     */
    @Override
    public Page<UserFeedback> getUserFeedbacks(Integer userId, Page<UserFeedback> page) {
        return userFeedbackService.getUserFeedbacksByUserId(userId, page);
    }

    /**
     * 获取用户日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户日志列表
     */
    @Override
    public Page<UserLogs> getUserLogs(Integer userId, Page<UserLogs> page) {
        return userLogsService.getUserLogsByUserId(userId, page);
    }

    /**
     * 获取用户VIP日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户VIP日志列表
     */
    @Override
    public Page<UserVipLog> getUserVipLogs(Integer userId, Page<UserVipLog> page) {
        return userVipLogService.getUserVipLogsByUserId(userId, page);
    }

    /**
     * 获取用户VIP积分日志列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户VIP积分日志列表
     */
    @Override
    public Page<UserVipPointsLog> getUserVipPointsLogs(Integer userId, Page<UserVipPointsLog> page) {
        return userVipPointsLogService.getUserVipPointsLogsByUserId(userId, page);
    }

    /**
     * 获取用户登录记录列表
     *
     * @param userId 用户ID
     * @param page   分页对象
     * @return 用户登录记录列表
     */
    @Override
    public Page<UserLoginRecords> getUserLoginRecords(Integer userId, Page<UserLoginRecords> page) {
        return userLoginRecordsService.getUserLoginRecordsByUserId(userId, page);
    }

    /**
     * 根据用户名获取用户ID
     *
     * @param username 用户名
     * @return 用户ID
     */
    @Override
    public Integer getUserIdByUsername(String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.select("user_id");
        Users user = this.getOne(queryWrapper);
        return user != null ? user.getUserId() : null;
    }
    
}
