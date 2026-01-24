package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录记录表
 *
 * @TableName user_login_records
 */
@TableName(value = "user_login_records")
@Data
public class UserLoginRecords implements Serializable {
    /**
     * 登录记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long loginId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 登录IP地址
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
     * 登录类型
     */
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    /**
     * 登录状态(1成功/0失败)
     */
    private Integer loginStatus;

    /**
     * 失败原因(仅当登录失败时记录)
     */
    private String failureReason;

    /**
     * 登录时间
     */
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}