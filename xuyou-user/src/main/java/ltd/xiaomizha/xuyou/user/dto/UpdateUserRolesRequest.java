package ltd.xiaomizha.xuyou.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 更新用户角色请求DTO
 */
@Data
public class UpdateUserRolesRequest {
    @JsonProperty("roleIds")
    private List<Integer> roleIds;
}
