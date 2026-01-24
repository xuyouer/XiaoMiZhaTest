package ltd.xiaomizha.xuyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ltd.xiaomizha.xuyou.common.enums.entity.Gender;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @TableName user_profiles
 */
@TableName(value = "user_profiles")
@Data
public class UserProfiles implements Serializable {
    /**
     * 资料ID
     */
    @TableId(type = IdType.AUTO)
    private Integer profileId;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 性别
     */
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * 个人简介
     */
    private String bio;

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