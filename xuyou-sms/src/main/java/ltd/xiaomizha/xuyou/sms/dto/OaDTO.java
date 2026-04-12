package ltd.xiaomizha.xuyou.sms.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class OaDTO {

    /**
     * OA 配置标识
     * <p>
     * 对应 application-sms.yml 中 oas 节点的配置名
     */
    @Value("${oa.config-id:oaDingTalkByYaml}")
    private String oaConfigId;

}
