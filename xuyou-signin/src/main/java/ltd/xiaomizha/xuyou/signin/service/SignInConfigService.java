package ltd.xiaomizha.xuyou.signin.service;

import ltd.xiaomizha.xuyou.signin.entity.SignInConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author xiaom
 * @description 针对表【sign_in_config(签到配置表)】的数据库操作Service
 * @createDate 2026-02-25 18:44:03
 */
public interface SignInConfigService extends IService<SignInConfig> {
    
    /**
     * 根据连续签到天数获取签到奖励配置
     *
     * @param continuousDays 连续签到天数
     * @return 签到配置信息
     */
    SignInConfig getSignInConfigByContinuousDays(int continuousDays);

    /**
     * 计算签到积分奖励
     *
     * @param continuousDays 连续签到天数
     * @return 积分奖励
     */
    int calculateSignInReward(int continuousDays);

}
