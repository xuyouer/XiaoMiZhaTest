package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.entity.UserProfiles;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserProfilesMapper;
import ltd.xiaomizha.xuyou.user.service.UserProfilesService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_profiles(用户表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
@Slf4j
public class UserProfilesServiceImpl extends ServiceImpl<UserProfilesMapper, UserProfiles>
        implements UserProfilesService {

    @Resource
    private UsersService usersService;

    @Override
    public Page<UserProfiles> getProfilesPage(Integer current, Integer pageSize) {
        Page<UserProfiles> page = new Page<>(current, pageSize);
        QueryWrapper<UserProfiles> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        return this.page(page, wrapper);
    }

    @Override
    public UserProfiles getProfileById(Integer profileId) {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("资料ID不能为空且必须大于0");
        }
        UserProfiles profile = this.getById(profileId);
        if (profile == null) {
            throw new RuntimeException("用户资料不存在");
        }
        return profile;
    }

    @Override
    public boolean addProfile(UserProfiles userProfiles) {
        if (userProfiles == null) {
            throw new IllegalArgumentException("用户资料信息不能为空");
        }
        // 验证用户是否存在
        Integer userId = userProfiles.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证是否已存在用户资料
        UserProfiles existingProfile = this.getUserProfileByUserId(userId);
        if (existingProfile != null) {
            throw new RuntimeException("用户资料已存在");
        }
        // 验证必填字段
        if (userProfiles.getNickname() == null || userProfiles.getNickname().isEmpty()) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        // 验证昵称长度
        if (userProfiles.getNickname().length() > 50) {
            throw new IllegalArgumentException("昵称长度不能超过50个字符");
        }
        // 验证邮箱格式和唯一性
        String email = userProfiles.getEmail();
        if (email != null) {
            if (email.isEmpty()) {
                throw new IllegalArgumentException("邮箱不能为空");
            }
            // 简单邮箱格式验证
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
            // 验证邮箱唯一性
            QueryWrapper<UserProfiles> emailWrapper = new QueryWrapper<>();
            emailWrapper.eq("email", email);
            if (this.count(emailWrapper) > 0) {
                throw new RuntimeException("该邮箱已被其他用户使用");
            }
        }
        // 验证手机号格式和唯一性
        String phone = userProfiles.getPhone();
        if (phone != null) {
            if (phone.isEmpty()) {
                throw new IllegalArgumentException("手机号不能为空");
            }
            // 简单手机号格式验证
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
            // 验证手机号唯一性
            QueryWrapper<UserProfiles> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("phone", phone);
            if (this.count(phoneWrapper) > 0) {
                throw new RuntimeException("该手机号已被其他用户使用");
            }
        }
        // 验证头像URL长度
        String avatarUrl = userProfiles.getAvatarUrl();
        if (avatarUrl != null && avatarUrl.length() > 255) {
            throw new IllegalArgumentException("头像URL长度不能超过255个字符");
        }
        // 验证个人简介长度
        String bio = userProfiles.getBio();
        if (bio != null && bio.length() > 255) {
            throw new IllegalArgumentException("个人简介长度不能超过255个字符");
        }
        return this.save(userProfiles);
    }

    @Override
    public boolean updateProfile(Integer profileId, UserProfiles userProfiles) {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("资料ID不能为空且必须大于0");
        }
        if (userProfiles == null) {
            throw new IllegalArgumentException("用户资料信息不能为空");
        }
        // 验证资料是否存在
        if (!this.existsById(profileId)) {
            throw new RuntimeException("用户资料不存在");
        }
        // 验证用户是否存在
        Integer userId = userProfiles.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            // 验证用户是否已存在其他资料记录
            QueryWrapper<UserProfiles> existingWrapper = new QueryWrapper<>();
            existingWrapper.eq("user_id", userId)
                    .ne("profile_id", profileId);
            if (this.count(existingWrapper) > 0) {
                throw new RuntimeException("该用户已存在其他资料记录");
            }
        }
        // 验证必填字段
        if (userProfiles.getNickname() != null) {
            if (userProfiles.getNickname().isEmpty()) {
                throw new IllegalArgumentException("昵称不能为空");
            }
            // 验证昵称长度
            if (userProfiles.getNickname().length() > 50) {
                throw new IllegalArgumentException("昵称长度不能超过50个字符");
            }
        }
        // 验证邮箱格式和唯一性
        String email = userProfiles.getEmail();
        if (email != null) {
            if (email.isEmpty()) {
                throw new IllegalArgumentException("邮箱不能为空");
            }
            // 简单邮箱格式验证
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
            // 验证邮箱唯一性
            QueryWrapper<UserProfiles> emailWrapper = new QueryWrapper<>();
            emailWrapper.eq("email", email)
                    .ne("profile_id", profileId);
            if (this.count(emailWrapper) > 0) {
                throw new RuntimeException("该邮箱已被其他用户使用");
            }
        }
        // 验证手机号格式和唯一性
        String phone = userProfiles.getPhone();
        if (phone != null) {
            if (phone.isEmpty()) {
                throw new IllegalArgumentException("手机号不能为空");
            }
            // 简单手机号格式验证
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new IllegalArgumentException("手机号格式不正确");
            }
            // 验证手机号唯一性
            QueryWrapper<UserProfiles> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("phone", phone)
                    .ne("profile_id", profileId);
            if (this.count(phoneWrapper) > 0) {
                throw new RuntimeException("该手机号已被其他用户使用");
            }
        }
        // 验证头像URL长度
        String avatarUrl = userProfiles.getAvatarUrl();
        if (avatarUrl != null && avatarUrl.length() > 255) {
            throw new IllegalArgumentException("头像URL长度不能超过255个字符");
        }
        // 验证个人简介长度
        String bio = userProfiles.getBio();
        if (bio != null && bio.length() > 255) {
            throw new IllegalArgumentException("个人简介长度不能超过255个字符");
        }
        userProfiles.setProfileId(profileId);
        return this.updateById(userProfiles);
    }

    @Override
    public boolean deleteProfile(Integer profileId) {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("资料ID不能为空且必须大于0");
        }
        // 验证资料是否存在
        if (!this.existsById(profileId)) {
            throw new RuntimeException("用户资料不存在");
        }
        return this.removeById(profileId);
    }

    @Override
    public boolean createDefaultUserProfile(Integer userId, String username) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证是否已存在用户资料
        UserProfiles existingProfile = this.getUserProfileByUserId(userId);
        if (existingProfile != null) {
            throw new RuntimeException("用户资料已存在");
        }
        // 添加user_profiles记录
        UserProfiles userProfiles = new UserProfiles();
        userProfiles.setUserId(userId);
        userProfiles.setNickname(username);

        return this.save(userProfiles);
    }

    @Override
    public UserProfiles getUserProfileByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserProfiles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean batchAddProfiles(List<UserProfiles> userProfilesList) {
        if (userProfilesList == null || userProfilesList.isEmpty()) {
            throw new IllegalArgumentException("用户资料列表不能为空");
        }
        for (UserProfiles profile : userProfilesList) {
            if (profile == null) {
                throw new IllegalArgumentException("用户资料列表中包含空记录");
            }
            // 验证用户是否存在
            Integer userId = profile.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
            // 验证是否已存在用户资料
            UserProfiles existingProfile = this.getUserProfileByUserId(userId);
            if (existingProfile != null) {
                throw new RuntimeException("用户资料已存在: 用户ID " + userId);
            }
            // 验证必填字段
            if (profile.getNickname() == null || profile.getNickname().isEmpty()) {
                throw new IllegalArgumentException("昵称不能为空: 用户ID " + userId);
            }
        }
        return this.saveBatch(userProfilesList);
    }

    @Override
    public boolean batchUpdateProfiles(List<UserProfiles> userProfilesList) {
        if (userProfilesList == null || userProfilesList.isEmpty()) {
            throw new IllegalArgumentException("用户资料列表不能为空");
        }
        for (UserProfiles profile : userProfilesList) {
            if (profile == null || profile.getProfileId() == null || profile.getProfileId() <= 0) {
                throw new IllegalArgumentException("用户资料列表中包含无效记录");
            }
            // 验证资料是否存在
            if (!this.existsById(profile.getProfileId())) {
                throw new RuntimeException("用户资料不存在: " + profile.getProfileId());
            }
            // 验证用户是否存在
            Integer userId = profile.getUserId();
            if (userId != null) {
                if (userId <= 0) {
                    throw new IllegalArgumentException("用户ID必须大于0");
                }
                Users user = usersService.getById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在: " + userId);
                }
            }
            // 验证必填字段
            if (profile.getNickname() != null && profile.getNickname().isEmpty()) {
                throw new IllegalArgumentException("昵称不能为空");
            }
        }
        return this.updateBatchById(userProfilesList);
    }

    @Override
    public boolean batchDeleteProfiles(List<Integer> profileIds) {
        if (profileIds == null || profileIds.isEmpty()) {
            throw new IllegalArgumentException("资料ID列表不能为空");
        }
        // 验证列表中的资料是否都存在
        for (Integer profileId : profileIds) {
            if (profileId == null || profileId <= 0) {
                throw new IllegalArgumentException("资料ID列表中包含无效ID");
            }
            if (!this.existsById(profileId)) {
                throw new RuntimeException("用户资料不存在: " + profileId);
            }
        }
        return this.removeByIds(profileIds);
    }

    private boolean existsById(Integer profileId) {
        if (profileId == null) {
            return false;
        }
        return this.getById(profileId) != null;
    }

}