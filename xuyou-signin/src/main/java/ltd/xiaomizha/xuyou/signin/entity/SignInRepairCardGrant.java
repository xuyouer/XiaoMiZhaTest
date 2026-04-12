package ltd.xiaomizha.xuyou.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Source;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 补签卡发放记录表
 *
 * @TableName sign_in_repair_card_grant
 */
@TableName(value = "sign_in_repair_card_grant")
@Data
public class SignInRepairCardGrant implements Serializable {
    /**
     * 发放记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long grantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 补签卡类型
     */
    private Integer cardType;

    /**
     * 发放数量
     */
    private Integer quantity;

    /**
     * 来源: MONTHLY_GRANT-每月发放, PURCHASE-购买, REWARD-奖励, ADMIN_GRANT-管理员发放
     */
    private Source source;

    /**
     * 发放月份(YYYY-MM), 用于月度发放追踪
     */
    private String grantMonth;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}