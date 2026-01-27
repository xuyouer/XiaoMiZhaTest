package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.utils.user.UserUtils;
import ltd.xiaomizha.xuyou.user.entity.UserNames;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserNamesMapper;
import ltd.xiaomizha.xuyou.user.service.UserNamesService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_names(用户名信息表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
@Slf4j
public class UserNamesServiceImpl extends ServiceImpl<UserNamesMapper, UserNames>
        implements UserNamesService {

    @Resource
    private UsersService usersService;

    @Override
    public Page<UserNames> getNamesPage(Integer current, Integer pageSize) {
        Page<UserNames> page = new Page<>(current, pageSize);
        return this.page(page);
    }

    @Override
    public UserNames getNameById(Integer nameId) {
        if (nameId == null || nameId <= 0) {
            throw new IllegalArgumentException("用户名ID不能为空且必须大于0");
        }
        UserNames userName = this.getById(nameId);
        if (userName == null) {
            throw new RuntimeException("用户名信息不存在");
        }
        return userName;
    }

    @Override
    public boolean addName(UserNames userNames) {
        if (userNames == null) {
            throw new IllegalArgumentException("用户名信息不能为空");
        }
        // 验证用户是否存在
        Integer userId = userNames.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证用户是否已存在用户名记录
        QueryWrapper<UserNames> existingWrapper = new QueryWrapper<>();
        existingWrapper.eq("user_id", userId);
        if (this.count(existingWrapper) > 0) {
            throw new RuntimeException("用户已存在用户名记录");
        }
        // 验证必填字段
        if (userNames.getDisplayName() == null || userNames.getDisplayName().isEmpty()) {
            throw new IllegalArgumentException("显示名不能为空");
        }
        // 验证create_name唯一性
        if (userNames.getCreateName() == null || userNames.getCreateName().isEmpty()) {
            // 生成唯一create_name
            userNames.setCreateName(UserUtils.generateCreateName());
        } else {
            QueryWrapper<UserNames> createNameWrapper = new QueryWrapper<>();
            createNameWrapper.eq("create_name", userNames.getCreateName());
            if (this.count(createNameWrapper) > 0) {
                throw new RuntimeException("创建用户名已存在");
            }
        }
        // 验证is_default_display只能是0或1
        if (userNames.getIsDefaultDisplay() == null) {
            userNames.setIsDefaultDisplay(1); // 默认使用显示名
        } else if (userNames.getIsDefaultDisplay() != 0 && userNames.getIsDefaultDisplay() != 1) {
            throw new IllegalArgumentException("是否默认显示只能是0或1");
        }
        return this.save(userNames);
    }

    @Override
    public boolean updateName(Integer nameId, UserNames userNames) {
        if (nameId == null || nameId <= 0) {
            throw new IllegalArgumentException("用户名ID不能为空且必须大于0");
        }
        if (userNames == null) {
            throw new IllegalArgumentException("用户名信息不能为空");
        }
        // 验证用户名信息是否存在
        if (!this.existsById(nameId)) {
            throw new RuntimeException("用户名信息不存在");
        }
        // 验证用户是否存在
        Integer userId = userNames.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            // 验证用户是否已存在其他用户名记录
            QueryWrapper<UserNames> existingWrapper = new QueryWrapper<>();
            existingWrapper.eq("user_id", userId)
                    .ne("name_id", nameId);
            if (this.count(existingWrapper) > 0) {
                throw new RuntimeException("该用户已存在其他用户名记录");
            }
        }
        // 验证必填字段
        if (userNames.getDisplayName() != null && userNames.getDisplayName().isEmpty()) {
            throw new IllegalArgumentException("显示名不能为空");
        }
        // 验证create_name唯一性
        if (userNames.getCreateName() != null && !userNames.getCreateName().isEmpty()) {
            QueryWrapper<UserNames> createNameWrapper = new QueryWrapper<>();
            createNameWrapper.eq("create_name", userNames.getCreateName())
                    .ne("name_id", nameId);
            if (this.count(createNameWrapper) > 0) {
                throw new RuntimeException("创建用户名已存在");
            }
        }
        // 验证is_default_display只能是0或1
        if (userNames.getIsDefaultDisplay() != null && userNames.getIsDefaultDisplay() != 0 && userNames.getIsDefaultDisplay() != 1) {
            throw new IllegalArgumentException("是否默认显示只能是0或1");
        }
        userNames.setNameId(nameId);
        return this.updateById(userNames);
    }

    @Override
    public boolean deleteName(Integer nameId) {
        if (nameId == null || nameId <= 0) {
            throw new IllegalArgumentException("用户名ID不能为空且必须大于0");
        }
        // 验证用户名信息是否存在
        if (!this.existsById(nameId)) {
            throw new RuntimeException("用户名信息不存在");
        }
        return this.removeById(nameId);
    }

    @Override
    public boolean createDefaultUserName(Integer userId, String username) {
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
        // 生成唯一create_name
        String createName = UserUtils.generateCreateName();

        // 添加user_names记录
        UserNames userNames = new UserNames();
        userNames.setUserId(userId);
        userNames.setCreateName(createName); // 默认创建用户名
        userNames.setDisplayName(username); // 默认显示名暂时为用户登录名
        userNames.setIsDefaultDisplay(UserConstants.IS_DEFAULT_DISPLAY_YES); // 默认使用显示名显示

        return this.save(userNames);
    }

    @Override
    public UserNames getUserNameByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserNames> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean batchAddNames(List<UserNames> userNamesList) {
        if (userNamesList == null || userNamesList.isEmpty()) {
            throw new IllegalArgumentException("用户名信息列表不能为空");
        }
        for (UserNames userName : userNamesList) {
            if (userName == null) {
                throw new IllegalArgumentException("用户名信息列表中包含空记录");
            }
            // 验证用户是否存在
            Integer userId = userName.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
            // 验证必填字段
            if (userName.getDisplayName() == null || userName.getDisplayName().isEmpty()) {
                throw new IllegalArgumentException("显示名不能为空");
            }
            if (userName.getCreateName() == null || userName.getCreateName().isEmpty()) {
                // 生成唯一create_name
                userName.setCreateName(UserUtils.generateCreateName());
            }
        }
        return this.saveBatch(userNamesList);
    }

    @Override
    public boolean batchUpdateNames(List<UserNames> userNamesList) {
        if (userNamesList == null || userNamesList.isEmpty()) {
            throw new IllegalArgumentException("用户名信息列表不能为空");
        }
        for (UserNames userName : userNamesList) {
            if (userName == null || userName.getNameId() == null || userName.getNameId() <= 0) {
                throw new IllegalArgumentException("用户名信息列表中包含无效记录");
            }
            // 验证用户名信息是否存在
            if (!this.existsById(userName.getNameId())) {
                throw new RuntimeException("用户名信息不存在: " + userName.getNameId());
            }
            // 验证用户是否存在
            Integer userId = userName.getUserId();
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
            if (userName.getDisplayName() != null && userName.getDisplayName().isEmpty()) {
                throw new IllegalArgumentException("显示名不能为空");
            }
        }
        return this.updateBatchById(userNamesList);
    }

    @Override
    public boolean batchDeleteNames(List<Integer> nameIds) {
        if (nameIds == null || nameIds.isEmpty()) {
            throw new IllegalArgumentException("用户名ID列表不能为空");
        }
        // 验证列表中的用户名信息是否都存在
        for (Integer nameId : nameIds) {
            if (nameId == null || nameId <= 0) {
                throw new IllegalArgumentException("用户名ID列表中包含无效ID");
            }
            if (!this.existsById(nameId)) {
                throw new RuntimeException("用户名信息不存在: " + nameId);
            }
        }
        return this.removeByIds(nameIds);
    }

    @Override
    public boolean isUsernameUnique(String username, Integer excludeId) {
        if (username == null || username.isEmpty()) {
            return true;
        }
        QueryWrapper<UserNames> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("display_name", username);
        if (excludeId != null) {
            queryWrapper.ne("name_id", excludeId);
        }
        return this.count(queryWrapper) == 0;
    }

    private boolean existsById(Integer nameId) {
        if (nameId == null) {
            return false;
        }
        return this.getById(nameId) != null;
    }

}