package ltd.xiaomizha.xuyou.common.config;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltd.xiaomizha.xuyou.common.utils.mybatis.PageUtils;

import java.util.List;


/**
 * 分页配置类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageConfig {

    /**
     * 当前页码
     */
    private long current;

    /**
     * 每页大小
     */
    private long pageSize;

    /**
     * 排序信息
     */
    private List<OrderItem> orders;

    /**
     * 计算偏移量
     */
    public long getOffset() {
        return PageUtils.calculateOffset(current, pageSize);
    }

    /**
     * 验证参数是否有效
     */
    public boolean isValid() {
        return PageUtils.isValidParams(current, pageSize);
    }

}
