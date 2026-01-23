package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户成长值获取记录表
 * @TableName user_vip_points_log
 */
@TableName(value ="user_vip_points_log")
@Data
public class UserVipPointsLog implements Serializable {
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 规则ID
     */
    private Integer ruleId;

    /**
     * 规则代码
     */
    private String ruleCode;

    /**
     * 获取的成长值
     */
    private Integer pointsEarned;

    /**
     * 当前总成长值
     */
    private Integer currentVipPoints;

    /**
     * 当前VIP等级
     */
    private Integer currentVipLevel;

    /**
     * 关联业务ID
     */
    private String referenceId;

    /**
     * 关联业务类型
     */
    private String referenceType;

    /**
     * 创建时间
     */
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}