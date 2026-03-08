package ltd.xiaomizha.xuyou.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 签到表
 *
 * @TableName sign_in
 */
@TableName(value = "sign_in")
@Data
public class SignIn implements Serializable {
    /**
     * 签到记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long signInId;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 签到日期
     */
    private LocalDateTime signInDate;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 签到获得的积分
     */
    private Integer pointsReward;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}