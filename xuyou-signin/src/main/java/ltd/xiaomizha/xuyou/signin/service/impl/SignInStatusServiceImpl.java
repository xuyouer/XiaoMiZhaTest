package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.signin.entity.SignInStatus;
import ltd.xiaomizha.xuyou.signin.mapper.SignInStatusMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInStatusService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author xiaom
 * @description 针对表【sign_in_status(签到状态表)】的数据库操作Service实现
 * @createDate 2026-02-25 18:44:03
 */
@Service
public class SignInStatusServiceImpl extends ServiceImpl<SignInStatusMapper, SignInStatus>
        implements SignInStatusService {

    /**
     * 更新或创建签到状态
     *
     * @param userId         用户ID
     * @param continuousDays 连续签到天数
     * @param signInDateTime 签到时间
     * @return 签到状态对象
     */
    @Override
    public SignInStatus updateOrCreateSignInStatus(Long userId, int continuousDays, LocalDateTime signInDateTime) {
        LambdaQueryWrapper<SignInStatus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInStatus::getUserId, userId);
        SignInStatus signInStatus = baseMapper.selectOne(wrapper);

        if (signInStatus != null) {
            // 更新现有状态
            signInStatus.setCurrentContinuousDays(continuousDays);
            signInStatus.setLastSignInDate(signInDateTime);
            signInStatus.setIsContinuous(1); // 连续签到中
            signInStatus.setTotalSignIns(signInStatus.getTotalSignIns() + 1);
            signInStatus.setMaxContinuousDays(Math.max(signInStatus.getMaxContinuousDays(), continuousDays));
            baseMapper.updateById(signInStatus);
        } else {
            // 创建新状态
            signInStatus = new SignInStatus();
            signInStatus.setUserId(userId);
            signInStatus.setCurrentContinuousDays(continuousDays);
            signInStatus.setLastSignInDate(signInDateTime);
            signInStatus.setIsContinuous(1);
            signInStatus.setTotalSignIns(1);
            signInStatus.setMaxContinuousDays(continuousDays);
            baseMapper.insert(signInStatus);
        }

        return signInStatus;
    }

    /**
     * 获取用户签到状态
     *
     * @param userId 用户ID
     * @return 签到状态对象
     */
    @Override
    public SignInStatus getSignInStatusByUserId(Long userId) {
        LambdaQueryWrapper<SignInStatus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInStatus::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }
}
