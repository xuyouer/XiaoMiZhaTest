package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserPoints;
import ltd.xiaomizha.xuyou.user.service.UserPointsService;
import ltd.xiaomizha.xuyou.user.mapper.UserPointsMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaom
* @description 针对表【user_points(用户积分表)】的数据库操作Service实现
* @createDate 2026-01-21 19:16:15
*/
@Service
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints>
    implements UserPointsService{

}




