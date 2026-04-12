package ltd.xiaomizha.xuyou.mail.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Action;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮箱验证历史记录表
 *
 * @TableName email_validate_history
 */
@TableName(value = "email_validate_history")
@Data
public class EmailValidateHistory implements Serializable {
    /**
     * 历史记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long historyId;

    /**
     * 关联用户ID(可为空，未注册用户)
     */
    private Long userId;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 操作类型：SEND-发送, VALIDATE-验证成功, EXPIRE-过期, REVOKE-撤销
     */
    private Action actionType;

    /**
     * Token类型
     */
    private TokenType tokenType;

    /**
     * 操作结果：0-失败, 1-成功
     */
    private Integer result;

    /**
     * 错误信息(失败时)
     */
    private String errorMessage;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 浏览器UA信息
     */
    private String userAgent;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}