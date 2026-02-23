package ltd.xiaomizha.xuyou.user.event;

import ltd.xiaomizha.xuyou.user.entity.Users;
import org.springframework.context.ApplicationEvent;

/**
 * 用户注册成功事件
 * <p>
 * 当用户注册成功后发布此事件, 用于触发后续的默认数据创建操作
 * </p>
 */
public class UserRegisteredEvent extends ApplicationEvent {

    private final Users users;

    /**
     * 构造函数
     *
     * @param source 事件源
     * @param users  用户信息
     */
    public UserRegisteredEvent(Object source, Users users) {
        super(source);
        this.users = users;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public Users getUsers() {
        return users;
    }
}
