package ltd.xiaomizha.xuyou.license.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.LicenseType;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * License授权系统表
 *
 * @TableName license_info
 */
@TableName(value = "license_info")
@Data
public class LicenseInfo implements Serializable {
    /**
     * LicenseID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 许可证密钥(唯一标识)
     */
    private String licenseKey;

    /**
     * 许可证ID(用于标识)
     */
    private String licenseId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 公司名
     */
    private String companyName;

    /**
     * 联系邮箱(便于通知)
     */
    private String contactEmail;

    /**
     * 产品版本
     */
    private String productVersion;

    /**
     * 授权功能列表
     */
    private String features;

    /**
     * 有效期开始时间
     */
    private LocalDateTime startTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime endTime;

    /**
     * 硬件绑定信息
     */
    private String hardwareInfo;

    /**
     * 许可证类型
     */
    private LicenseType licenseType;

    /**
     * 最大并发用户数(为空表示无上限)
     */
    private Integer maxConcurrentUsers;

    /**
     * 是否允许离线使用
     */
    private Integer allowOffline;

    /**
     * 许可证状态
     */
    private Status status;

    /**
     * 激活码(用于激活许可证)
     */
    private String activationCode;

    /**
     * 最后激活时间
     */
    private LocalDateTime lastActivationTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 备注信息
     */
    private String remarks;

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