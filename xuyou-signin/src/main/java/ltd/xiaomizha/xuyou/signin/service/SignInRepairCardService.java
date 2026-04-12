package ltd.xiaomizha.xuyou.signin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.common.enums.entity.Source;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairCard;

import java.util.Map;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_card(补签卡库存表)】的数据库操作Service
 * @createDate 2026-03-17 16:02:17
 */
public interface SignInRepairCardService extends IService<SignInRepairCard> {

    /**
     * 获取或创建用户的补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型: 1-普通补签卡, 2-高级补签卡
     * @return 补签卡实体
     */
    SignInRepairCard getOrCreateRepairCard(Long userId, Integer cardType);

    /**
     * 获取用户可用补签卡数量
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型: 1-普通补签卡, 2-高级补签卡
     * @return 可用补签卡数量(排除已过期的)
     */
    int getAvailableCardCount(Long userId, Integer cardType);

    /**
     * 使用一张补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型
     * @return 是否使用成功
     */
    boolean useCard(Long userId, Integer cardType);

    /**
     * 批量添加补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型
     * @param quantity 数量
     * @param source   来源: MONTHLY_GRANT-每月发放, PURCHASE-购买, REWARD-奖励, ADMIN_GRANT-管理员发放
     * @param remark   备注
     * @return 是否添加成功
     */
    boolean addCards(Long userId, Integer cardType, int quantity, Source source, String remark);

    /**
     * 获取用户所有补签卡信息
     *
     * @param userId 用户ID
     * @return 包含普通卡、高级卡、总卡数和详细信息的Map
     */
    Map<String, Object> getUserAllCards(Long userId);

    /**
     * 领取免费补签卡
     * <p>
     * 每月可领取三张免费补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型: 1-普通补签卡, 2-高级补签卡
     * @return 领取结果,包含领取数量和当前库存
     */
    Map<String, Object> claimFreeCard(Long userId, Integer cardType);

    /**
     * 检查用户本月是否已领取免费补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型
     * @return 是否已领取
     */
    boolean hasClaimedThisMonth(Long userId, Integer cardType);

}
