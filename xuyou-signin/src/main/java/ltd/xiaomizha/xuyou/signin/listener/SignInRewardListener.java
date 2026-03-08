package ltd.xiaomizha.xuyou.signin.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 签到积分奖励消息处理器
 */
@Slf4j
@Component
public class SignInRewardListener {

    /**
     * 处理签到积分奖励消息
     *
     * @param message     消息内容
     * @param channel     通道
     * @param deliveryTag 投递标签
     */
    @RabbitListener(queues = "signin.reward.queue")
    public void handleSignInRewardMessage(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("接收到签到积分奖励消息: {}", message);

            // 从消息中获取用户ID和积分奖励
            Long userId = (Long) message.get("userId");
            Integer pointsReward = (Integer) message.get("pointsReward");
            Integer continuousDays = (Integer) message.get("continuousDays");

            // TODO: 积分发放 更新用户积分、记录积分变动日志
            log.info("为用户 {} 发放签到积分奖励 {}, 连续签到 {} 天", userId, pointsReward, continuousDays);

            // 确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理签到积分奖励消息失败", e);
            try {
                // 拒绝消息, 不重新入队
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("拒绝消息失败", ex);
            }
        }
    }

}
