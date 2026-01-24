package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserResources;
import ltd.xiaomizha.xuyou.user.service.UserResourcesService;
import ltd.xiaomizha.xuyou.user.mapper.UserResourcesMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaom
* @description 针对表【user_resources(用户资源表)】的数据库操作Service实现
* @createDate 2026-01-24 12:37:48
*/
@Service
public class UserResourcesServiceImpl extends ServiceImpl<UserResourcesMapper, UserResources>
    implements UserResourcesService{

}




