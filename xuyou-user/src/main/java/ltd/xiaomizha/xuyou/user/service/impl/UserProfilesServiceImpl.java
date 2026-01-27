package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.user.entity.UserProfiles;
import ltd.xiaomizha.xuyou.user.mapper.UserProfilesMapper;
import ltd.xiaomizha.xuyou.user.service.UserProfilesService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_profiles(用户表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
public class UserProfilesServiceImpl extends ServiceImpl<UserProfilesMapper, UserProfiles>
        implements UserProfilesService {

    @Override
    public boolean createDefaultUserProfile(Integer userId, String username) {
        // 添加user_profiles记录
        UserProfiles userProfiles = new UserProfiles();
        userProfiles.setUserId(userId);
        userProfiles.setNickname(username);

        return this.save(userProfiles);
    }

    @Override
    public UserProfiles getUserProfileByUserId(Integer userId) {
        QueryWrapper<UserProfiles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

}




