package ltd.xiaomizha.xuyou.license.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.ChangeType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * License变更记录表
 *
 * @TableName license_change_log
 */
@TableName(value = "license_change_log")
@Data
public class LicenseChangeLog implements Serializable {
    /**
     * 许可证记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 许可证密钥
     */
    private String licenseKey;

    /**
     * 变更类型(CREATE/ACTIVATE/RENEW/REVOKE/SUSPEND/EXTEND)
     */
    private ChangeType changeType;

    /**
     * 变更前值
     */
    private String oldValue;

    /**
     * 变更后值
     */
    private String newValue;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 变更原因
     */
    private String reason;

    /**
     * 变更时间
     */
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}