package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Level;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户操作日志表
 *
 * @TableName user_logs
 */
@TableName(value = "user_logs")
@Data
public class UserLogs implements Serializable {
    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long logId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 日志级别
     */
    @Enumerated(EnumType.STRING)
    private Level level;

    /**
     * 操作类型(登录/登出/修改资料等)
     */
    private String action;

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
     * 建档时间
     */
    private Date createdAt;

    /**
     * 最后更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}