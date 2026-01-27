package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;
import ltd.xiaomizha.xuyou.user.entity.UserVipInfo;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserVipInfoMapper;
import ltd.xiaomizha.xuyou.user.service.UserVipInfoService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

/**
 * @author xiaom
 * @description 针对表【user_vip_info(用户会员信息表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
@Slf4j
public class UserVipInfoServiceImpl extends ServiceImpl<UserVipInfoMapper, UserVipInfo>
        implements UserVipInfoService {

    @Resource
    private UsersService usersService;

    @Override
    public Page<UserVipInfo> getVipInfosPage(Integer current, Integer pageSize) {
        Page<UserVipInfo> page = new Page<>(current, pageSize);
        QueryWrapper<UserVipInfo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("vip_level", "created_at");
        return this.page(page, wrapper);
    }

    @Override
    public UserVipInfo getVipInfoById(Integer vipId) {
        if (vipId == null || vipId <= 0) {
            throw new IllegalArgumentException("VIP ID不能为空且必须大于0");
        }
        UserVipInfo vipInfo = this.getById(vipId);
        if (vipInfo == null) {
            throw new RuntimeException("用户VIP信息不存在");
        }
        return vipInfo;
    }

    @Override
    public boolean addVipInfo(UserVipInfo userVipInfo) {
        if (userVipInfo == null) {
            throw new IllegalArgumentException("用户VIP信息不能为空");
        }
        // 验证用户是否存在
        Integer userId = userVipInfo.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证是否已存在VIP信息
        UserVipInfo existingVipInfo = this.getUserVipInfoByUserId(userId);
        if (existingVipInfo != null) {
            throw new RuntimeException("用户VIP信息已存在");
        }
        // 验证VIP等级
        if (userVipInfo.getVipLevel() < 0) {
            throw new IllegalArgumentException("VIP等级不能为负数");
        }
        // 验证VIP积分
        if (userVipInfo.getVipPoints() < 0) {
            throw new IllegalArgumentException("VIP成长值不能为负数");
        }
        if (userVipInfo.getTotalEarnedPoints() < 0) {
            throw new IllegalArgumentException("累计获得成长值不能为负数");
        }
        if (userVipInfo.getPointsToday() < 0) {
            throw new IllegalArgumentException("今日已获得成长值不能为负数");
        }
        if (userVipInfo.getPointsThisMonth() < 0) {
            throw new IllegalArgumentException("本月已获得成长值不能为负数");
        }
        // 验证累计获得成长值不能小于当前VIP积分
        if (userVipInfo.getTotalEarnedPoints() < userVipInfo.getVipPoints()) {
            throw new IllegalArgumentException("累计获得成长值不能小于当前VIP积分");
        }
        // 验证充值金额相关字段
        if (userVipInfo.getTotalRechargeAmount() != null && userVipInfo.getTotalRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("累计充值金额不能为负数");
        }
        if (userVipInfo.getLastRechargeAmount() != null && userVipInfo.getLastRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("最后充值金额不能为负数");
        }
        // 验证日期字段合理性
        if (userVipInfo.getVipExpireDate() != null && userVipInfo.getLevelExpireDate() != null) {
            if (userVipInfo.getVipExpireDate().before(userVipInfo.getLevelExpireDate())) {
                throw new IllegalArgumentException("VIP到期日期不能早于等级有效期");
            }
        }
        return this.save(userVipInfo);
    }

    @Override
    public boolean updateVipInfo(Integer vipId, UserVipInfo userVipInfo) {
        if (vipId == null || vipId <= 0) {
            throw new IllegalArgumentException("VIP ID不能为空且必须大于0");
        }
        if (userVipInfo == null) {
            throw new IllegalArgumentException("用户VIP信息不能为空");
        }
        // 验证VIP信息是否存在
        if (!this.existsById(vipId)) {
            throw new RuntimeException("用户VIP信息不存在");
        }
        // 验证用户是否存在
        Integer userId = userVipInfo.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            // 验证用户是否已存在其他VIP信息记录
            QueryWrapper<UserVipInfo> existingWrapper = new QueryWrapper<>();
            existingWrapper.eq("user_id", userId)
                    .ne("vip_id", vipId);
            if (this.count(existingWrapper) > 0) {
                throw new RuntimeException("该用户已存在其他VIP信息记录");
            }
        }
        // 验证VIP等级
        if (userVipInfo.getVipLevel() != null && userVipInfo.getVipLevel() < 0) {
            throw new IllegalArgumentException("VIP等级不能为负数");
        }
        // 验证VIP积分
        if (userVipInfo.getVipPoints() != null && userVipInfo.getVipPoints() < 0) {
            throw new IllegalArgumentException("VIP成长值不能为负数");
        }
        if (userVipInfo.getTotalEarnedPoints() != null && userVipInfo.getTotalEarnedPoints() < 0) {
            throw new IllegalArgumentException("累计获得成长值不能为负数");
        }
        if (userVipInfo.getPointsToday() != null && userVipInfo.getPointsToday() < 0) {
            throw new IllegalArgumentException("今日已获得成长值不能为负数");
        }
        if (userVipInfo.getPointsThisMonth() != null && userVipInfo.getPointsThisMonth() < 0) {
            throw new IllegalArgumentException("本月已获得成长值不能为负数");
        }
        // 验证累计获得成长值不能小于当前VIP积分
        UserVipInfo currentVipInfo = this.getById(vipId);
        int vipPoints = userVipInfo.getVipPoints() != null ? userVipInfo.getVipPoints() : currentVipInfo.getVipPoints();
        int totalEarnedPoints = userVipInfo.getTotalEarnedPoints() != null ? userVipInfo.getTotalEarnedPoints() : currentVipInfo.getTotalEarnedPoints();
        if (totalEarnedPoints < vipPoints) {
            throw new IllegalArgumentException("累计获得成长值不能小于当前VIP积分");
        }
        // 验证充值金额相关字段
        if (userVipInfo.getTotalRechargeAmount() != null && userVipInfo.getTotalRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("累计充值金额不能为负数");
        }
        if (userVipInfo.getLastRechargeAmount() != null && userVipInfo.getLastRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("最后充值金额不能为负数");
        }
        // 验证日期字段合理性
        if (userVipInfo.getVipExpireDate() != null && userVipInfo.getLevelExpireDate() != null) {
            if (userVipInfo.getVipExpireDate().before(userVipInfo.getLevelExpireDate())) {
                throw new IllegalArgumentException("VIP到期日期不能早于等级有效期");
            }
        }
        userVipInfo.setVipId(vipId);
        return this.updateById(userVipInfo);
    }

    @Override
    public boolean deleteVipInfo(Integer vipId) {
        if (vipId == null || vipId <= 0) {
            throw new IllegalArgumentException("VIP ID不能为空且必须大于0");
        }
        // 验证VIP信息是否存在
        if (!this.existsById(vipId)) {
            throw new RuntimeException("用户VIP信息不存在");
        }
        return this.removeById(vipId);
    }

    @Override
    public boolean createDefaultUserVipInfo(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证是否已存在VIP信息
        UserVipInfo existingVipInfo = this.getUserVipInfoByUserId(userId);
        if (existingVipInfo != null) {
            throw new RuntimeException("用户VIP信息已存在");
        }
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
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 激活user_vip_info
        QueryWrapper<UserVipInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserVipInfo userVipInfo = this.getOne(queryWrapper);
        if (userVipInfo == null) {
            throw new RuntimeException("用户VIP信息不存在");
        }
        userVipInfo.setVipStatus(Status.ACTIVE);

        return this.updateById(userVipInfo);
    }

    @Override
    public UserVipInfo getUserVipInfoByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserVipInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean batchAddVipInfos(List<UserVipInfo> userVipInfoList) {
        if (userVipInfoList == null || userVipInfoList.isEmpty()) {
            throw new IllegalArgumentException("用户VIP信息列表不能为空");
        }
        for (UserVipInfo vipInfo : userVipInfoList) {
            if (vipInfo == null) {
                throw new IllegalArgumentException("用户VIP信息列表中包含空记录");
            }
            // 验证用户是否存在
            Integer userId = vipInfo.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
            // 验证是否已存在VIP信息
            UserVipInfo existingVipInfo = this.getUserVipInfoByUserId(userId);
            if (existingVipInfo != null) {
                throw new RuntimeException("用户VIP信息已存在: 用户ID " + userId);
            }
            // 验证VIP等级
            if (vipInfo.getVipLevel() < 0) {
                throw new IllegalArgumentException("VIP等级不能为负数: 用户ID " + userId);
            }
            // 验证VIP积分
            if (vipInfo.getVipPoints() < 0) {
                throw new IllegalArgumentException("VIP成长值不能为负数: 用户ID " + userId);
            }
            if (vipInfo.getTotalEarnedPoints() < 0) {
                throw new IllegalArgumentException("累计获得成长值不能为负数: 用户ID " + userId);
            }
            if (vipInfo.getPointsToday() < 0) {
                throw new IllegalArgumentException("今日已获得成长值不能为负数: 用户ID " + userId);
            }
            if (vipInfo.getPointsThisMonth() < 0) {
                throw new IllegalArgumentException("本月已获得成长值不能为负数: 用户ID " + userId);
            }
            // 验证充值金额相关字段
            if (vipInfo.getTotalRechargeAmount() != null && vipInfo.getTotalRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("累计充值金额不能为负数: 用户ID " + userId);
            }
            if (vipInfo.getLastRechargeAmount() != null && vipInfo.getLastRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("最后充值金额不能为负数: 用户ID " + userId);
            }
        }
        return this.saveBatch(userVipInfoList);
    }

    @Override
    public boolean batchUpdateVipInfos(List<UserVipInfo> userVipInfoList) {
        if (userVipInfoList == null || userVipInfoList.isEmpty()) {
            throw new IllegalArgumentException("用户VIP信息列表不能为空");
        }
        for (UserVipInfo vipInfo : userVipInfoList) {
            if (vipInfo == null || vipInfo.getVipId() == null || vipInfo.getVipId() <= 0) {
                throw new IllegalArgumentException("用户VIP信息列表中包含无效记录");
            }
            // 验证VIP信息是否存在
            if (!this.existsById(vipInfo.getVipId())) {
                throw new RuntimeException("用户VIP信息不存在: " + vipInfo.getVipId());
            }
            // 验证用户是否存在
            Integer userId = vipInfo.getUserId();
            if (userId != null) {
                if (userId <= 0) {
                    throw new IllegalArgumentException("用户ID必须大于0");
                }
                Users user = usersService.getById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在: " + userId);
                }
            }
            // 验证VIP等级
            if (vipInfo.getVipLevel() != null && vipInfo.getVipLevel() < 0) {
                throw new IllegalArgumentException("VIP等级不能为负数");
            }
            // 验证VIP积分
            if (vipInfo.getVipPoints() != null && vipInfo.getVipPoints() < 0) {
                throw new IllegalArgumentException("VIP成长值不能为负数");
            }
            if (vipInfo.getTotalEarnedPoints() != null && vipInfo.getTotalEarnedPoints() < 0) {
                throw new IllegalArgumentException("累计获得成长值不能为负数");
            }
            if (vipInfo.getPointsToday() != null && vipInfo.getPointsToday() < 0) {
                throw new IllegalArgumentException("今日已获得成长值不能为负数");
            }
            if (vipInfo.getPointsThisMonth() != null && vipInfo.getPointsThisMonth() < 0) {
                throw new IllegalArgumentException("本月已获得成长值不能为负数");
            }
            // 验证充值金额相关字段
            if (vipInfo.getTotalRechargeAmount() != null && vipInfo.getTotalRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("累计充值金额不能为负数");
            }
            if (vipInfo.getLastRechargeAmount() != null && vipInfo.getLastRechargeAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("最后充值金额不能为负数");
            }
        }
        return this.updateBatchById(userVipInfoList);
    }

    @Override
    public boolean batchDeleteVipInfos(List<Integer> vipIds) {
        if (vipIds == null || vipIds.isEmpty()) {
            throw new IllegalArgumentException("VIP ID列表不能为空");
        }
        // 验证列表中的VIP信息是否都存在
        for (Integer vipId : vipIds) {
            if (vipId == null || vipId <= 0) {
                throw new IllegalArgumentException("VIP ID列表中包含无效ID");
            }
            if (!this.existsById(vipId)) {
                throw new RuntimeException("用户VIP信息不存在: " + vipId);
            }
        }
        return this.removeByIds(vipIds);
    }

    private boolean existsById(Integer vipId) {
        if (vipId == null) {
            return false;
        }
        return this.getById(vipId) != null;
    }

}