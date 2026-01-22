package ltd.xiaomizha.xuyou.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.common.response.ResponseResultPage;
import ltd.xiaomizha.xuyou.common.utils.mybatis.PageUtils;
import ltd.xiaomizha.xuyou.user.entity.Users;
import ltd.xiaomizha.xuyou.user.service.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("users")
@Tag(name = "用户管理", description = "用户管理API")
public class UsersController {
    @Resource
    private UsersService usersService;

    @GetMapping("/list")
    @Operation(summary = "获取所有用户")
    public ResponseResult<?> getList() {
        try {
            List<Users> users = usersService.list();
            log.info("获取用户成功");
            return ResponseResultPage.ok(users);
        } catch (Exception e) {
            log.error("获取用户失败", e);
            return ResponseResult.error("获取用户失败");
        }
    }

    @GetMapping(value = {"/list/page", "/list/{current}", "/list/{current}/{pageSize}"})
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

}
