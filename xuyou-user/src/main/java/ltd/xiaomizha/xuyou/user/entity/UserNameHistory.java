package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户名变更历史表
 * @TableName user_name_history
 */
@TableName(value ="user_name_history")
@Data
public class UserNameHistory {
    /**
     * 历史ID
     */
    @TableId(type = IdType.AUTO)
    private Integer historyId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 原显示名称
     */
    private String oldDisplayName;

    /**
     * 新显示名称
     */
    private String newDisplayName;

    /**
     * 修改人用户ID
     */
    private Integer changedBy;

    /**
     * 修改时间
     */
    private Date changedAt;
}