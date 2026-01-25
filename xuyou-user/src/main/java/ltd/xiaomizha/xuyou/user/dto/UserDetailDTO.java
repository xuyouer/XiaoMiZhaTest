package ltd.xiaomizha.xuyou.user.dto;

import lombok.Data;
import ltd.xiaomizha.xuyou.user.entity.*;

import java.util.List;

/**
 * 用户详细信息DTO
 */
@Data
public class UserDetailDTO {
    /**
     * 用户基本信息
     */
    private Users user;
    
    /**
     * 用户资料
     */
    private UserProfiles userProfile;
    
    /**
     * 用户名信息
     */
    private UserNames userNames;
    
    /**
     * 用户名变更历史
     */
    private List<UserNameHistory> userNameHistories;
    
    /**
     * 用户积分信息
     */
    private UserPoints userPoints;
    
    /**
     * 用户VIP信息
     */
    private UserVipInfo userVipInfo;
    
    /**
     * 用户角色列表
     */
    private List<UserRoles> userRoles;
    
    /**
     * 用户角色下的资源列表
     */
    private List<UserResources> userResources;
}