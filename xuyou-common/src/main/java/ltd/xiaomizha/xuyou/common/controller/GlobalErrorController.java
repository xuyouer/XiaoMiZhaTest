package ltd.xiaomizha.xuyou.common.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;

/**
 * 全局错误控制器
 * <p>
 * 处理所有错误
 */
@RestController
@Tag(name = "全局错误控制", description = "处理所有错误")
public class GlobalErrorController implements ErrorController {

    /**
     * 处理所有错误, 返回统一的JSON格式响应
     */
    @RequestMapping("/error")
    public ResponseEntity<ResponseResult<Void>> handleError(HttpServletRequest request) {
        // 获取错误状态码
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        // 如果状态码为空, 默认500
        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        // 根据状态码返回不同的错误信息
        ResponseResult<Void> result = ResponseResult.error();
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            result.code(ResultEnum.NOT_FOUND.getCode())
                    .message(ResultEnum.NOT_FOUND.getMessage());
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            result.code(ResultEnum.INTERNAL_SERVER_ERROR.getCode())
                    .message(ResultEnum.INTERNAL_SERVER_ERROR.getMessage());
        } else {
            result.code(statusCode)
                    .message("请求处理失败");
        }

        return ResponseEntity.status(statusCode).body(result);
    }
}
