package ltd.xiaomizha.xuyou.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.SystemConfigEnum;
import ltd.xiaomizha.xuyou.user.entity.*;
import ltd.xiaomizha.xuyou.user.mapper.UsersMapper;
import ltd.xiaomizha.xuyou.user.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiaom
 * @description 针对表【users(用户表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
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
    private SystemConfigsService systemConfigsService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean registerUser(Users users) {
        // 验证用户名唯一性
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", users.getUsername());
        if (this.count(queryWrapper) > 0) {
            return false;
        }

        // 密码加密
        String passwordHash = BCrypt.hashpw(users.getPasswordHash());
        users.setPasswordHash(passwordHash);
        users.setAccountStatus(UserConstants.ACCOUNT_STATUS_NORMAL); // 账户状态默认正常

        // 保存用户信息
        boolean result = this.save(users);
        if (!result) {
            return false;
        }

        // 生成唯一create_name
        String createName = UserConstants.CREATE_NAME_PREFIX + IdUtil.fastSimpleUUID().substring(0, 16);

        // 添加user_names记录
        UserNames userNames = new UserNames();
        userNames.setUserId(users.getUserId());
        userNames.setCreateName(createName);
        userNames.setDisplayName(users.getUsername()); // display_name暂时为用户登录名
        userNames.setIsDefaultDisplay(UserConstants.IS_DEFAULT_DISPLAY_YES);
        result = userNamesService.save(userNames);
        if (!result) {
            throw new RuntimeException("添加用户名信息失败");
        }

        // 添加user_points记录
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(users.getUserId());
        userPoints.setTotalPoints(0);
        userPoints.setAvailablePoints(0);
        userPoints.setFrozenPoints(0);
        userPoints.setConsumedPoints(0);
        result = userPointsService.save(userPoints);
        if (!result) {
            throw new RuntimeException("添加用户积分信息失败");
        }

        // 添加user_profiles记录
        UserProfiles userProfiles = new UserProfiles();
        userProfiles.setUserId(users.getUserId());
        userProfiles.setNickname(users.getUsername());
        result = userProfilesService.save(userProfiles);
        if (!result) {
            throw new RuntimeException("添加用户资料信息失败");
        }

        // 添加user_vip_info记录
        UserVipInfo userVipInfo = new UserVipInfo();
        userVipInfo.setUserId(users.getUserId());
        userVipInfo.setVipLevel(0); // 默认普通用户
        userVipInfo.setVipPoints(0);
        userVipInfo.setTotalEarnedPoints(0);
        userVipInfo.setPointsToday(0);
        userVipInfo.setPointsThisMonth(0);
        userVipInfo.setVipStatus("INACTIVE");
        result = userVipInfoService.save(userVipInfo);
        if (!result) {
            throw new RuntimeException("添加用户会员信息失败");
        }

        return true;
    }

    @Override
    public boolean loginUser(String username, String password) {
        // 查询用户
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Users users = this.getOne(queryWrapper);
        if (users == null) {
            return false;
        }

        // 验证密码
        return BCrypt.checkpw(password, users.getPasswordHash());
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
                String passwordHash = BCrypt.hashpw(users.getPasswordHash());
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




