package ltd.xiaomizha.xuyou.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 更新用户资源请求DTO
 */
@Data
public class UpdateUserResourcesRequest {
    @JsonProperty("resourceIds")
    private List<Integer> resourceIds;
}
