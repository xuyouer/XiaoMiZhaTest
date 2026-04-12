package ltd.xiaomizha.xuyou.sms.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SmsDTO {

    /**
     * 短信厂商标识
     * <p>
     * 对应 application-sms.yml 中的 blends 配置名
     */
    @Value("${sms.blend-name:aliyun}")
    private String blendName;

    /**
     * 验证码过期时间 (秒), 默认5分钟
     */
    @Value("${sms.code-expire:300}")
    private Integer codeExpireSeconds;

    /**
     * 同一手机号发送间隔 (秒), 默认60秒
     */
    @Value("${sms.send-interval:60}")
    private Integer sendIntervalSeconds;

    /**
     * 验证码长度, 默认6位
     */
    @Value("${sms.code-length:6}")
    private Integer codeLength;

}
