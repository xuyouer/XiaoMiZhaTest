package ltd.xiaomizha.xuyou.signin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.common.enums.entity.Source;
import ltd.xiaomizha.xuyou.signin.entity.SignInRepairCardGrant;
import ltd.xiaomizha.xuyou.signin.mapper.SignInRepairCardGrantMapper;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairCardGrantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiaom
 * @description 针对表【sign_in_repair_card_grant(补签卡发放记录表)】的数据库操作Service实现
 * @createDate 2026-03-17 16:02:17
 */
@Service
public class SignInRepairCardGrantServiceImpl extends ServiceImpl<SignInRepairCardGrantMapper, SignInRepairCardGrant>
        implements SignInRepairCardGrantService {

    /**
     * 检查用户本月是否已发放过补签卡
     *
     * @param userId     用户ID
     * @param cardType   补签卡类型
     * @param grantMonth 发放月份(格式: yyyy-MM)
     * @return 是否已发放
     */
    @Override
    public boolean hasGrantedThisMonth(Long userId, Integer cardType, String grantMonth) {
        LambdaQueryWrapper<SignInRepairCardGrant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairCardGrant::getUserId, userId)
                .eq(SignInRepairCardGrant::getCardType, cardType)
                .eq(SignInRepairCardGrant::getSource, Source.MONTHLY_GRANT)
                .eq(SignInRepairCardGrant::getGrantMonth, grantMonth);
        return count(wrapper) > 0;
    }

    /**
     * 获取用户补签卡发放历史记录
     *
     * @param userId 用户ID
     * @param limit  返回数量限制
     * @return 发放记录列表
     */
    @Override
    public List<SignInRepairCardGrant> getGrantHistory(Long userId, int limit) {
        LambdaQueryWrapper<SignInRepairCardGrant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairCardGrant::getUserId, userId)
                .orderByDesc(SignInRepairCardGrant::getCreatedAt)
                .last("LIMIT " + limit);
        return list(wrapper);
    }

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
    @Override
    public void recordGrant(Long userId, Integer cardType, int quantity, Source source, String grantMonth, String remark) {
        SignInRepairCardGrant grant = new SignInRepairCardGrant();
        grant.setUserId(userId);
        grant.setCardType(cardType);
        grant.setQuantity(quantity);
        grant.setSource(source);
        grant.setGrantMonth(grantMonth);
        grant.setRemark(remark);
        grant.setCreatedAt(LocalDateTime.now());
        save(grant);
    }

    /**
     * 检查用户在指定月份是否已发放过补签卡
     *
     * @param userId     用户ID
     * @param cardType   补签卡类型
     * @param source     来源
     * @param grantMonth 发放月份(格式: yyyy-MM)
     * @return 是否已发放
     */
    @Override
    public boolean hasGrantedInMonth(Long userId, Integer cardType, Source source, String grantMonth) {
        if (userId == null || cardType == null || source == null || grantMonth == null) {
            return false;
        }
        LambdaQueryWrapper<SignInRepairCardGrant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRepairCardGrant::getUserId, userId)
                .eq(SignInRepairCardGrant::getCardType, cardType)
                .eq(SignInRepairCardGrant::getSource, source)
                .eq(SignInRepairCardGrant::getGrantMonth, grantMonth);
        return count(wrapper) > 0;
    }
}




