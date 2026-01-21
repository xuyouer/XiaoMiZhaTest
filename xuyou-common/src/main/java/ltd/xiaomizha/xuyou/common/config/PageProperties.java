package ltd.xiaomizha.xuyou.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 分页配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "monopoly.page")
public class PageProperties {

    /**
     * 是否启用分页
     */
    private boolean enable = true;

    /**
     * 默认当前页
     */
    private long defaultCurrent = 1;

    /**
     * 默认每页大小
     */
    private long defaultPageSize = 10;

    /**
     * 最大每页大小
     */
    private long maxPageSize = 1000;

    /**
     * 最小每页大小
     */
    private long minPageSize = 1;

    /**
     * 是否优化count查询
     */
    private boolean optimizeCountSql = true;

    /**
     * 是否启用排序
     */
    private boolean enableOrderBy = true;

    /**
     * 排序字段白名单
     * <p>
     * 用逗号分隔
     */
    private String orderFieldWhitelist = "id,create_time,update_time";

    /**
     * 页码参数名列表
     * <p>
     * 用逗号分隔
     */
    private String pageParamNames = "current,page,pageNo,pageNum";

    /**
     * 页大小参数名列表
     * <p>
     * 用逗号分隔
     */
    private String sizeParamNames = "pageSize,size,limit,perPage,pageLength";

    /**
     * 获取排序字段白名单列表
     */
    public List<String> getOrderFieldWhitelistAsList() {
        return Arrays.asList(orderFieldWhitelist.split(","));
    }

    /**
     * 获取页码参数名列表
     */
    public List<String> getPageParamNamesAsList() {
        return Arrays.asList(pageParamNames.split(","));
    }

    /**
     * 获取页大小参数名列表
     */
    public List<String> getSizeParamNamesAsList() {
        return Arrays.asList(sizeParamNames.split(","));
    }
}