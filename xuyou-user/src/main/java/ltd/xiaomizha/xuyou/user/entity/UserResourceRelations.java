package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.GrantType;
import ltd.xiaomizha.xuyou.common.enums.entity.PermissionType;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户资源关联表(直接授权)
 *
 * @TableName user_resource_relations
 */
@TableName(value = "user_resource_relations")
@Data
public class UserResourceRelations implements Serializable {
    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long relationId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 资源ID
     */
    private Integer resourceId;

    /**
     * 权限类型
     */
    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;

    /**
     * 授权类型(直接授权、继承授权、基于角色)
     */
    @Enumerated(EnumType.STRING)
    private GrantType grantType;

    /**
     * 授权来源ID(如角色ID、用户组ID)
     */
    private Integer sourceId;

    /**
     * 授权来源类型(ROLE, GROUP等)
     */
    private String sourceType;

    /**
     * 权限到期时间
     */
    private Date expiresAt;

    /**
     * 是否激活(1-是,0-否)
     */
    private Integer isActive;

    /**
     * 权限优先级(1-99, 数字越大优先级越高)
     */
    private Integer priority;

    /**
     * 权限条件(JSON格式, 如时间范围、数据范围等)
     */
    private Object conditionJson;

    /**
     * 授权人用户ID
     */
    private Integer grantedBy;

    /**
     * 授权原因
     */
    private String grantReason;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 撤销时间
     */
    private Date revokedAt;

    /**
     * 撤销人用户ID
     */
    private Integer revokedBy;

    /**
     * 撤销原因
     */
    private String revokeReason;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}