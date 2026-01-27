package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统配置表
 *
 * @TableName system_configs
 */
@TableName(value = "system_configs")
@Data
public class SystemConfigs implements Serializable {
    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置类型: string, number, boolean, json
     */
    private String configType;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 是否为公开配置
     */
    private Integer isPublic;

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