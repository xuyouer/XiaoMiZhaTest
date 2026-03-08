package ltd.xiaomizha.xuyou.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 签到状态表
 *
 * @TableName sign_in_status
 */
@TableName(value = "sign_in_status")
@Data
public class SignInStatus implements Serializable {
    /**
     * 状态ID
     */
    @TableId(type = IdType.AUTO)
    private Long statusId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前连续签到天数
     */
    private Integer currentContinuousDays;

    /**
     * 最后签到日期
     */
    private LocalDateTime lastSignInDate;

    /**
     * 是否处于连续签到状态
     */
    private Integer isContinuous;

    /**
     * 总签到次数
     */
    private Integer totalSignIns;

    /**
     * 历史最大连续签到天数
     */
    private Integer maxContinuousDays;

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