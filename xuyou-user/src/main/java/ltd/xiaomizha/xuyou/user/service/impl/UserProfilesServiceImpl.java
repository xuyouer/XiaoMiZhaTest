package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserProfiles;
import ltd.xiaomizha.xuyou.user.service.UserProfilesService;
import ltd.xiaomizha.xuyou.user.mapper.UserProfilesMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaom
* @description 针对表【user_profiles(用户表)】的数据库操作Service实现
* @createDate 2026-01-21 19:16:15
*/
@Service
public class UserProfilesServiceImpl extends ServiceImpl<UserProfilesMapper, UserProfiles>
    implements UserProfilesService{

}




