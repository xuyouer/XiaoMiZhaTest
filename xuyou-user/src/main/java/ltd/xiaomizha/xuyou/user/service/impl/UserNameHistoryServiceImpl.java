package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserNameHistory;
import ltd.xiaomizha.xuyou.user.service.UserNameHistoryService;
import ltd.xiaomizha.xuyou.user.mapper.UserNameHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaom
* @description 针对表【user_name_history(用户名变更历史表)】的数据库操作Service实现
* @createDate 2026-01-21 19:16:15
*/
@Service
public class UserNameHistoryServiceImpl extends ServiceImpl<UserNameHistoryMapper, UserNameHistory>
    implements UserNameHistoryService{

}




