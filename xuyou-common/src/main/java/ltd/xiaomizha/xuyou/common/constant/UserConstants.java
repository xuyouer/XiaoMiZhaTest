package ltd.xiaomizha.xuyou.common.constant;

/**
 * 用户相关常量
 */
public final class UserConstants {

    /**
     * 用户创建名前缀
     */
    public static final String CREATE_NAME_PREFIX = "xmzid_";

    /**
     * 账户状态: 正常
     */
    public static final Integer ACCOUNT_STATUS_NORMAL = 1;

    /**
     * 账户状态: 禁用
     */
    public static final Integer ACCOUNT_STATUS_DISABLED = 0;

    /**
     * 默认显示名标志: 是
     */
    public static final Integer IS_DEFAULT_DISPLAY_YES = 1;

    /**
     * 默认显示名标志: 否
     */
    public static final Integer IS_DEFAULT_DISPLAY_NO = 0;

    /**
     * 用户相关常量
     */
    public static final Integer DEFAULT_ROLE_ID = 3; // 默认普通用户角色ID
    public static final Integer DEFAULT_ASSIGNED_BY = 10000; // 默认分配人: 管理员
    public static final Integer DEFAULT_IS_PRIMARY = 1; // 默认主角色

    /**
     * 登录相关常量
     */
    public static final String DEFAULT_DEVICE_INFO = "UNKNOWN"; // 默认设备信息
    public static final String UNKNOWN = "UNKNOWN"; // 通用未知值

    /**
     * 积分相关常量
     */
    public static final Integer DEFAULT_POINTS = 0; // 默认积分为0

    /**
     * VIP相关常量
     */
    public static final Integer DEFAULT_VIP_LEVEL = 0; // 默认普通用户VIP等级

}
