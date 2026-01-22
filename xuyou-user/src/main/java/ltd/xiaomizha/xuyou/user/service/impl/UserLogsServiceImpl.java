package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserLogs;
import ltd.xiaomizha.xuyou.user.service.UserLogsService;
import ltd.xiaomizha.xuyou.user.mapper.UserLogsMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaom
* @description 针对表【user_logs(用户操作日志表)】的数据库操作Service实现
* @createDate 2026-01-21 19:16:15
*/
@Service
public class UserLogsServiceImpl extends ServiceImpl<UserLogsMapper, UserLogs>
    implements UserLogsService{

}




