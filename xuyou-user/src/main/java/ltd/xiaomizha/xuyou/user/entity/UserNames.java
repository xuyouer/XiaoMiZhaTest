package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户名信息表
 * @TableName user_names
 */
@TableName(value ="user_names")
@Data
public class UserNames {
    /**
     * 名称ID
     */
    @TableId(type = IdType.AUTO)
    private Integer nameId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 创建用户名
     */
    private String createName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 是否使用显示名作为默认显示(1-是,0-否)
     */
    private Integer isDefaultDisplay;

    /**
     * 建档时间
     */
    private Date createdAt;

    /**
     * 最后更新时间
     */
    private Date updatedAt;
}