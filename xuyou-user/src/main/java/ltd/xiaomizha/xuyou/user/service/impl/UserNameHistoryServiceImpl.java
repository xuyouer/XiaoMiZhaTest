package ltd.xiaomizha.xuyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.user.entity.UserNameHistory;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.mapper.UserNameHistoryMapper;
import ltd.xiaomizha.xuyou.user.service.UserNameHistoryService;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaom
 * @description 针对表【user_name_history(用户名变更历史表)】的数据库操作Service实现
 * @createDate 2026-01-21 19:16:15
 */
@Service
@Slf4j
public class UserNameHistoryServiceImpl extends ServiceImpl<UserNameHistoryMapper, UserNameHistory>
        implements UserNameHistoryService {

    @Resource
    private UsersService usersService;

    @Override
    public Page<UserNameHistory> getHistoriesPage(Integer current, Integer pageSize) {
        Page<UserNameHistory> page = new Page<>(current, pageSize);
        QueryWrapper<UserNameHistory> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("changed_at");
        return this.page(page, wrapper);
    }

    @Override
    public UserNameHistory getHistoryById(Integer historyId) {
        if (historyId == null || historyId <= 0) {
            throw new IllegalArgumentException("历史ID不能为空且必须大于0");
        }
        UserNameHistory history = this.getById(historyId);
        if (history == null) {
            throw new RuntimeException("用户名变更历史不存在");
        }
        return history;
    }

    @Override
    public boolean addHistory(UserNameHistory userNameHistory) {
        if (userNameHistory == null) {
            throw new IllegalArgumentException("用户名变更历史信息不能为空");
        }
        // 验证用户是否存在
        Integer userId = userNameHistory.getUserId();
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证原显示名称
        if (userNameHistory.getOldDisplayName() == null || userNameHistory.getOldDisplayName().isEmpty()) {
            throw new IllegalArgumentException("原显示名称不能为空");
        }
        // 验证新显示名称
        if (userNameHistory.getNewDisplayName() == null || userNameHistory.getNewDisplayName().isEmpty()) {
            throw new IllegalArgumentException("新显示名称不能为空");
        }
        // 验证原显示名称和新显示名称不能相同
        if (userNameHistory.getOldDisplayName().equals(userNameHistory.getNewDisplayName())) {
            throw new IllegalArgumentException("原显示名称和新显示名称不能相同");
        }
        // 验证修改人用户是否存在
        Integer changedBy = userNameHistory.getChangedBy();
        if (changedBy != null) {
            if (changedBy <= 0) {
                throw new IllegalArgumentException("修改人用户ID必须大于0");
            }
            Users changedByUser = usersService.getById(changedBy);
            if (changedByUser == null) {
                throw new RuntimeException("修改人用户不存在");
            }
        }
        return this.save(userNameHistory);
    }

    @Override
    public boolean updateHistory(Integer historyId, UserNameHistory userNameHistory) {
        if (historyId == null || historyId <= 0) {
            throw new IllegalArgumentException("历史ID不能为空且必须大于0");
        }
        if (userNameHistory == null) {
            throw new IllegalArgumentException("用户名变更历史信息不能为空");
        }
        // 验证用户名变更历史是否存在
        if (!this.existsById(historyId)) {
            throw new RuntimeException("用户名变更历史不存在");
        }
        // 验证用户是否存在
        Integer userId = userNameHistory.getUserId();
        if (userId != null) {
            if (userId <= 0) {
                throw new IllegalArgumentException("用户ID必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
        }
        // 验证原显示名称
        if (userNameHistory.getOldDisplayName() != null) {
            if (userNameHistory.getOldDisplayName().isEmpty()) {
                throw new IllegalArgumentException("原显示名称不能为空");
            }
        }
        // 验证新显示名称
        if (userNameHistory.getNewDisplayName() != null) {
            if (userNameHistory.getNewDisplayName().isEmpty()) {
                throw new IllegalArgumentException("新显示名称不能为空");
            }
        }
        // 验证原显示名称和新显示名称不能相同
        if (userNameHistory.getOldDisplayName() != null && userNameHistory.getNewDisplayName() != null) {
            if (userNameHistory.getOldDisplayName().equals(userNameHistory.getNewDisplayName())) {
                throw new IllegalArgumentException("原显示名称和新显示名称不能相同");
            }
        }
        // 验证修改人用户是否存在
        Integer changedBy = userNameHistory.getChangedBy();
        if (changedBy != null) {
            if (changedBy <= 0) {
                throw new IllegalArgumentException("修改人用户ID必须大于0");
            }
            Users changedByUser = usersService.getById(changedBy);
            if (changedByUser == null) {
                throw new RuntimeException("修改人用户不存在");
            }
        }
        userNameHistory.setHistoryId(historyId);
        return this.updateById(userNameHistory);
    }

    @Override
    public boolean deleteHistory(Integer historyId) {
        if (historyId == null || historyId <= 0) {
            throw new IllegalArgumentException("历史ID不能为空且必须大于0");
        }
        // 验证用户名变更历史是否存在
        if (!this.existsById(historyId)) {
            throw new RuntimeException("用户名变更历史不存在");
        }
        return this.removeById(historyId);
    }

    @Override
    public List<UserNameHistory> getUserNameHistoriesByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        QueryWrapper<UserNameHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("changed_at");
        return this.list(wrapper);
    }

    @Override
    public Page<UserNameHistory> getUserNameHistoriesByUserId(Integer userId, Integer current, Integer pageSize) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        // 验证用户是否存在
        Users user = usersService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Page<UserNameHistory> page = new Page<>(current, pageSize);
        QueryWrapper<UserNameHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("changed_at");
        return this.page(page, wrapper);
    }

    @Override
    public boolean batchAddHistories(List<UserNameHistory> userNameHistoryList) {
        if (userNameHistoryList == null || userNameHistoryList.isEmpty()) {
            throw new IllegalArgumentException("用户名变更历史列表不能为空");
        }
        for (UserNameHistory history : userNameHistoryList) {
            if (history == null) {
                throw new IllegalArgumentException("用户名变更历史列表中包含空历史记录");
            }
            // 验证用户是否存在
            Integer userId = history.getUserId();
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID不能为空且必须大于0");
            }
            Users user = usersService.getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在: " + userId);
            }
        }
        return this.saveBatch(userNameHistoryList);
    }

    @Override
    public boolean batchUpdateHistories(List<UserNameHistory> userNameHistoryList) {
        if (userNameHistoryList == null || userNameHistoryList.isEmpty()) {
            throw new IllegalArgumentException("用户名变更历史列表不能为空");
        }
        for (UserNameHistory history : userNameHistoryList) {
            if (history == null || history.getHistoryId() == null || history.getHistoryId() <= 0) {
                throw new IllegalArgumentException("用户名变更历史列表中包含无效历史记录");
            }
            // 验证用户名变更历史是否存在
            if (!this.existsById(history.getHistoryId())) {
                throw new RuntimeException("用户名变更历史不存在: " + history.getHistoryId());
            }
            // 验证用户是否存在
            Integer userId = history.getUserId();
            if (userId != null) {
                if (userId <= 0) {
                    throw new IllegalArgumentException("用户ID必须大于0");
                }
                Users user = usersService.getById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在: " + userId);
                }
            }
        }
        return this.updateBatchById(userNameHistoryList);
    }

    @Override
    public boolean batchDeleteHistories(List<Integer> historyIds) {
        if (historyIds == null || historyIds.isEmpty()) {
            throw new IllegalArgumentException("历史ID列表不能为空");
        }
        // 验证列表中的历史记录是否都存在
        for (Integer historyId : historyIds) {
            if (historyId == null || historyId <= 0) {
                throw new IllegalArgumentException("历史ID列表中包含无效ID");
            }
            if (!this.existsById(historyId)) {
                throw new RuntimeException("用户名变更历史不存在: " + historyId);
            }
        }
        return this.removeByIds(historyIds);
    }

    private boolean existsById(Integer historyId) {
        if (historyId == null) {
            return false;
        }
        return this.getById(historyId) != null;
    }

}