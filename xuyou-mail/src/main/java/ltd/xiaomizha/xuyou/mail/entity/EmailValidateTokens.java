package ltd.xiaomizha.xuyou.mail.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮箱验证Token记录表
 *
 * @TableName email_validate_tokens
 */
@TableName(value = "email_validate_tokens")
@Data
public class EmailValidateTokens implements Serializable {
    /**
     * Token记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long tokenId;

    /**
     * 关联用户ID(关联users.user_id)
     */
    private Long userId;

    /**
     * 待验证邮箱地址
     */
    private String email;

    /**
     * 验证Token(唯一标识)
     */
    private String token;

    /**
     * Token类型：REGISTER-注册验证, RESET-重置密码, BIND-绑定邮箱, OTHER-其他
     */
    private TokenType tokenType;

    /**
     * 状态：0-未使用, 1-已验证, 2-已过期, 3-已撤销
     */
    private Integer status;

    /**
     * Token过期时间
     */
    private LocalDateTime expireAt;

    /**
     * 验证通过时间
     */
    private LocalDateTime validatedAt;

    /**
     * 撤销时间
     */
    private LocalDateTime revokedAt;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}