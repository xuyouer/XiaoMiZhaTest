package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Status;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户角色关联表
 *
 * @TableName user_role_relations
 */
@TableName(value = "user_role_relations")
@Data
public class UserRoleRelations implements Serializable {
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
     * 角色ID
     */
    private Integer roleId;

    /**
     * 分配人用户ID
     */
    private Integer assignedBy;

    /**
     * 角色到期时间
     */
    private Date expiresAt;

    /**
     * 是否主角色(1-是,0-否)
     */
    private Integer isPrimary;

    /**
     * 关联状态
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * 撤销原因
     */
    private String revokeReason;

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