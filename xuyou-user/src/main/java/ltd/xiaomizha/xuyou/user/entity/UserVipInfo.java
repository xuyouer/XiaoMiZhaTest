package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户会员信息表
 *
 * @TableName user_vip_info
 */
@TableName(value = "user_vip_info")
@Data
public class UserVipInfo implements Serializable {
    /**
     * 会员信息ID
     */
    @TableId(type = IdType.AUTO)
    private Integer vipId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * VIP等级(0-普通用户,1-VIP1,2-VIP2,...)
     */
    private Integer vipLevel;

    /**
     * VIP成长值/积分
     */
    private Integer vipPoints;

    /**
     * 升级到下一级所需成长值
     */
    private Integer nextLevelRequired;

    /**
     * 累计获得成长值(历史累计, 不减少)
     */
    private Integer totalEarnedPoints;

    /**
     * 今日已获得成长值(每日重置)
     */
    private Integer pointsToday;

    /**
     * 本月已获得成长值(每月重置)
     */
    private Integer pointsThisMonth;

    /**
     * 最后获取成长值日期
     */
    private Date lastPointsDate;

    /**
     * VIP到期日期(会员权益有效期)
     */
    private Date vipExpireDate;

    /**
     * 等级有效期(高等级到期后降级)
     */
    private Date levelExpireDate;

    /**
     * VIP状态
     */
    @Enumerated(EnumType.STRING)
    private Status vipStatus;

    /**
     * VIP升级日期
     */
    private Date vipUpgradeDate;

    /**
     * 累计充值金额
     */
    private BigDecimal totalRechargeAmount;

    /**
     * 最后充值时间
     */
    private Date lastRechargeDate;

    /**
     * 最后充值金额
     */
    private BigDecimal lastRechargeAmount;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 最后更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}