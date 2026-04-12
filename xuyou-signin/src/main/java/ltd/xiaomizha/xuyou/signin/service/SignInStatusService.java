package ltd.xiaomizha.xuyou.signin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.signin.entity.SignInStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiaom
 * @description 针对表【sign_in_status(签到状态表)】的数据库操作Service
 * @createDate 2026-02-25 18:44:03
 */
public interface SignInStatusService extends IService<SignInStatus> {

    /**
     * 更新或创建签到状态
     *
     * @param userId         用户ID
     * @param continuousDays 连续签到天数
     * @param signInDateTime 签到时间
     * @return 签到状态对象
     */
    SignInStatus updateOrCreateSignInStatus(Long userId, int continuousDays, LocalDateTime signInDateTime);

    /**
     * 获取用户签到状态
     *
     * @param userId 用户ID
     * @return 签到状态对象
     */
    SignInStatus getSignInStatusByUserId(Long userId);

    List<Long> getAllUserIds();

}
