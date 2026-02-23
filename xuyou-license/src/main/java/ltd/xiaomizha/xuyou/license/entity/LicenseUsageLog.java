package ltd.xiaomizha.xuyou.license.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Action;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * License使用记录表
 *
 * @TableName license_usage_log
 */
@TableName(value = "license_usage_log")
@Data
public class LicenseUsageLog implements Serializable {
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
     * 操作类型(ACTIVATE/VALIDATE/CHECK)
     */
    private Action action;

    /**
     * 操作IP地址
     */
    private String ipAddress;

    /**
     * 用户代理(浏览器信息)
     */
    private String userAgent;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 操作详情
     */
    private String details;

    /**
     * 操作状态(1成功/0失败)
     */
    private Integer status;

    /**
     * 失败原因(仅当操作失败时记录)
     */
    private String failureReason;

    /**
     * 操作时间
     */
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}