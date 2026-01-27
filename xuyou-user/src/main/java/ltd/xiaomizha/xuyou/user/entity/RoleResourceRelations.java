package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.PermissionType;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色资源关联表
 *
 * @TableName role_resource_relations
 */
@TableName(value = "role_resource_relations")
@Data
public class RoleResourceRelations implements Serializable {
    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long relationId;

    /**
     * 角色ID
     */
    private Integer roleId;

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
     * 权限条件(JSON格式)
     */
    private Object conditionJson;

    /**
     * 是否可继承给子角色(1-是,0-否)
     */
    private Integer isInheritable;

    /**
     * 权限优先级
     */
    private Integer priority;

    /**
     * 授权人用户ID
     */
    private Integer grantedBy;

    /**
     * 状态(1-启用,0-禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}