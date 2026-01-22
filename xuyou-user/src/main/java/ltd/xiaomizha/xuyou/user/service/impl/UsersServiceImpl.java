package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import ltd.xiaomizha.xuyou.user.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaom
* @description 针对表【users(用户表)】的数据库操作Service实现
* @createDate 2026-01-21 19:16:15
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

}




