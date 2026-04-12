package ltd.xiaomizha.xuyou.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Source;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 补签卡库存表
 *
 * @TableName sign_in_repair_card
 */
@TableName(value = "sign_in_repair_card")
@Data
public class SignInRepairCard implements Serializable {
    /**
     * 补签卡ID
     */
    @TableId(type = IdType.AUTO)
    private Long cardId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 补签卡类型: 1-普通补签卡, 2-高级补签卡(可补签多天)
     */
    private Integer cardType;

    /**
     * 库存数量
     */
    private Integer quantity;

    /**
     * 来源: MONTHLY_GRANT-每月发放, PURCHASE-购买, REWARD-奖励, ADMIN_GRANT-管理员发放
     */
    private Source source;

    /**
     * 过期日期(为空表示永久有效)
     */
    private LocalDate expiryDate;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}