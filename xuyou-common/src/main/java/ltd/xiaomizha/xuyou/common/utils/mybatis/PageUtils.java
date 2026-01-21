package ltd.xiaomizha.xuyou.common.utils.mybatis;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.config.PageConfig;
import ltd.xiaomizha.xuyou.common.constant.PageConstants;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.common.utils.http.ServletUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 分页工具类
 */
@Slf4j
public class PageUtils {

    private PageUtils() {
        throw new AssertionError("工具类不允许实例化");
    }

    /**
     * 通过反射获取排序
     */
    @SuppressWarnings("unchecked")
    private static <T> List<OrderItem> getOrdersByReflection(Page<T> page) {
        try {
            Object orders = page.getClass().getDeclaredField("orders").get(page);
            if (orders instanceof List) {
                return (List<OrderItem>) orders;
            }
        } catch (Exception e) {
            // 忽略异常, 返回空列表
        }
        return new ArrayList<>();
    }

    /**
     * 通过反射设置排序
     */
    private static <T> void setOrdersByReflection(Page<T> page, List<OrderItem> orders) {
        try {
            page.getClass().getDeclaredField("orders").set(page, orders);
        } catch (Exception e) {
            // 忽略异常
        }
    }

    /**
     * 复制分页配置
     */
    public static <S, T> void copyPageConfig(Page<S> source, Page<T> target) {
        target.setCurrent(source.getCurrent());
        target.setSize(source.getSize());
        target.setTotal(source.getTotal());

        List<OrderItem> orders = getOrders(source);
        if (!orders.isEmpty()) {
            setOrders(target, new ArrayList<>(orders));
        }
    }

    /**
     * 创建分页对象并复制配置
     */
    public static <S, T> Page<T> copyPage(Page<S> source) {
        if (source == null) {
            return null;
        }

        Page<T> target = new Page<>();
        copyPageConfig(source, target);
        return target;
    }

    /**
     * 获取排序列表
     */
    public static <T> List<OrderItem> getOrders(Page<T> page) {
        return getOrdersByReflection(page);
    }

    /**
     * 设置排序列表
     */
    public static <T> void setOrders(Page<T> page, List<OrderItem> orders) {
        try {
            page.setOrders(orders);
        } catch (NoSuchMethodError e) {
            setOrdersByReflection(page, orders);
        }
    }

    /**
     * 添加排序项
     */
    private static <T> void addOrder(Page<T> page, OrderItem orderItem) {
        List<OrderItem> orders = getOrders(page);
        if (orders == null) {
            orders = new ArrayList<>();
        }
        orders.add(orderItem);
        setOrders(page, orders);
    }

    /**
     * 创建分页对象
     * <p>
     * 使用默认配置
     *
     * @param <T> 数据类型
     * @return 分页对象
     */
    public static <T> Page<T> createPage() {
        return createPage(PageConstants.DefaultValue.CURRENT,
                PageConstants.DefaultValue.PAGE_SIZE);
    }

    /**
     * 创建分页对象
     * <p>
     * 带默认值
     *
     * @param defaultCurrent  默认当前页
     * @param defaultPageSize 默认每页大小
     * @param <T>             数据类型
     * @return 分页对象
     */
    public static <T> Page<T> createPage(long defaultCurrent, long defaultPageSize) {
        Page<T> page = new Page<>();

        // 获取当前页
        long current = parsePageParam(PageConstants.ParamName.CURRENT, defaultCurrent);
        current = tryAlternativeParams(current, defaultCurrent, PageConstants.DefaultValue.MIN_CURRENT, getPageParamNames());

        // 获取每页大小
        long pageSize = parsePageParam(PageConstants.ParamName.PAGE_SIZE, defaultPageSize);
        pageSize = tryAlternativeParams(pageSize, defaultPageSize, PageConstants.DefaultValue.MIN_PAGE_SIZE, getPageSizeParamNames());

        // 边界检查
        current = Math.max(current, PageConstants.DefaultValue.MIN_CURRENT);
        pageSize = clamp(pageSize, PageConstants.DefaultValue.MIN_PAGE_SIZE, PageConstants.DefaultValue.MAX_PAGE_SIZE);

        page.setCurrent(current);
        page.setSize(pageSize);

        // 添加排序
        addOrderItems(page);

        log.debug("创建分页对象: current={}, size={}", current, pageSize);
        return page;
    }

    /**
     * 创建分页对象
     * <p>
     * 使用配置
     *
     * @param config 分页配置
     * @param <T>    数据类型
     * @return 分页对象
     */
    public static <T> Page<T> createPage(PageConfig config) {
        if (config == null) {
            return createPage();
        }

        Page<T> page = new Page<>();
        page.setCurrent(config.getCurrent());
        page.setSize(config.getPageSize());

        if (config.getOrders() != null && !config.getOrders().isEmpty()) {
            page.setOrders(new ArrayList<>(config.getOrders()));
        }

        return page;
    }

    /**
     * 获取页码参数名称列表
     */
    private static List<String> getPageParamNames() {
        return List.of(
                PageConstants.ParamName.CURRENT,
                PageConstants.ParamName.PAGE,
                PageConstants.ParamName.PAGE_NO,
                PageConstants.ParamName.PAGE_NUM
        );
    }

    /**
     * 获取页大小参数名称列表
     */
    private static List<String> getPageSizeParamNames() {
        return List.of(
                PageConstants.ParamName.PAGE_SIZE,
                PageConstants.ParamName.SIZE,
                PageConstants.ParamName.LIMIT,
                PageConstants.ParamName.PER_PAGE,
                PageConstants.ParamName.PAGE_LENGTH
        );
    }

    /**
     * 尝试其他参数名
     */
    private static long tryAlternativeParams(long value, long defaultValue, long minValue, List<String> paramNames) {
        if (value != defaultValue) {
            return value;
        }

        for (String paramName : paramNames) {
            long newValue = parsePageParam(paramName, defaultValue);
            if (newValue != defaultValue) {
                return clamp(newValue, minValue, Long.MAX_VALUE);
            }
        }

        return defaultValue;
    }

    /**
     * 解析分页参数
     */
    private static long parsePageParam(String paramName, long defaultValue) {
        try {
            String paramValue = ServletUtil.getParameterFromRequest(paramName);
            if (StringUtils.hasText(paramValue) && NumberUtil.isNumber(paramValue)) {
                return Long.parseLong(paramValue.trim());
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return defaultValue;
    }

    /**
     * 值限制在范围内
     */
    private static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * 添加排序项
     */
    private static <T> void addOrderItems(Page<T> page) {
        try {
            // 获取排序字段
            String orderField = Optional.ofNullable(ServletUtil.getParameterFromRequest(PageConstants.ParamName.ORDER_BY))
                    .orElseGet(() -> ServletUtil.getParameterFromRequest(PageConstants.ParamName.SORT));

            if (StringUtils.hasText(orderField)) {
                // 获取排序类型
                String orderType = Optional.ofNullable(ServletUtil.getParameterFromRequest(PageConstants.ParamName.ORDER_TYPE))
                        .orElse(PageConstants.Sort.ASC);

                // boolean isAsc = PageConstants.Sort.ASC.equalsIgnoreCase(orderType.trim())
                //         || PageConstants.Sort.ASC_NUM.equals(orderType.trim());
                boolean isAsc = isAscending(orderType);

                // 处理多个排序字段
                String[] fields = orderField.split(PageConstants.Sort.FIELD_SEPARATOR);
                for (String field : fields) {
                    String trimmedField = field.trim();
                    if (isValidOrderField(trimmedField)) {
                        // OrderItem orderItem = isAsc ?
                        //         OrderItem.asc(trimmedField) :
                        //         OrderItem.desc(trimmedField);
                        // page.addOrder(orderItem);
                        page.addOrder(isAsc ? OrderItem.asc(trimmedField) : OrderItem.desc(trimmedField));
                    }
                }
            }
        } catch (Exception e) {
            // 忽略排序异常
        }
    }

    /**
     * 判断是否为升序
     */
    private static boolean isAscending(String orderType) {
        if (StrUtil.isBlank(orderType)) {
            return true;
        }

        String type = orderType.trim();
        return PageConstants.Sort.ASC.equalsIgnoreCase(type) ||
                PageConstants.Sort.ASC_NUM.equals(type);
    }

    /**
     * 验证排序字段
     */
    private static boolean isValidOrderField(String field) {
        if (!StringUtils.hasText(field)) {
            return false;
        }
        return field.matches(PageConstants.Regex.ORDER_FIELD);
    }

    /**
     * 计算分页偏移量
     *
     * @param current  当前页
     * @param pageSize 每页大小
     * @return 偏移量
     */
    public static long calculateOffset(long current, long pageSize) {
        long validCurrent = Math.max(current, PageConstants.DefaultValue.MIN_CURRENT);
        // long validPageSize = Math.max(pageSize, PageConstants.DefaultValue.MIN_PAGE_SIZE);
        long validPageSize = Math.max(
                Math.min(pageSize, PageConstants.DefaultValue.MAX_PAGE_SIZE),
                PageConstants.DefaultValue.MIN_PAGE_SIZE
        );
        return (validCurrent - 1) * validPageSize;
    }

    /**
     * 计算总页数
     *
     * @param total    总记录数
     * @param pageSize 每页大小
     * @return 总页数
     */
    public static long calculatePages(long total, long pageSize) {
        if (total <= 0) {
            return 0;
        }
        if (pageSize <= 0) {
            pageSize = PageConstants.DefaultValue.PAGE_SIZE;
        }
        return (total + pageSize - 1) / pageSize;
    }

    /**
     * 验证分页参数
     *
     * @param current  当前页
     * @param pageSize 每页大小
     * @return 是否有效
     */
    public static boolean isValidParams(long current, long pageSize) {
        return current >= PageConstants.DefaultValue.MIN_CURRENT &&
                pageSize >= PageConstants.DefaultValue.MIN_PAGE_SIZE &&
                pageSize <= PageConstants.DefaultValue.MAX_PAGE_SIZE;
    }

    /**
     * 转换分页数据
     *
     * @param sourcePage 源分页
     * @param converter  数据转换器
     * @param <S>        源数据类型
     * @param <T>        目标数据类型
     * @return 转换后的分页
     */
    public static <S, T> Page<T> convertPage(Page<S> sourcePage, Function<S, T> converter) {
        Page<T> targetPage = new Page<>();
        // targetPage.setCurrent(sourcePage.getCurrent());
        // targetPage.setSize(sourcePage.getSize());
        // targetPage.setTotal(sourcePage.getTotal());
        copyPageConfig(sourcePage, targetPage);

        // 复制排序信息
        List<OrderItem> orders = getOrders(sourcePage);
        if (orders != null && !orders.isEmpty()) {
            setOrders(targetPage, new ArrayList<>(orders));
        }

        // 转换数据
        if (ObjectUtil.isNotEmpty(sourcePage.getRecords())) {
            List<T> convertedList = new ArrayList<>();
            for (S record : sourcePage.getRecords()) {
                convertedList.add(converter.apply(record));
            }
            targetPage.setRecords(convertedList);
        }

        return targetPage;
    }

    /**
     * 获取分页配置对象
     *
     * @return 分页配置
     */
    public static PageConfig getPageConfig() {
        return getPageConfig(PageConstants.DefaultValue.CURRENT,
                PageConstants.DefaultValue.PAGE_SIZE);
    }

    /**
     * 获取分页配置对象
     * <p>
     * 带默认值
     *
     * @param defaultCurrent  默认当前页
     * @param defaultPageSize 默认每页大小
     * @return 分页配置
     */
    public static PageConfig getPageConfig(long defaultCurrent, long defaultPageSize) {
        Page<Object> page = createPage(defaultCurrent, defaultPageSize);
        return PageConfig.builder()
                .current(page.getCurrent())
                .pageSize(page.getSize())
                .orders(getOrders(page))
                .build();
    }

    /**
     * 添加排序到分页对象
     *
     * @param page      分页对象
     * @param field     排序字段
     * @param ascending 是否升序
     * @param <T>       数据类型
     */
    public static <T> void addSort(Page<T> page, String field, boolean ascending) {
        if (StringUtils.hasText(field) && isValidOrderField(field)) {
            OrderItem orderItem = ascending ?
                    OrderItem.asc(field) :
                    OrderItem.desc(field);
            addOrder(page, orderItem);
        }
    }

    /**
     * 添加默认排序
     * <p>
     * 按创建时间倒序
     *
     * @param page 分页对象
     * @param <T>  数据类型
     */
    public static <T> void addDefaultSort(Page<T> page) {
        addSort(page, PageConstants.Sort.CREATE_TIME, false);
    }

    /**
     * 清空排序
     *
     * @param page 分页对象
     * @param <T>  数据类型
     */
    public static <T> void clearSort(Page<T> page) {
        setOrders(page, new ArrayList<>());
    }

    /**
     * 转换分页响应结果
     *
     * @param sourcePage 源分页响应
     * @param converter  数据转换器
     * @param <S>        源数据类型
     * @param <T>        目标数据类型
     * @return 转换后的分页响应
     */
    public static <S, T> ResponseResultPage<T> convertResponse(
            ResponseResultPage<S> sourcePage, Function<S, T> converter) {

        if (sourcePage == null) {
            return ResponseResultPage.empty();
        }

        // 转换数据列表
        List<T> targetList = new ArrayList<>();
        if (sourcePage.getData() != null) {
            for (S item : sourcePage.getData()) {
                targetList.add(converter.apply(item));
            }
        }

        return ResponseResultPage.ok(
                targetList,
                sourcePage.getCurrent(),
                sourcePage.getPageSize(),
                sourcePage.getTotal()
        );
    }

    /**
     * 从MyBatis Plus分页转换为响应分页
     *
     * @param page      MyBatis Plus分页
     * @param converter 数据转换器
     * @param <S>       源数据类型
     * @param <T>       目标数据类型
     * @return 转换后的分页响应
     */
    public static <S, T> ResponseResultPage<T> convertFromMybatisPage(
            Page<S> page, Function<S, T> converter) {

        if (page == null) {
            return ResponseResultPage.empty();
        }

        // 先转换数据
        Page<T> convertedPage = convertPage(page, converter);

        // 再转换为响应分页
        return ResponseResultPage.ok(convertedPage);
    }

    /**
     * 确定当前页码
     * <p>
     * 优先级: 路径参数 > 查询参数 > 默认值1
     */
    public static long determineCurrentPage(Long pathCurrent, Long queryPage) {
        if (pathCurrent != null && pathCurrent > 0) {
            return pathCurrent;
        }
        if (queryPage != null && queryPage > 0) {
            return queryPage;
        }
        return 1;
    }

    /**
     * 确定每页条数
     * <p>
     * 优先级: 路径参数 > 查询参数 > 默认值10
     */
    public static long determinePageSize(Long pathPageSize, Long querySize) {
        if (pathPageSize != null && pathPageSize > 0) {
            return Math.min(pathPageSize, 1000); // 限制最大1000条
        }
        if (querySize != null && querySize > 0) {
            return Math.min(querySize, 1000); // 限制最大1000条
        }
        return 10;
    }
}