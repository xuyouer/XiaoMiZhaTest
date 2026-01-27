package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.constant.UserConstants;
import ltd.xiaomizha.xuyou.common.enums.entity.LoginType;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.common.utils.mybatis.PageUtils;
import ltd.xiaomizha.xuyou.common.utils.user.UserUtils;
import ltd.xiaomizha.xuyou.user.dto.UserDetailDTO;
import ltd.xiaomizha.xuyou.user.entity.*;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("users")
@Tag(name = "用户管理", description = "用户管理API")
public class UsersController {
    @Resource
    private UsersService usersService;

    // @GetMapping("/list")
    // @Operation(summary = "获取所有用户")
    // public ResponseResult<?> getList() {
    //     try {
    //         List<Users> users = usersService.list();
    //         log.info("获取用户成功");
    //         return ResponseResultPage.ok(users);
    //     } catch (Exception e) {
    //         log.error("获取用户失败", e);
    //         return ResponseResult.error("获取用户失败");
    //     }
    // }

    // @GetMapping(value = {"/list/page", "/list/{current}", "/list/{current}/{pageSize}"})
    @GetMapping(value = {"/list", "/list/{current}", "/list/{current}/{pageSize}"})
    @Operation(summary = "分页获取所有用户")
    @Parameters({
            @Parameter(name = "current", description = "当前页码", example = "1"),
            @Parameter(name = "pageSize", description = "每页条数", example = "10")
    })
    public ResponseResult<?> getPageList(
            @PathVariable(required = false) Long current,
            @PathVariable(required = false) Long pageSize,
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size) {
        try {
            // 优先级: 路径参数 > 查询参数 > 默认值
            long currentPage = PageUtils.determineCurrentPage(current, page);
            long pageSizeValue = PageUtils.determinePageSize(pageSize, size);
            // 获取分页结果
            Page<Users> pageResult = usersService.page(
                    ResponseResultPage.getPage(currentPage, pageSizeValue)
            );
            log.info("获取用户成功: 总记录数={}, 当前页={}, 每页大小={}",
                    pageResult.getTotal(), currentPage, pageSizeValue);
            return ResponseResultPage.ok(pageResult);
        } catch (Exception e) {
            log.error("获取用户失败", e);
            return ResponseResult.error("获取用户失败");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "注册用户")
    public ResponseResult<?> registerUser(@RequestBody Users users) {
        try {
            boolean result = usersService.registerUser(users);
            if (result) {
                log.info("注册用户成功: username={}", users.getUsername());
                return ResponseResult.ok("注册成功");
            } else {
                log.error("注册用户失败: 用户名已存在或用户名已被禁用");
                return ResponseResult.error("注册失败: 用户名已存在或用户名已被禁用");
            }
        } catch (Exception e) {
            log.error("注册用户失败", e);
            return ResponseResult.error("注册失败: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseResult<?> loginUser(@RequestBody Users users, jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 获取客户端信息
            String ipAddress = UserUtils.getClientIp(request);
            String userAgent = UserUtils.getUserAgent(request);
            String deviceInfo = UserConstants.DEFAULT_DEVICE_INFO;

            boolean result = usersService.loginUser(users.getUsername(), users.getPasswordHash(), ipAddress, userAgent, deviceInfo, LoginType.LOGIN);
            if (result) {
                log.info("用户登录成功: username={}", users.getUsername());
                return ResponseResult.ok("登录成功");
            } else {
                log.error("用户登录失败: 用户名密码错误或用户已被禁用");
                return ResponseResult.error("登录失败: 用户名密码错误或用户已被禁用");
            }
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ResponseResult.error("登录失败: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "根据user_id查询单个用户详细信息")
    public ResponseResult<?> getUserDetailById(@PathVariable Integer userId) {
        try {
            UserDetailDTO userDetail = usersService.getUserDetailById(userId);
            if (userDetail != null) {
                log.info("查询用户详细信息成功: userId={}", userId);
                return ResponseResult.ok(userDetail);
            } else {
                log.error("查询用户详细信息失败: 用户不存在");
                return ResponseResult.error("查询失败: 用户不存在");
            }
        } catch (Exception e) {
            log.error("查询用户详细信息失败", e);
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/feedbacks")
    @Operation(summary = "根据user_id查询单个用户的反馈")
    public ResponseResultPage<UserFeedback> getUserFeedbacks(@PathVariable Integer userId) {
        try {
            Page<UserFeedback> page = ResponseResultPage.getPage();
            Page<UserFeedback> feedbacks = usersService.getUserFeedbacks(userId, page);
            log.info("查询用户反馈成功: userId={}", userId);
            return ResponseResultPage.ok(feedbacks);
        } catch (Exception e) {
            log.error("查询用户反馈失败", e);
            return ResponseResultPage.empty();
        }
    }

    @GetMapping("/{userId}/logs")
    @Operation(summary = "根据user_id查询单个用户的操作日志")
    public ResponseResultPage<UserLogs> getUserLogs(@PathVariable Integer userId) {
        try {
            Page<UserLogs> page = ResponseResultPage.getPage();
            Page<UserLogs> logs = usersService.getUserLogs(userId, page);
            log.info("查询用户操作日志成功: userId={}", userId);
            return ResponseResultPage.ok(logs);
        } catch (Exception e) {
            log.error("查询用户操作日志失败", e);
            return ResponseResultPage.empty();
        }
    }

    @GetMapping("/{userId}/vip-logs")
    @Operation(summary = "根据user_id查询单个用户的VIP日志")
    public ResponseResultPage<UserVipLog> getUserVipLogs(@PathVariable Integer userId) {
        try {
            Page<UserVipLog> page = ResponseResultPage.getPage();
            Page<UserVipLog> vipLogs = usersService.getUserVipLogs(userId, page);
            log.info("查询用户VIP日志成功: userId={}", userId);
            return ResponseResultPage.ok(vipLogs);
        } catch (Exception e) {
            log.error("查询用户VIP日志失败", e);
            return ResponseResultPage.empty();
        }
    }

    @GetMapping("/{userId}/vip-points-logs")
    @Operation(summary = "根据user_id查询单个用户的VIP积分日志")
    public ResponseResultPage<UserVipPointsLog> getUserVipPointsLogs(@PathVariable Integer userId) {
        try {
            Page<UserVipPointsLog> page = ResponseResultPage.getPage();
            Page<UserVipPointsLog> vipPointsLogs = usersService.getUserVipPointsLogs(userId, page);
            log.info("查询用户VIP积分日志成功: userId={}", userId);
            return ResponseResultPage.ok(vipPointsLogs);
        } catch (Exception e) {
            log.error("查询用户VIP积分日志失败", e);
            return ResponseResultPage.empty();
        }
    }

    @GetMapping("/{userId}/login-records")
    @Operation(summary = "根据user_id查询单个用户的登录记录")
    public ResponseResultPage<UserLoginRecords> getUserLoginRecords(@PathVariable Integer userId) {
        try {
            Page<UserLoginRecords> page = ResponseResultPage.getPage();
            Page<UserLoginRecords> loginRecords = usersService.getUserLoginRecords(userId, page);
            log.info("查询用户登录记录成功: userId={}", userId);
            return ResponseResultPage.ok(loginRecords);
        } catch (Exception e) {
            log.error("查询用户登录记录失败", e);
            return ResponseResultPage.empty();
        }
    }

    @PutMapping("/update/{userId}")
    @Operation(summary = "修改用户")
    public ResponseResult<?> updateUser(@PathVariable Integer userId, @RequestBody Users users) {
        try {
            users.setUserId(userId);

            boolean result = usersService.updateUser(users);
            if (result) {
                log.info("修改用户成功: userId={}", userId);
                return ResponseResult.ok("修改成功");
            } else {
                log.error("修改用户失败: 用户不存在或不允许修改除密码外的其他字段");
                return ResponseResult.error("修改失败: 用户不存在或不允许修改除密码外的其他字段");
            }
        } catch (Exception e) {
            log.error("修改用户失败", e);
            return ResponseResult.error("修改失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/logout/{userId}")
    @Operation(summary = "注销用户")
    public ResponseResult<?> logoutUser(@PathVariable Integer userId) {
        try {
            boolean result = usersService.logoutUser(userId);
            if (result) {
                log.info("注销用户成功: userId={}", userId);
                return ResponseResult.ok("注销成功");
            } else {
                log.error("注销用户失败: 用户不存在或用户已被禁用");
                return ResponseResult.error("注销失败: 用户不存在或用户已被禁用");
            }
        } catch (Exception e) {
            log.error("注销用户失败", e);
            return ResponseResult.error("注销失败: " + e.getMessage());
        }
    }
}
