package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户反馈表
 * @TableName user_feedback
 */
@TableName(value ="user_feedback")
@Data
public class UserFeedback implements Serializable {
    /**
     * 反馈ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 反馈类型: 1-系统问题, 2-功能建议, 3-BUG反馈, 4-其他
     */
    private Integer type;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 状态: 1-待处理, 2-已受理, 3-已解决, 4-已关闭
     */
    private Integer status;

    /**
     * 回复内容
     */
    private String reply;

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