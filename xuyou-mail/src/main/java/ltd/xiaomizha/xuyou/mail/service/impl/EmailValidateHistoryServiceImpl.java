package ltd.xiaomizha.xuyou.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.entity.Action;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;
import ltd.xiaomizha.xuyou.mail.entity.EmailValidateHistory;
import ltd.xiaomizha.xuyou.mail.mapper.EmailValidateHistoryMapper;
import ltd.xiaomizha.xuyou.mail.service.EmailValidateHistoryService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【email_validate_history(邮箱验证历史记录表)】的数据库操作Service实现
 * @createDate 2026-04-05 19:08:18
 */
@Slf4j
@Service
public class EmailValidateHistoryServiceImpl extends ServiceImpl<EmailValidateHistoryMapper, EmailValidateHistory>
        implements EmailValidateHistoryService {

    /**
     * 保存操作历史记录
     *
     * @param userId       用户ID
     * @param email        邮箱地址
     * @param actionType   操作类型
     * @param tokenType    Token类型
     * @param result       操作结果
     * @param errorMessage 错误信息
     * @param clientIp     客户端IP
     */
    @Override
    public void saveHistory(Long userId, String email, Action actionType, TokenType tokenType, boolean result, String errorMessage, String clientIp) {
        try {
            EmailValidateHistory history = new EmailValidateHistory();
            history.setUserId(userId);
            history.setEmail(email);
            history.setActionType(actionType);
            history.setTokenType(tokenType);
            history.setResult(result ? 1 : 0);
            history.setErrorMessage(errorMessage);
            history.setClientIp(clientIp);

            this.save(history);

            log.debug("操作历史已保存: actionType={}, result={}, email={}", actionType, result, email);
        } catch (Exception e) {
            log.error("保存操作历史失败", e);
        }
    }
}




