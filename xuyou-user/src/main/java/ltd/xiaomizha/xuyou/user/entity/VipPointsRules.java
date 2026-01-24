package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.PointsType;

import java.io.Serializable;
import java.util.Date;

/**
 * 成长值获取规则表
 *
 * @TableName vip_points_rules
 */
@TableName(value = "vip_points_rules")
@Data
public class VipPointsRules implements Serializable {
    /**
     * 规则ID
     */
    @TableId(type = IdType.AUTO)
    private Integer ruleId;

    /**
     * 规则代码
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 成长值
     */
    private Integer pointsValue;

    /**
     * 类型(DAILY-每日,ONCE-仅一次,EVERYTIME-每次)
     */
    @Enumerated(EnumType.STRING)
    private PointsType pointsType;

    /**
     * 每日最多次数
     */
    private Integer maxTimesPerDay;

    /**
     * 总次数限制
     */
    private Integer maxTimesTotal;

    /**
     * 所需最低VIP等级
     */
    private Integer requireVipLevel;

    /**
     * 冷却时间(秒)
     */
    private Integer cooldownSeconds;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 状态(1-启用,0-禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}