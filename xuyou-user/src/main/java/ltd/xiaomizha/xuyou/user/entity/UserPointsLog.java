package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户积分变更记录表
 * @TableName user_points_log
 */
@TableName(value ="user_points_log")
@Data
public class UserPointsLog {
    /**
     * 积分记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 积分变更值(正数为增加,负数为减少)
     */
    private Integer pointsChange;

    /**
     * 积分类型
     */
    private Object pointsType;

    /**
     * 变更后总积分
     */
    private Integer currentTotal;

    /**
     * 变更后可用积分
     */
    private Integer currentAvailable;

    /**
     * 变更描述
     */
    private String description;

    /**
     * 关联业务ID
     */
    private String referenceId;

    /**
     * 操作人用户ID(系统操作为NULL)
     */
    private Integer operatorId;

    /**
     * 创建时间
     */
    private Date createdAt;
}