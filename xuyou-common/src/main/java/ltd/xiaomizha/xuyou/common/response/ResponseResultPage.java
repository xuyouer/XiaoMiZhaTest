package ltd.xiaomizha.xuyou.common.response;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.PageConstants;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.utils.http.ServletUtil;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 分页响应结果
 *
 * @param <T> 数据类型
 */
@Slf4j
@Getter
@Setter
public class ResponseResultPage<T> extends ResponseResult<List<T>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     * <p>
     * 从1开始
     */
    private long current;

    /**
     * 每页数量
     */
    private long pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 是否还有下一页
     */
    private boolean hasNext;

    /**
     * 是否还有上一页
     */
    private boolean hasPrevious;

    /**
     * 是否第一页
     */
    private boolean isFirst;

    /**
     * 是否最后一页
     */
    private boolean isLast;

    /**
     * 成功响应分页数据
     *
     * @param page MyBatis Plus分页对象
     * @param <T>  数据类型
     * @return 分页响应结果
     */
    public static <T> ResponseResultPage<T> ok(Page<T> page) {
        ResponseResultPage<T> result = new ResponseResultPage<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(ResultEnum.SUCCESS.getMessage());
        result.setData(page.getRecords());
        result.setCurrent(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotal(page.getTotal());

        long pages = page.getPages();
        result.setPages(pages);
        boolean hasNext = page.getCurrent() < pages;
        result.setHasNext(hasNext);
        boolean hasPrevious = page.getCurrent() > 1;
        result.setHasPrevious(hasPrevious);
        result.setFirst(!hasPrevious);
        result.setLast(!hasNext);

        return result;
    }

    /**
     * 成功响应分页数据
     * <p>
     * 自定义数据
     *
     * @param data     数据列表
     * @param current  当前页
     * @param pageSize 每页大小
     * @param total    总记录数
     * @param <T>      数据类型
     * @return 分页响应结果
     */
    public static <T> ResponseResultPage<T> ok(List<T> data, long current, long pageSize, long total) {
        ResponseResultPage<T> result = new ResponseResultPage<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(ResultEnum.SUCCESS.getMessage());
        result.setData(data);
        result.setCurrent(current);
        result.setPageSize(pageSize);
        result.setTotal(total);

        long pages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
        result.setPages(pages);
        boolean hasNext = current < pages;
        result.setHasNext(hasNext);
        boolean hasPrevious = current > 1;
        result.setHasPrevious(hasPrevious);
        result.setFirst(!hasPrevious);
        result.setLast(!hasNext);

        return result;
    }

    /**
     * 成功响应空分页数据
     *
     * @param <T> 数据类型
     * @return 空分页响应结果
     */
    public static <T> ResponseResultPage<T> empty() {
        return ok(new ArrayList<>(),
                PageConstants.DefaultValue.CURRENT,
                PageConstants.DefaultValue.PAGE_SIZE,
                0);
    }

    /**
     * 获取请求分页对象
     * <p>
     * 从Request请求对象中获取分页参数
     *
     * @param <T> 数据类型
     * @return MyBatis Plus分页对象
     */
    public static <T> Page<T> getPage() {
        return getPage(PageConstants.DefaultValue.CURRENT,
                PageConstants.DefaultValue.PAGE_SIZE);
    }

    /**
     * 获取请求分页对象
     * <p>
     * 带默认值
     *
     * @param defaultCurrent  默认当前页
     * @param defaultPageSize 默认每页大小
     * @param <T>             数据类型
     * @return MyBatis Plus分页对象
     */
    public static <T> Page<T> getPage(long defaultCurrent, long defaultPageSize) {
        Page<T> page = new Page<>();

        // 获取并校验当前页
        long current = parsePageParam(
                PageConstants.ParamName.CURRENT,
                defaultCurrent,
                PageConstants.DefaultValue.MIN_CURRENT,
                Long.MAX_VALUE
        );

        // 尝试其他常见的页码参数名
        current = tryAlternativePageParams(current, defaultCurrent);

        // 获取并校验每页大小
        long pageSize = parsePageParam(
                PageConstants.ParamName.PAGE_SIZE,
                defaultPageSize,
                PageConstants.DefaultValue.MIN_PAGE_SIZE,
                PageConstants.DefaultValue.MAX_PAGE_SIZE
        );

        // 尝试其他常见的页大小参数名
        pageSize = tryAlternativeSizeParams(pageSize, defaultPageSize);

        // 设置分页参数
        page.setCurrent(current);
        page.setSize(pageSize);

        // 设置排序
        applyOrderBy(page);

        log.debug("创建分页对象: current={}, size={}", current, pageSize);
        return page;
    }

    /**
     * 尝试其他页码参数名
     */
    private static long tryAlternativePageParams(long currentValue, long defaultValue) {
        if (currentValue != defaultValue) {
            return currentValue;
        }

        // 尝试其他页码参数名
        String[] pageParamNames = {
                PageConstants.ParamName.PAGE,
                PageConstants.ParamName.PAGE_NO,
                PageConstants.ParamName.PAGE_NUM
        };

        for (String paramName : pageParamNames) {
            long value = parsePageParam(
                    paramName,
                    defaultValue,
                    PageConstants.DefaultValue.MIN_CURRENT,
                    Long.MAX_VALUE
            );
            if (value != defaultValue) {
                return value;
            }
        }

        return defaultValue;
    }

    /**
     * 尝试其他页大小参数名
     */
    private static long tryAlternativeSizeParams(long pageSizeValue, long defaultValue) {
        if (pageSizeValue != defaultValue) {
            return pageSizeValue;
        }

        // 尝试其他页大小参数名
        String[] sizeParamNames = {
                PageConstants.ParamName.SIZE,
                PageConstants.ParamName.LIMIT,
                PageConstants.ParamName.PER_PAGE,
                PageConstants.ParamName.PAGE_LENGTH
        };

        for (String paramName : sizeParamNames) {
            long value = parsePageParam(
                    paramName,
                    defaultValue,
                    PageConstants.DefaultValue.MIN_PAGE_SIZE,
                    PageConstants.DefaultValue.MAX_PAGE_SIZE
            );
            if (value != defaultValue) {
                return value;
            }
        }

        return defaultValue;
    }

    /**
     * 解析分页参数
     *
     * @param paramName    参数名
     * @param defaultValue 默认值
     * @param minValue     最小值
     * @param maxValue     最大值
     * @return 解析后的参数值
     */
    private static long parsePageParam(String paramName, long defaultValue, long minValue, long maxValue) {
        try {
            String paramValue = ServletUtil.getParameterFromRequest(paramName);
            if (StringUtils.hasText(paramValue) && NumberUtil.isNumber(paramValue)) {
                long value = Long.parseLong(paramValue.trim());

                // 边界检查
                if (value < minValue) {
                    log.warn(PageConstants.ErrorMessage.INVALID_PAGE_NUMBER +
                                    ", 参数名: {}, 值: {}, 使用最小值: {}",
                            paramName, value, minValue);
                    return minValue;
                }
                if (value > maxValue) {
                    log.warn(PageConstants.ErrorMessage.PAGE_SIZE_TOO_LARGE.replace("{max}", String.valueOf(maxValue)) +
                                    ", 参数名: {}, 值: {}, 使用最大值: {}",
                            paramName, value, maxValue);
                    return maxValue;
                }
                return value;
            }
        } catch (NumberFormatException e) {
            log.warn("分页参数 {} 格式错误: {}", paramName, e.getMessage());
        } catch (Exception e) {
            log.warn("获取分页参数 {} 异常: {}", paramName, e.getMessage());
        }
        return defaultValue;
    }

    /**
     * 应用排序参数
     *
     * @param page 分页对象
     * @param <T>  数据类型
     */
    private static <T> void applyOrderBy(Page<T> page) {
        try {
            // 尝试获取排序字段
            String orderField = Optional.ofNullable(ServletUtil.getParameterFromRequest(PageConstants.ParamName.ORDER_BY))
                    .orElseGet(() -> ServletUtil.getParameterFromRequest(PageConstants.ParamName.SORT));

            if (StringUtils.hasText(orderField)) {
                // 获取排序类型(asc/desc)
                String orderType = Optional.ofNullable(ServletUtil.getParameterFromRequest(PageConstants.ParamName.ORDER_TYPE))
                        .orElse(PageConstants.Sort.ASC);

                boolean isAsc = PageConstants.Sort.ASC.equalsIgnoreCase(orderType.trim())
                        || PageConstants.Sort.ASC_NUM.equals(orderType.trim());

                // 验证排序字段
                if (isValidOrderField(orderField)) {
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
                            log.debug("设置排序: field={}, asc={}", trimmedField, isAsc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析排序参数异常: {}", e.getMessage());
        }
    }


    /**
     * 验证排序字段是否合法
     * <p>
     * 防止SQL注入
     *
     * @param orderField 排序字段
     * @return 是否合法
     */
    protected static boolean isValidOrderField(String orderField) {
        if (!StringUtils.hasText(orderField)) {
            return false;
        }

        String trimmed = orderField.trim();

        // 基础校验: 符合正则表达式
        if (!trimmed.matches(PageConstants.Regex.ORDER_FIELD)) {
            log.warn("排序字段不符合规范: {}", orderField);
            return false;
        }

        // TODO 更多的校验逻辑
        // TODO 例如: 检查字段是否在白名单中

        return true;
    }

    /**
     * 获取分页参数
     * <p>
     * 不创建Page对象
     *
     * @return 分页参数对象
     */
    public static PageParams getPageParams() {
        return getPageParams(PageConstants.DefaultValue.CURRENT,
                PageConstants.DefaultValue.PAGE_SIZE);
    }

    /**
     * 获取分页参数
     * <p>
     * 不创建Page对象, 带默认值
     *
     * @param defaultCurrent  默认当前页
     * @param defaultPageSize 默认每页大小
     * @return 分页参数对象
     */
    public static PageParams getPageParams(long defaultCurrent, long defaultPageSize) {
        Page<?> page = getPage(defaultCurrent, defaultPageSize);
        return new PageParams(page.getCurrent(), page.getSize());
    }

    /**
     * 分页参数封装类
     */
    @Getter
    public static class PageParams {
        private final long current;
        private final long pageSize;
        private final long offset;

        public PageParams(long current, long pageSize) {
            this.current = Math.max(current, PageConstants.DefaultValue.MIN_CURRENT);
            this.pageSize = Math.max(
                    Math.min(pageSize, PageConstants.DefaultValue.MAX_PAGE_SIZE),
                    PageConstants.DefaultValue.MIN_PAGE_SIZE
            );
            this.offset = (this.current - 1) * this.pageSize;
        }
    }

    /**
     * 转换为MyBatis Plus分页对象
     * <p>
     * 用于后续查询
     *
     * @param <T> 数据类型
     * @return MyBatis Plus分页对象
     */
    public <T> Page<T> toMybatisPage() {
        Page<T> page = new Page<>(this.current, this.pageSize);
        page.setTotal(this.total);
        return page;
    }

    /**
     * 是否为空分页
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return ObjectUtil.isEmpty(this.getData()) || this.total == 0;
    }

    /**
     * 获取实际数据数量
     * <p>
     * 当前页
     *
     * @return 当前页数据数量
     */
    public int getCurrentSize() {
        return ObjectUtil.isNotEmpty(this.getData()) ? this.getData().size() : 0;
    }

    /**
     * 转换为Map结构
     * <p>
     * 便于前端使用
     *
     * @return Map结构的分页数据
     */
    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put(PageConstants.ResponseField.DATA, this.getData());
        map.put(PageConstants.ResponseField.CURRENT, this.current);
        map.put(PageConstants.ResponseField.PAGE_SIZE, this.pageSize);
        map.put(PageConstants.ResponseField.TOTAL, this.total);
        map.put(PageConstants.ResponseField.PAGES, this.pages);
        map.put(PageConstants.ResponseField.HAS_NEXT, this.hasNext);
        map.put(PageConstants.ResponseField.HAS_PREVIOUS, this.hasPrevious);
        map.put(PageConstants.ResponseField.IS_FIRST, this.isFirst);
        map.put(PageConstants.ResponseField.IS_LAST, this.isLast);
        return map;
    }
}
