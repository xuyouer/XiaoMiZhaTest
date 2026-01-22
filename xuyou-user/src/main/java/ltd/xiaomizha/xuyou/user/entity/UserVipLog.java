package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户会员变更记录表
 * @TableName user_vip_log
 */
@TableName(value ="user_vip_log")
@Data
public class UserVipLog {
    /**
     * 会员记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 原VIP等级
     */
    private Integer oldVipLevel;

    /**
     * 新VIP等级
     */
    private Integer newVipLevel;

    /**
     * 原VIP成长值
     */
    private Integer oldVipPoints;

    /**
     * 新VIP成长值
     */
    private Integer newVipPoints;

    /**
     * 变更类型
     */
    private Object changeType;

    /**
     * 变更原因
     */
    private String changeReason;

    /**
     * 操作人用户ID(系统操作为NULL)
     */
    private Integer operatorId;

    /**
     * 创建时间
     */
    private Date createdAt;
}