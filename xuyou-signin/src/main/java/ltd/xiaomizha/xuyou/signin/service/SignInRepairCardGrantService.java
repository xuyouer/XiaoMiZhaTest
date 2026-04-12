package ltd.xiaomizha.xuyou.signin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.common.enums.entity.Source;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairCardGrant;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_card_grant(补签卡发放记录表)】的数据库操作Service
 * @createDate 2026-03-17 16:02:17
 */
public interface SignInRepairCardGrantService extends IService<SignInRepairCardGrant> {

    /**
     * 检查用户本月是否已发放过补签卡
     *
     * @param userId     用户ID
     * @param cardType   补签卡类型
     * @param grantMonth 发放月份(格式: yyyy-MM)
     * @return 是否已发放
     */
    boolean hasGrantedThisMonth(Long userId, Integer cardType, String grantMonth);

    /**
     * 获取用户补签卡发放历史记录
     *
     * @param userId 用户ID
     * @param limit  返回数量限制
     * @return 发放记录列表
     */
    List<SignInRepairCardGrant> getGrantHistory(Long userId, int limit);

    /**
     * 记录补签卡发放
     *
     * @param userId     用户ID
     * @param cardType   补签卡类型
     * @param quantity   发放数量
     * @param source     来源: MONTHLY_GRANT-每月发放, PURCHASE-购买, REWARD-奖励, ADMIN_GRANT-管理员发放
     * @param grantMonth 发放月份(格式: yyyy-MM)
     * @param remark     备注
     */
    void recordGrant(Long userId, Integer cardType, int quantity, Source source, String grantMonth, String remark);

    /**
     * 检查用户在指定月份是否已发放过补签卡
     *
     * @param userId     用户ID
     * @param cardType   补签卡类型
     * @param source     来源
     * @param grantMonth 发放月份(格式: yyyy-MM)
     * @return 是否已发放
     */
    boolean hasGrantedInMonth(Long userId, Integer cardType, Source source, String grantMonth);

}
