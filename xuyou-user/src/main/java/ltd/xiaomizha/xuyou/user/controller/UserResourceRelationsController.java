package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.user.dto.UpdateUserResourcesRequest;
import ltd.xiaomizha.xuyou.user.entity.UserResourceRelations;
import ltd.xiaomizha.xuyou.user.entity.UserResources;
import ltd.xiaomizha.xuyou.user.service.UserResourceRelationsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("user-resource-relations")
@Tag(name = "用户资源关联管理", description = "用户资源关联管理API")
public class UserResourceRelationsController {

    @Resource
    private UserResourceRelationsService userResourceRelationsService;

    @Operation(summary = "获取用户资源关联列表", description = "分页获取所有用户资源关联")
    @GetMapping("/list")
    public ResponseResult<?> getRelationsPage(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<UserResourceRelations> relationsPage = userResourceRelationsService.getRelationsPage(current, pageSize);
            return ResponseResultPage.ok(relationsPage.getRecords(), relationsPage.getCurrent(), relationsPage.getSize(), relationsPage.getTotal());
        } catch (Exception e) {
            log.error("获取用户资源关联列表失败: {}", e.getMessage(), e);
            return ResponseResultPage.error(e.getMessage());
        }
    }

    @Operation(summary = "获取用户资源关联详情", description = "根据关联ID获取用户资源关联详情")
    @GetMapping("/{relationId}")
    public ResponseResult<?> getRelationById(@Parameter(description = "关联ID") @PathVariable Long relationId) {
        try {
            UserResourceRelations relation = userResourceRelationsService.getRelationById(relationId);
            return ResponseResult.success(relation);
        } catch (Exception e) {
            log.error("获取用户资源关联详情失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "新增用户资源关联", description = "新增用户资源关联")
    @PostMapping
    public ResponseResult<?> addRelation(@RequestBody UserResourceRelations userResourceRelations) {
        try {
            boolean result = userResourceRelationsService.addRelation(userResourceRelations);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("新增用户资源关联失败");
            }
        } catch (Exception e) {
            log.error("新增用户资源关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "更新用户资源关联", description = "更新用户资源关联信息")
    @PutMapping("/{relationId}")
    public ResponseResult<?> updateRelation(@Parameter(description = "关联ID") @PathVariable Long relationId,
                                            @RequestBody UserResourceRelations userResourceRelations) {
        try {
            boolean result = userResourceRelationsService.updateRelation(relationId, userResourceRelations);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新用户资源关联失败");
            }
        } catch (Exception e) {
            log.error("更新用户资源关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "删除用户资源关联", description = "根据关联ID删除用户资源关联")
    @DeleteMapping("/{relationId}")
    public ResponseResult<?> deleteRelation(@Parameter(description = "关联ID") @PathVariable Long relationId) {
        try {
            boolean result = userResourceRelationsService.deleteRelation(relationId);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("删除用户资源关联失败");
            }
        } catch (Exception e) {
            log.error("删除用户资源关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取资源列表", description = "根据用户ID获取该用户的所有资源")
    @GetMapping("/user/{userId}/resources")
    public ResponseResult<?> getResourcesByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            List<UserResources> resources = userResourceRelationsService.getUserResourcesByUserId(userId);
            return ResponseResult.success(resources);
        } catch (Exception e) {
            log.error("根据用户ID获取资源列表失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID更新用户资源", description = "根据用户ID更新用户的资源列表")
    @PutMapping("/user/{userId}/resources")
    public ResponseResult<?> updateResourcesByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId,
                                                     @RequestBody UpdateUserResourcesRequest request) {
        if (request == null || request.getResourceIds() == null) {
            return ResponseResult.error("资源ID列表不能为空");
        }
        try {
            boolean result = userResourceRelationsService.updateUserResourcesByUserId(userId, request.getResourceIds());
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("更新用户资源失败");
            }
        } catch (Exception e) {
            log.error("根据用户ID更新用户资源失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "根据用户ID获取资源关联列表", description = "根据用户ID获取该用户的所有资源关联记录")
    @GetMapping("/user/{userId}/relations")
    public ResponseResult<?> getRelationsByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
        try {
            List<UserResourceRelations> relations = userResourceRelationsService.getRelationsByUserId(userId);
            return ResponseResult.success(relations);
        } catch (Exception e) {
            log.error("根据用户ID获取资源关联列表失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量添加用户资源关联", description = "批量添加用户资源关联")
    @PostMapping("/batch")
    public ResponseResult<?> batchAddRelations(@RequestBody List<UserResourceRelations> userResourceRelationsList) {
        try {
            boolean result = userResourceRelationsService.batchAddRelations(userResourceRelationsList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量添加用户资源关联失败");
            }
        } catch (Exception e) {
            log.error("批量添加用户资源关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量更新用户资源关联", description = "批量更新用户资源关联信息")
    @PutMapping("/batch")
    public ResponseResult<?> batchUpdateRelations(@RequestBody List<UserResourceRelations> userResourceRelationsList) {
        try {
            boolean result = userResourceRelationsService.batchUpdateRelations(userResourceRelationsList);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量更新用户资源关联失败");
            }
        } catch (Exception e) {
            log.error("批量更新用户资源关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Operation(summary = "批量删除用户资源关联", description = "批量删除用户资源关联")
    @DeleteMapping("/batch")
    public ResponseResult<?> batchDeleteRelations(@RequestBody List<Long> relationIds) {
        try {
            boolean result = userResourceRelationsService.batchDeleteRelations(relationIds);
            if (result) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("批量删除用户资源关联失败");
            }
        } catch (Exception e) {
            log.error("批量删除用户资源关联失败: {}", e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

}
