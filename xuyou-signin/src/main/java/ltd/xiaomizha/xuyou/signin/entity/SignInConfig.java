package ltd.xiaomizha.xuyou.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 签到配置表
 *
 * @TableName sign_in_config
 */
@TableName(value = "sign_in_config")
@Data
public class SignInConfig implements Serializable {
    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Integer configId;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 对应积分奖励
     */
    private Integer pointsReward;

    /**
     * 是否启用
     */
    private Integer isActive;

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