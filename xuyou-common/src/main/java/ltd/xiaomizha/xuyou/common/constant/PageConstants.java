package ltd.xiaomizha.xuyou.common.constant;

/**
 * 分页相关常量
 * <p>
 * 提供分页相关的常量定义
 */
public final class PageConstants {

    private PageConstants() {
        throw new AssertionError("工具类不允许实例化");
    }

    /**
     * 分页参数名称常量
     */
    public static final class ParamName {
        private ParamName() {
        }

        // 页码参数名
        public static final String CURRENT = "current";
        public static final String PAGE = "page";
        public static final String PAGE_NO = "pageNo";
        public static final String PAGE_NUM = "pageNum";

        // 页大小参数名
        public static final String PAGE_SIZE = "pageSize";
        public static final String SIZE = "size";
        public static final String LIMIT = "limit";
        public static final String PER_PAGE = "perPage";
        public static final String PAGE_LENGTH = "pageLength";

        // 排序参数名
        public static final String ORDER_BY = "orderBy";
        public static final String SORT = "sort";
        public static final String ORDER_FIELD = "orderField";
        public static final String ORDER_TYPE = "orderType";
        public static final String SORT_FIELD = "sortField";
        public static final String SORT_TYPE = "sortType";
        public static final String DIRECTION = "direction";

        // 分页模式参数名
        public static final String OFFSET = "offset";
        public static final String START = "start";
        public static final String END = "end";
        public static final String LAST_ID = "lastId";

        // 高级分页参数
        public static final String TOTAL = "total";
        public static final String PAGES = "pages";
        public static final String WITH_COUNT = "withCount";
    }

    /**
     * 分页默认值常量
     */
    public static final class DefaultValue {
        private DefaultValue() {
        }

        // 页码默认值
        public static final long CURRENT = 1L;
        public static final long MIN_CURRENT = 1L;

        // 页大小默认值
        public static final long PAGE_SIZE = 10L;
        public static final long MIN_PAGE_SIZE = 1L;
        public static final long MAX_PAGE_SIZE = 1000L;

        // 其他默认值
        public static final boolean WITH_COUNT = true;
        public static final boolean OPTIMIZE_COUNT = true;
    }

    /**
     * 排序相关常量
     */
    public static final class Sort {
        private Sort() {
        }

        // 排序类型
        public static final String ASC = "asc";
        public static final String DESC = "desc";
        public static final String ASC_NUM = "1";
        public static final String DESC_NUM = "-1";

        // 数据库排序关键字
        public static final String ASC_KEYWORD = "ASC";
        public static final String DESC_KEYWORD = "DESC";

        // 排序字段分隔符
        public static final String FIELD_SEPARATOR = ",";
        public static final String ORDER_SEPARATOR = " ";

        // 通用排序字段
        public static final String CREATE_TIME = "create_time";
        public static final String UPDATE_TIME = "update_time";
        public static final String ID = "id";
        public static final String SORT = "sort";
        public static final String ORDER = "order";
    }

    /**
     * 分页模式常量
     */
    public static final class Mode {
        private Mode() {
        }

        // 分页模式
        public static final String PAGE = "page";          // 传统分页
        public static final String OFFSET = "offset";      // 偏移量分页
        public static final String CURSOR = "cursor";      // 游标分页
        public static final String SCROLL = "scroll";      // 滚动分页

        // 分页算法
        public static final String SIMPLE = "simple";      // 简单分页
        public static final String COMPLEX = "complex";    // 复杂分页
    }

    /**
     * 分页返回字段常量
     */
    public static final class ResponseField {
        private ResponseField() {
        }

        public static final String DATA = "data";
        public static final String CURRENT = "current";
        public static final String PAGE_SIZE = "pageSize";
        public static final String TOTAL = "total";
        public static final String PAGES = "pages";
        public static final String HAS_NEXT = "hasNext";
        public static final String HAS_PREVIOUS = "hasPrevious";
        public static final String IS_FIRST = "isFirst";
        public static final String IS_LAST = "isLast";
        public static final String LIST = "list";
        public static final String ITEMS = "items";
        public static final String RECORDS = "records";
    }

    /**
     * 错误消息常量
     */
    public static final class ErrorMessage {
        private ErrorMessage() {
        }

        public static final String INVALID_PAGE_NUMBER = "页码必须大于0";
        public static final String INVALID_PAGE_SIZE = "每页大小必须在{min}到{max}之间";
        public static final String INVALID_ORDER_FIELD = "排序字段不合法";
        public static final String INVALID_ORDER_TYPE = "排序类型必须为asc或desc";
        public static final String PAGE_SIZE_TOO_LARGE = "每页大小不能超过{max}";
        public static final String PAGE_NUMBER_TOO_LARGE = "页码超出范围";
    }

    /**
     * 正则表达式常量
     */
    public static final class Regex {
        private Regex() {
        }

        // 排序参数正则
        public static final String ORDER_FIELD = "^[a-zA-Z_][a-zA-Z0-9_]*$";
        public static final String ORDER_TYPE = "^(asc|desc|ASC|DESC|1|-1)$";

        // 分页参数正则
        public static final String PAGE_NUMBER = "^[1-9]\\d*$";
        public static final String PAGE_SIZE = "^[1-9]\\d*$";

        // 复杂排序正则（如：field1 asc,field2 desc）
        public static final String COMPLEX_SORT = "^[a-zA-Z_][a-zA-Z0-9_]*(\\s+(asc|desc|ASC|DESC))?(,[a-zA-Z_][a-zA-Z0-9_]*(\\s+(asc|desc|ASC|DESC))?)*$";
    }

    /**
     * 配置键名常量
     */
    public static final class ConfigKey {
        private ConfigKey() {
        }

        public static final String ENABLE_PAGE = "monopoly.page.enable";
        public static final String DEFAULT_PAGE_SIZE = "monopoly.page.default-page-size";
        public static final String MAX_PAGE_SIZE = "monopoly.page.max-page-size";
        public static final String DEFAULT_CURRENT = "monopoly.page.default-current";
        public static final String OPTIMIZE_COUNT_SQL = "monopoly.page.optimize-count-sql";
        public static final String ENABLE_ORDER_BY = "monopoly.page.enable-order-by";
        public static final String ORDER_FIELD_WHITELIST = "monopoly.page.order-field-whitelist";
        public static final String PAGE_PARAM_NAMES = "monopoly.page.param-names";
        public static final String PAGE_SIZE_PARAM_NAMES = "monopoly.page.size-param-names";
    }
}