package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserNames;
import ltd.xiaomizha.xuyou.user.service.UserNamesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("names")
@Tag(name = "用户名管理", description = "用户名管理API")
public class UserNamesController {

    @Resource
    private UserNamesService userNamesService;

    @Operation(summary = "获取用户名列表", description = "分页获取所有用户名信息")
    @GetMapping("/list")
    public ResponseResult<?> getNamesPage(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserNames> namesPage = userNamesService.getNamesPage(current, pageSize);
            return ResponseResultPage.ok(namesPage.getRecords(), namesPage.getCurrent(), namesPage.getSize(), namesPage.getTotal());
        } catch (Exception e) {
            log.error("获取用户名列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取用户名详情", description = "根据名称ID获取用户名详情")
    @GetMapping("/{nameId}")
    public ResponseResult<?> getNameById(@Parameter(description = "名称ID") @PathVariable Integer nameId) {
        try {
            UserNames userNames = userNamesService.getNameById(nameId);
            return ResponseResult.success(userNames);
        } catch (Exception e) {
            log.error("获取用户名详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取用户名信息", description = "根据用户ID获取该用户的用户名信息")
    @GetMapping("/user/{userId}")
    public ResponseResult<?> getNameByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            UserNames userNames = userNamesService.getUserNameByUserId(userId);
            return ResponseResult.success(userNames);
        } catch (Exception e) {
            log.error("根据用户ID获取用户名信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新用户名信息", description = "更新用户名信息, 只能修改display_name和is_default_display字段")
    @PutMapping("/{nameId}")
    public ResponseResult<?> updateName(@Parameter(description = "名称ID") @PathVariable Integer nameId,
                                        @RequestBody UserNames userNames) {
        try {
            boolean result = userNamesService.updateName(nameId, userNames);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新用户名信息失败");
            }
        } catch (Exception e) {
            log.error("更新用户名信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增用户名信息", description = "新增用户名信息")
    @PostMapping
    public ResponseResult<?> addName(@RequestBody UserNames userNames) {
        try {
            boolean result = userNamesService.addName(userNames);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增用户名信息失败");
            }
        } catch (Exception e) {
            log.error("新增用户名信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除用户名信息", description = "根据名称ID删除用户名信息")
    @DeleteMapping("/{nameId}")
    public ResponseResult<?> deleteName(@Parameter(description = "名称ID") @PathVariable Integer nameId) {
        try {
            boolean result = userNamesService.deleteName(nameId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除用户名信息失败");
            }
        } catch (Exception e) {
            log.error("删除用户名信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加用户名信息", description = "批量添加用户名信息")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddNames(@RequestBody List<UserNames> userNamesList) {
        try {
            boolean result = userNamesService.batchAddNames(userNamesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加用户名信息失败");
            }
        } catch (Exception e) {
            log.error("批量添加用户名信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新用户名信息", description = "批量更新用户名信息, 只能修改display_name和is_default_display字段")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateNames(@RequestBody List<UserNames> userNamesList) {
        try {
            boolean result = userNamesService.batchUpdateNames(userNamesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新用户名信息失败");
            }
        } catch (Exception e) {
            log.error("批量更新用户名信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除用户名信息", description = "批量删除用户名信息")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteNames(@RequestBody List<Integer> nameIds) {
        try {
            boolean result = userNamesService.batchDeleteNames(nameIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除用户名信息失败");
            }
        } catch (Exception e) {
            log.error("批量删除用户名信息失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}