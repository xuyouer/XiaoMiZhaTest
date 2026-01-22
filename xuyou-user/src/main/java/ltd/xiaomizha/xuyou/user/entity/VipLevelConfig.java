package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * VIP等级配置表
 * @TableName vip_level_config
 */
@TableName(value ="vip_level_config")
@Data
public class VipLevelConfig {
    /**
     * 等级ID
     */
    @TableId(type = IdType.AUTO)
    private Integer levelId;

    /**
     * VIP等级(0-普通用户,1-10为VIP等级)
     */
    private Integer vipLevel;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 升级所需最小成长值
     */
    private Integer minPoints;

    /**
     * 升级所需最大成长值(为空表示无上限)
     */
    private Integer maxPoints;

    /**
     * 等级图标URL
     */
    private String iconUrl;

    /**
     * 徽章颜色
     */
    private String badgeColor;

    /**
     * 每日成长值获取上限
     */
    private Integer dailyPointsLimit;

    /**
     * 每月成长值获取上限
     */
    private Integer monthlyPointsLimit;

    /**
     * 等级特权(JSON格式)
     */
    private Object benefitsJson;

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
}