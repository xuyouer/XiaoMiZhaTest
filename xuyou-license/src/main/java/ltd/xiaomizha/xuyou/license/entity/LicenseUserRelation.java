package ltd.xiaomizha.xuyou.license.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * License用户关联表
 *
 * @TableName license_user_relation
 */
@TableName(value = "license_user_relation")
@Data
public class LicenseUserRelation implements Serializable {
    /**
     * 许可证关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long relationId;

    /**
     * 许可证密钥
     */
    private String licenseKey;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关联状态
     */
    private Status status;

    /**
     * 分配人用户ID
     */
    private Long assignedBy;

    /**
     * 分配时间
     */
    private LocalDateTime assignedAt;

    /**
     * 关联到期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}