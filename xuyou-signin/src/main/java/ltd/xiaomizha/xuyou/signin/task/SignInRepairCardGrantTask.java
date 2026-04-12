package ltd.xiaomizha.xuyou.signin.task;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.signin.service.SignInRepairService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SignInRepairCardGrantTask {

    @Resource
    private SignInRepairService signInRepairService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void grantMonthlyCards() {
        log.info("开始执行每月补签卡发放定时任务");
        try {
            signInRepairService.grantMonthlyCardsToAllUsers();
            log.info("每月补签卡发放定时任务执行完成");
        } catch (Exception e) {
            log.error("每月补签卡发放定时任务执行失败: {}", e.getMessage(), e);
        }
    }

}
