package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.utils.user.UserUtils;
import ltd.xiaomizha.xuyou.user.entity.UserNames;
import ltd.xiaomizha.xuyou.user.mapper.UserNamesMapper;
import ltd.xiaomizha.xuyou.user.service.UserNamesService;
import org.springframework.stereotype.Service;

/**
 * @author xiaom
 * @description 针对表【user_names(用户名信息表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
public class UserNamesServiceImpl extends ServiceImpl<UserNamesMapper, UserNames>
        implements UserNamesService {

    @Override
    public boolean createDefaultUserName(Integer userId, String username) {
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
        QueryWrapper<UserNames> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

}




