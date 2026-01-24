package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.user.entity.UserVipInfo;
import ltd.xiaomizha.xuyou.user.mapper.UserVipInfoMapper;
import ltd.xiaomizha.xuyou.user.service.UserVipInfoService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_vip_info(用户会员信息表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
@Slf4j
public class UserVipInfoServiceImpl extends ServiceImpl<UserVipInfoMapper, UserVipInfo>
        implements UserVipInfoService {

    @Override
    public boolean createDefaultUserVipInfo(Integer userId) {
        // 添加user_vip_info记录
        UserVipInfo userVipInfo = new UserVipInfo();
        userVipInfo.setUserId(userId);
        userVipInfo.setVipLevel(UserConstants.DEFAULT_VIP_LEVEL); // 默认普通用户
        userVipInfo.setVipPoints(UserConstants.DEFAULT_POINTS); // 默认VIP成长值
        userVipInfo.setTotalEarnedPoints(UserConstants.DEFAULT_POINTS); // 默认累计获得成长值
        userVipInfo.setPointsToday(UserConstants.DEFAULT_POINTS); // 默认今日已获得成长值
        userVipInfo.setPointsThisMonth(UserConstants.DEFAULT_POINTS); // 默认本月已获得成长值
        userVipInfo.setVipStatus(Status.INACTIVE); // 默认未激活, 需登录一次进行激活ACTIVE

        return this.save(userVipInfo);
    }

    @Override
    public boolean activateUserVipInfo(Integer userId) {
        // 激活user_vip_info
        // UserVipInfo userVipInfo = new UserVipInfo();
        // userVipInfo.setUserId(userId);
        QueryWrapper<UserVipInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserVipInfo userVipInfo = this.getOne(queryWrapper);
        userVipInfo.setVipStatus(Status.ACTIVE);

        return this.updateById(userVipInfo);
    }

}




