package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.signin.entity.SignInConfig;
import ltd.xiaomizha.xuyou.signin.mapper.SignInConfigMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【sign_in_config(签到配置表)】的数据库操作Service实现
 * @createDate 2026-02-25 18:44:03
 */
@Slf4j
@Service
public class SignInConfigServiceImpl extends ServiceImpl<SignInConfigMapper, SignInConfig>
        implements SignInConfigService {

    /**
     * 根据连续签到天数获取签到奖励配置
     *
     * @param continuousDays 连续签到天数
     * @return 签到配置信息
     */
    @Override
    public SignInConfig getSignInConfigByContinuousDays(int continuousDays) {
        LambdaQueryWrapper<SignInConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInConfig::getContinuousDays, continuousDays)
                .eq(SignInConfig::getIsActive, 1);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 计算签到积分奖励
     *
     * @param continuousDays 连续签到天数
     * @return 积分奖励
     */
    @Override
    public int calculateSignInReward(int continuousDays) {
        // // 从配置表中获取对应连续天数的奖励
        // SignInConfig config = getSignInConfigByContinuousDays(continuousDays);
        // if (config != null) return config.getPointsReward();

        // 获取所有激活的签到配置, 按连续天数降序排序
        LambdaQueryWrapper<SignInConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SignInConfig::getContinuousDays)
                .eq(SignInConfig::getIsActive, 1);
        List<SignInConfig> configs = baseMapper.selectList(wrapper);

        // 遍历配置, 找到第一个连续天数小于等于当前连续签到天数的配置
        for (SignInConfig config : configs) {
            if (continuousDays >= config.getContinuousDays()) {
                return config.getPointsReward();
            }
        }

        // 默认奖励规则
        return 10;
    }
}
