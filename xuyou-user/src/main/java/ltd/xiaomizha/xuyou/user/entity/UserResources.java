package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.BadgeType;
import ltd.xiaomizha.xuyou.common.enums.entity.ResourceCategory;
import ltd.xiaomizha.xuyou.common.enums.entity.Target;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户资源表
 *
 * @TableName user_resources
 */
@TableName(value = "user_resources")
@Data
public class UserResources implements Serializable {
    /**
     * 资源ID
     */
    @TableId(type = IdType.AUTO)
    private Integer resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源代码(唯一标识)
     */
    private String resourceCode;

    /**
     * 资源描述
     */
    private String resourceDescription;

    /**
     * 资源类型(目录、菜单、按钮、接口、页面、模块、其他)
     */
    @Enumerated(EnumType.STRING)
    private ResourceCategory resourceCategory;

    /**
     * 资源路径(路由路径或API路径)
     */
    private String resourcePath;

    /**
     * 组件路径(前端组件路径)
     */
    private String resourceComponent;

    /**
     * 资源图标
     */
    private String resourceIcon;

    /**
     * 父级资源ID(用于构建树形结构)
     */
    private Integer parentId;

    /**
     * 资源层级(从1开始)
     */
    private Integer level;

    /**
     * 排序序号(同级资源排序)
     */
    private Integer sortOrder;

    /**
     * 状态(1-启用,0-禁用)
     */
    private Integer status;

    /**
     * 是否可见(1-可见,0-隐藏)
     */
    private Integer visible;

    /**
     * 是否为系统内置资源(1-是,0-否)
     */
    private Integer isSystem;

    /**
     * 权限标识(用于权限验证)
     */
    private String permissionFlag;

    /**
     * 是否需要认证(1-是,0-否)
     */
    private Integer requiresAuth;

    /**
     * 是否缓存页面(仅对页面类型有效)
     */
    private Integer keepAlive;

    /**
     * 是否外部链接(1-是,0-否)
     */
    private Integer externalLink;

    /**
     * 链接打开方式
     */
    @Enumerated(EnumType.STRING)
    private Target target;

    /**
     * 徽章内容(如未读数量)
     */
    private String badge;

    /**
     * 徽章类型
     */
    @Enumerated(EnumType.STRING)
    private BadgeType badgeType;

    /**
     * 元数据(JSON格式, 可扩展存储额外信息)
     */
    private Object metaJson;

    /**
     * 创建人用户ID
     */
    private Integer createBy;

    /**
     * 更新人用户ID
     */
    private Integer updateBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 删除时间(软删除)
     */
    private Date deletedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}