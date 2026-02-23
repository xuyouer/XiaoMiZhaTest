package ltd.xiaomizha.xuyou.user.event.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.event.UserRegisteredEvent;
import ltd.xiaomizha.xuyou.user.service.*;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 用户注册成功事件监听器
 * <p>
 * 监听用户注册成功事件, 执行默认数据创建操作
 * </p>
 */
@Slf4j
@Component
public class UserRegisteredEventListener {

    @Resource
    private UserNamesService userNamesService;

    @Resource
    private UserPointsService userPointsService;

    @Resource
    private UserProfilesService userProfilesService;

    @Resource
    private UserVipInfoService userVipInfoService;

    @Resource
    private UserRoleRelationsService userRoleRelationsService;

    /**
     * 处理用户注册成功事件
     * <p>
     * 当用户注册成功后, 创建默认的用户名、积分、资料、会员和角色关联信息
     * </p>
     *
     * @param event 用户注册成功事件
     */
    @EventListener
    @Order(1) // 设置监听器顺序
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("开始处理用户注册成功事件, 用户ID: {}", event.getUsers().getUserId());

        try {
            // 添加用户名信息
            if (!userNamesService.createDefaultUserName(event.getUsers().getUserId(), event.getUsers().getUsername())) {
                throw new RuntimeException("添加用户名信息失败");
            }

            // 添加用户积分信息
            if (!userPointsService.createDefaultUserPoints(event.getUsers().getUserId())) {
                throw new RuntimeException("添加用户积分信息失败");
            }

            // 添加用户资料信息
            if (!userProfilesService.createDefaultUserProfile(event.getUsers().getUserId(), event.getUsers().getUsername())) {
                throw new RuntimeException("添加用户资料信息失败");
            }

            // 添加用户会员信息
            if (!userVipInfoService.createDefaultUserVipInfo(event.getUsers().getUserId())) {
                throw new RuntimeException("添加用户会员信息失败");
            }

            // 添加用户角色信息
            if (!userRoleRelationsService.createDefaultUserRoleRelation(event.getUsers().getUserId())) {
                throw new RuntimeException("添加用户角色信息失败");
            }

            log.info("用户注册成功事件处理完成, 用户ID: {}", event.getUsers().getUserId());
        } catch (Exception e) {
            log.error("处理用户注册成功事件失败, 用户ID: {}", event.getUsers().getUserId(), e);
            throw e;
        }
    }
}
