package ltd.xiaomizha.xuyou.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.xiaomizha.xuyou.common.enums.entity.Action;
import ltd.xiaomizha.xuyou.common.enums.entity.TokenType;
import ltd.xiaomizha.xuyou.mail.entity.EmailValidateHistory;

/**
 * @author xiaom
 * @description 针对表【email_validate_history(邮箱验证历史记录表)】的数据库操作Service
 * @createDate 2026-04-05 19:08:18
 */
public interface EmailValidateHistoryService extends IService<EmailValidateHistory> {

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
    void saveHistory(Long userId, String email, Action actionType, TokenType tokenType, boolean result, String errorMessage, String clientIp);

}
