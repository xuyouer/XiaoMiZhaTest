package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户积分表
 * @TableName user_points
 */
@TableName(value ="user_points")
@Data
public class UserPoints implements Serializable {
    /**
     * 积分记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer pointsId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 总积分
     */
    private Integer totalPoints;

    /**
     * 可用积分
     */
    private Integer availablePoints;

    /**
     * 冻结积分
     */
    private Integer frozenPoints;

    /**
     * 已消费积分
     */
    private Integer consumedPoints;

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