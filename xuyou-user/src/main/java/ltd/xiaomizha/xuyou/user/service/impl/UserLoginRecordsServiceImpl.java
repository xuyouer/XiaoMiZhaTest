package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.user.entity.UserLoginRecords;
import ltd.xiaomizha.xuyou.user.mapper.UserLoginRecordsMapper;
import ltd.xiaomizha.xuyou.user.service.UserLoginRecordsService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_login_records(用户登录记录表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:14
 */
@Service
public class UserLoginRecordsServiceImpl extends ServiceImpl<UserLoginRecordsMapper, UserLoginRecords>
        implements UserLoginRecordsService {

    @Override
    public boolean addLoginRecord(Integer userId, String ipAddress, String userAgent, String deviceInfo, LoginType loginType, Integer loginStatus, String failureReason) {
        // 添加login_records记录
        UserLoginRecords loginRecord = new UserLoginRecords();
        loginRecord.setUserId(userId);
        loginRecord.setIpAddress(ipAddress);
        loginRecord.setUserAgent(userAgent);
        loginRecord.setDeviceInfo(deviceInfo);
        loginRecord.setLoginType(loginType);
        loginRecord.setLoginStatus(loginStatus);
        loginRecord.setFailureReason(failureReason);

        return this.save(loginRecord);
    }

    @Override
    public boolean isFirstLogin(Integer userId) {
        // 检查用户登录记录是否存在
        QueryWrapper<UserLoginRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.count(queryWrapper) == 0;
    }

    @Override
    public Page<UserLoginRecords> getUserLoginRecordsByUserId(Integer userId, Page<UserLoginRecords> page) {
        QueryWrapper<UserLoginRecords> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }
 
}
