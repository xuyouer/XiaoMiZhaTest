package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.entity.UserRoleRelations;
import ltd.xiaomizha.xuyou.user.service.UserRoleRelationsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("role-relations")
@Tag(name = "用户角色关联管理", description = "用户角色关联管理API")
public class UserRoleRelationsController {

    @Resource
    private UserRoleRelationsService userRoleRelationsService;

    @Operation(summary = "获取角色关联列表", description = "分页获取所有用户角色关联")
    @GetMapping("/list")
    public ResponseResult<?> getRelationsPage(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserRoleRelations> relationsPage = userRoleRelationsService.getRelationsPage(current, pageSize);
            return ResponseResultPage.ok(relationsPage.getRecords(), relationsPage.getCurrent(), relationsPage.getSize(), relationsPage.getTotal());
        } catch (Exception e) {
            log.error("获取角色关联列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取角色关联详情", description = "根据关联ID获取用户角色关联详情")
    @GetMapping("/{relationId}")
    public ResponseResult<?> getRelationById(@Parameter(description = "关联ID") @PathVariable Long relationId) {
        try {
            UserRoleRelations relation = userRoleRelationsService.getRelationById(relationId);
            return ResponseResult.success(relation);
        } catch (Exception e) {
            log.error("获取角色关联详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增角色关联", description = "新增用户角色关联")
    @PostMapping
    public ResponseResult<?> addRelation(@RequestBody UserRoleRelations userRoleRelations) {
        try {
            boolean result = userRoleRelationsService.addRelation(userRoleRelations);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增角色关联失败");
            }
        } catch (Exception e) {
            log.error("新增角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新角色关联", description = "更新用户角色关联信息")
    @PutMapping("/{relationId}")
    public ResponseResult<?> updateRelation(@Parameter(description = "关联ID") @PathVariable Long relationId,
                                            @RequestBody UserRoleRelations userRoleRelations) {
        try {
            boolean result = userRoleRelationsService.updateRelation(relationId, userRoleRelations);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新角色关联失败");
            }
        } catch (Exception e) {
            log.error("更新角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除角色关联", description = "根据关联ID删除用户角色关联")
    @DeleteMapping("/{relationId}")
    public ResponseResult<?> deleteRelation(@Parameter(description = "关联ID") @PathVariable Long relationId) {
        try {
            boolean result = userRoleRelationsService.deleteRelation(relationId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除角色关联失败");
            }
        } catch (Exception e) {
            log.error("删除角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取角色列表", description = "根据用户ID获取该用户的所有角色")
    @GetMapping("/user/{userId}/roles")
    public ResponseResult<?> getRolesByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            return ResponseResult.success(userRoleRelationsService.getUserRolesByUserId(userId));
        } catch (Exception e) {
            log.error("根据用户ID获取角色列表失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "创建默认角色关联", description = "为用户创建默认角色关联")
    @PostMapping("/default/{userId}")
    public ResponseResult<?> createDefaultRelation(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            boolean result = userRoleRelationsService.createDefaultUserRoleRelation(userId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("创建默认角色关联失败");
            }
        } catch (Exception e) {
            log.error("创建默认角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "激活角色关联", description = "激活用户的角色关联")
    @PutMapping("/activate/{userId}")
    public ResponseResult<?> activateRelation(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            boolean result = userRoleRelationsService.activateUserRoleRelation(userId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("激活角色关联失败");
            }
        } catch (Exception e) {
            log.error("激活角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加角色关联", description = "批量添加用户角色关联")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddRelations(@RequestBody List<UserRoleRelations> userRoleRelationsList) {
        try {
            boolean result = userRoleRelationsService.batchAddRelations(userRoleRelationsList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加角色关联失败");
            }
        } catch (Exception e) {
            log.error("批量添加角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新角色关联", description = "批量更新用户角色关联信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateRelations(@RequestBody List<UserRoleRelations> userRoleRelationsList) {
        try {
            boolean result = userRoleRelationsService.batchUpdateRelations(userRoleRelationsList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新角色关联失败");
            }
        } catch (Exception e) {
            log.error("批量更新角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除角色关联", description = "批量删除用户角色关联")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteRelations(@RequestBody List<Long> relationIds) {
        try {
            boolean result = userRoleRelationsService.batchDeleteRelations(relationIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除角色关联失败");
            }
        } catch (Exception e) {
            log.error("批量删除角色关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}
