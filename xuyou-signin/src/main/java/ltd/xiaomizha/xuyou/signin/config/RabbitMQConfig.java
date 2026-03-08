package ltd.xiaomizha.xuyou.signin.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 */
@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String SIGN_IN_EXCHANGE = "signin.exchange";

    // 队列名称
    public static final String SIGN_IN_REWARD_QUEUE = "signin.reward.queue";

    // 路由键
    public static final String SIGN_IN_REWARD_ROUTING_KEY = "signin.reward";

    /**
     * 声明交换机
     */
    @Bean
    public DirectExchange signInExchange() {
        return new DirectExchange(SIGN_IN_EXCHANGE, true, false);
    }

    /**
     * 声明签到积分奖励队列
     */
    @Bean
    public Queue signInRewardQueue() {
        return new Queue(SIGN_IN_REWARD_QUEUE, true, false, false);
    }

    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding signInRewardBinding(Queue signInRewardQueue, DirectExchange signInExchange) {
        return BindingBuilder.bind(signInRewardQueue).to(signInExchange).with(SIGN_IN_REWARD_ROUTING_KEY);
    }
}
