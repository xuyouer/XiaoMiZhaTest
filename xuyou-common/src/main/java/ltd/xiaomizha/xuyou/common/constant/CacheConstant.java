package ltd.xiaomizha.xuyou.common.constant;

public final class CacheConstant {

    private CacheConstant() {
    }

    public static final String REDIS_PREFIX_SMS_CODE = "xiaomizha:sms:code:";

    public static final String REDIS_PREFIX_EMAIL_CODE = "xiaomizha:email:code:";
    public static final String REDIS_PREFIX_COOLDOWN = "xiaomizha:email:cooldown:";
    public static final String REDIS_PREFIX_DAILY_LIMIT = "xiaomizha:email:daily:limit:";

    public static final String REDIS_PREFIX_SCAN_LOGIN = "xiaomizha:scan_login:";
    public static final String REDIS_PREFIX_SCAN_LOGIN_USER_ID = ":userId";
    public static final String REDIS_PREFIX_SCAN_STATE = "xiaomizha:scan_state:";

    public static final String REDIS_KEY_TRIAL_START_TIME = "xiaomizha:trial_start_time";
    public static final String REDIS_KEY_TRIAL_HARDWARE = "xiaomizha:trial_hardware";

    public static final String REDIS_PREFIX_CAPTCHA_TOKEN = "xiaomizha:captcha:token:";

}
