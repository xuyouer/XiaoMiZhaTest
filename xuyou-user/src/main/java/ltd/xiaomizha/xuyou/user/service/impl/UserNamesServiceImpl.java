package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserNames;
import ltd.xiaomizha.xuyou.user.service.UserNamesService;
import ltd.xiaomizha.xuyou.user.mapper.UserNamesMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaom
* @description 针对表【user_names(用户名信息表)】的数据库操作Service实现
* @createDate 2026-01-21 19:16:15
*/
@Service
public class UserNamesServiceImpl extends ServiceImpl<UserNamesMapper, UserNames>
    implements UserNamesService{

}




