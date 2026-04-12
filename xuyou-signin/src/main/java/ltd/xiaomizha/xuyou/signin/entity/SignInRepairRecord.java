package ltd.xiaomizha.xuyou.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 补签记录表
 *
 * @TableName sign_in_repair_record
 */
@TableName(value = "sign_in_repair_record")
@Data
public class SignInRepairRecord implements Serializable {
    /**
     * 补签记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 补签日期
     */
    private LocalDate repairDate;

    /**
     * 使用的补签卡类型
     */
    private Integer cardType;

    /**
     * 补签前连续签到天数
     */
    private Integer continuousDaysBefore;

    /**
     * 补签后连续签到天数
     */
    private Integer continuousDaysAfter;

    /**
     * 补签获得的积分
     */
    private Integer pointsReward;

    /**
     * 状态: SUCCESS-成功, REVOKED-已撤销
     */
    private Status status;

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