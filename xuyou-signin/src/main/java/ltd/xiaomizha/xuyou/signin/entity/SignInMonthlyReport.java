package ltd.xiaomizha.xuyou.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 月度签到报告表
 *
 * @TableName sign_in_monthly_report
 */
@TableName(value = "sign_in_monthly_report")
@Data
public class SignInMonthlyReport implements Serializable {
    /**
     * 报告ID
     */
    @TableId(type = IdType.AUTO)
    private Long reportId;

    /**
     * 报告月份(YYYY-MM)
     */
    private String reportMonth;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 月度签到次数
     */
    private Integer totalSignIns;

    /**
     * 最大连续签到天数
     */
    private Integer continuousDays;

    /**
     * 获得的积分
     */
    private Integer pointsEarned;

    /**
     * 月度排名
     */
    private Integer rank;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}