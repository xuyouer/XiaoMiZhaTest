package ltd.xiaomizha.xuyou.common.constant;

public final class SignInConstant {

    private SignInConstant() {
    }

    public static final int LOCK_WAIT_SECONDS = 3;
    public static final int LOCK_LEASE_SECONDS = 5;
    public static final int MAX_CONTINUOUS_DAYS_CHECK = 365;
    public static final int DEFAULT_RANKING_LIMIT = 10;
    public static final int MIN_RANKING_LIMIT = 1;
    public static final int MAX_RANKING_LIMIT = 100;

    public static final String LOCK_KEY_PREFIX = "xiaomizha:signin:lock:";

    public static final String REPAIR_LOCK_KEY_PREFIX = "xiaomizha:signin:repair:lock:";
    public static final int DEFAULT_CARD_TYPE = 1;
    public static final double DEFAULT_POINTS_RATIO = 0.5;
    public static final int DEFAULT_MAX_REPAIR_DAYS = 30;
    public static final int DEFAULT_MONTHLY_GRANT_QUANTITY = 3;

}
