package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import ltd.xiaomizha.xuyou.common.enums.entity.Source;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairCard;
import ltd.xiaomizha.xuyou.signin.mapper.SignInRepairCardMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairCardGrantService;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairCardService;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_card(补签卡库存表)】的数据库操作Service实现
 * @createDate 2026-03-17 16:02:17
 */
@Service
public class SignInRepairCardServiceImpl extends ServiceImpl<SignInRepairCardMapper, SignInRepairCard>
        implements SignInRepairCardService {

    @Resource
    private SignInRepairConfigService repairConfigService;

    @Resource
    private SignInRepairCardGrantService cardGrantService;

    /**
     * 获取或创建用户的补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型: 1-普通补签卡, 2-高级补签卡
     * @return 补签卡实体
     */
    @Override
    public SignInRepairCard getOrCreateRepairCard(Long userId, Integer cardType) {
        LambdaQueryWrapper<SignInRepairCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairCard::getUserId, userId)
                .eq(SignInRepairCard::getCardType, cardType);
        SignInRepairCard card = getOne(wrapper);

        if (card == null) {
            card = new SignInRepairCard();
            card.setUserId(userId);
            card.setCardType(cardType);
            card.setQuantity(0);
            card.setSource(Source.MONTHLY_GRANT);
            save(card);
        }
        return card;
    }

    /**
     * 获取用户可用补签卡数量
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型: 1-普通补签卡, 2-高级补签卡
     * @return 可用补签卡数量(排除已过期的)
     */
    @Override
    public int getAvailableCardCount(Long userId, Integer cardType) {
        SignInRepairCard card = getOrCreateRepairCard(userId, cardType);

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDate.now())) {
            return 0;
        }

        return card.getQuantity();
    }

    /**
     * 使用一张补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型
     * @return 是否使用成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCard(Long userId, Integer cardType) {
        SignInRepairCard card = getOrCreateRepairCard(userId, cardType);

        if (card.getQuantity() <= 0) {
            return false;
        }

        if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(LocalDate.now())) {
            return false;
        }

        card.setQuantity(card.getQuantity() - 1);
        card.setUpdatedAt(LocalDateTime.now());
        return updateById(card);
    }

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCards(Long userId, Integer cardType, int quantity, Source source, String remark) {
        if (quantity <= 0) {
            return false;
        }

        int maxAccumulation = repairConfigService.getConfigValueAsInt("max_card_accumulation", 10);

        SignInRepairCard card = getOrCreateRepairCard(userId, cardType);

        int newQuantity = card.getQuantity() + quantity;
        if (maxAccumulation > 0 && newQuantity > maxAccumulation) {
            newQuantity = maxAccumulation;
        }

        card.setQuantity(newQuantity);
        card.setSource(source);

        boolean enableExpiry = repairConfigService.getConfigValueAsBoolean("enable_repair_card_expiry", false);
        if (enableExpiry && card.getExpiryDate() == null) {
            int expiryMonths = repairConfigService.getConfigValueAsInt("card_expiry_months", 3);
            card.setExpiryDate(LocalDate.now().plusMonths(expiryMonths));
        }

        card.setUpdatedAt(LocalDateTime.now());
        boolean updated = updateById(card);

        if (updated) {
            String grantMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            cardGrantService.recordGrant(userId, cardType, quantity, source, grantMonth, remark);
        }

        return updated;
    }

    /**
     * 获取用户所有补签卡信息
     *
     * @param userId 用户ID
     * @return 包含普通卡、高级卡、总卡数和详细信息的Map
     */
    @Override
    public Map<String, Object> getUserAllCards(Long userId) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<SignInRepairCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairCard::getUserId, userId);
        List<SignInRepairCard> cards = list(wrapper);

        int normalCardCount = 0;
        int advancedCardCount = 0;

        for (SignInRepairCard card : cards) {
            if (card.getExpiryDate() == null || !card.getExpiryDate().isBefore(LocalDate.now())) {
                if (card.getCardType() == 1) {
                    normalCardCount += card.getQuantity();
                } else if (card.getCardType() == 2) {
                    advancedCardCount += card.getQuantity();
                }
            }
        }

        result.put("normalCardCount", normalCardCount);
        result.put("advancedCardCount", advancedCardCount);
        result.put("totalCardCount", normalCardCount + advancedCardCount);
        result.put("cards", cards);

        return result;
    }

    /**
     * 领取免费补签卡
     * <p>
     * 每月可领取三张免费补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型: 1-普通补签卡, 2-高级补签卡
     * @return 领取结果,包含领取数量和当前库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claimFreeCard(Long userId, Integer cardType) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        if (cardType == null || (cardType != 1 && cardType != 2)) {
            throw new IllegalArgumentException("补签卡类型无效,必须为1(普通)或2(高级)");
        }
        if (hasClaimedThisMonth(userId, cardType)) {
            throw new RuntimeException("本月已领取过该类型补签卡,请下月再来");
        }
        int freeCardQuantity = repairConfigService.getConfigValueAsInt("monthly_grant_quantity", 3);
        if (freeCardQuantity <= 0) {
            freeCardQuantity = 1;
        }
        boolean added = addCards(userId, cardType, freeCardQuantity, Source.MONTHLY_GRANT, "每月免费领取");
        if (!added) {
            throw new RuntimeException("领取补签卡失败");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("cardType", cardType);
        result.put("claimedQuantity", freeCardQuantity);
        result.put("currentCards", getUserAllCards(userId));
        result.put("message", "成功领取" + freeCardQuantity + "张" + (cardType == 1 ? "普通" : "高级") + "补签卡");
        return result;
    }

    /**
     * 检查用户本月是否已领取免费补签卡
     *
     * @param userId   用户ID
     * @param cardType 补签卡类型
     * @return 是否已领取
     */
    @Override
    public boolean hasClaimedThisMonth(Long userId, Integer cardType) {
        if (userId == null || cardType == null) {
            return false;
        }
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return cardGrantService.hasGrantedInMonth(userId, cardType, Source.MONTHLY_GRANT, currentMonth);
    }
}




