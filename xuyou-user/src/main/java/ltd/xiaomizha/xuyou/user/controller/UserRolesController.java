package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserRoles;
import ltd.xiaomizha.xuyou.user.service.UserRolesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("roles")
@Tag(name = "用户角色管理", description = "用户角色管理API")
public class UserRolesController {

    @Resource
    private UserRolesService userRolesService;

    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    @GetMapping("/list")
    public ResponseResult<?> getRolesPage(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserRoles> rolesPage = userRolesService.getRolesPage(current, pageSize);
            return ResponseResultPage.ok(rolesPage.getRecords(), rolesPage.getCurrent(), rolesPage.getSize(), rolesPage.getTotal());
        } catch (Exception e) {
            log.error("获取角色列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详情")
    @GetMapping("/{roleId}")
    public ResponseResult<?> getRoleById(@Parameter(description = "角色ID") @PathVariable Integer roleId) {
        try {
            UserRoles role = userRolesService.getRoleById(roleId);
            return ResponseResult.success(role);
        } catch (Exception e) {
            log.error("获取角色详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增角色", description = "新增角色")
    @PostMapping
    public ResponseResult<?> addRole(@RequestBody UserRoles userRoles) {
        try {
            boolean result = userRolesService.addRole(userRoles);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增角色失败");
            }
        } catch (Exception e) {
            log.error("新增角色失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新角色", description = "更新角色信息")
    @PutMapping("/{roleId}")
    public ResponseResult<?> updateRole(@Parameter(description = "角色ID") @PathVariable Integer roleId,
                                        @RequestBody UserRoles userRoles) {
        try {
            boolean result = userRolesService.updateRole(roleId, userRoles);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新角色失败");
            }
        } catch (Exception e) {
            log.error("更新角色失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @DeleteMapping("/{roleId}")
    public ResponseResult<?> deleteRole(@Parameter(description = "角色ID") @PathVariable Integer roleId) {
        try {
            boolean result = userRolesService.deleteRole(roleId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除角色失败");
            }
        } catch (Exception e) {
            log.error("删除角色失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加角色", description = "批量添加角色")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddRoles(@RequestBody List<UserRoles> userRolesList) {
        try {
            boolean result = userRolesService.batchAddRoles(userRolesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加角色失败");
            }
        } catch (Exception e) {
            log.error("批量添加角色失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新角色", description = "批量更新角色信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateRoles(@RequestBody List<UserRoles> userRolesList) {
        try {
            boolean result = userRolesService.batchUpdateRoles(userRolesList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新角色失败");
            }
        } catch (Exception e) {
            log.error("批量更新角色失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除角色", description = "批量删除角色")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteRoles(@RequestBody List<Integer> roleIds) {
        try {
            boolean result = userRolesService.batchDeleteRoles(roleIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除角色失败");
            }
        } catch (Exception e) {
            log.error("批量删除角色失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}
