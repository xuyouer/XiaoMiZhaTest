package ltd.xiaomizha.xuyou.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.sms.service.OaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("oa")
@Tag(name = "OA消息", description = "OA通知API")
public class OaController {

    @Resource
    private OaService oaService;

    /**
     * 发送 OA 文本消息通知
     * <p>
     * 通过钉钉/企业微信机器人发送文本消息，
     * 支持通过手机号 @指定人员
     *
     * @param phones  接收通知的手机号（多个用逗号分隔）
     * @param content 消息内容
     * @return 发送结果
     */
    @PostMapping("/text-notice")
    @Operation(summary = "发送OA文本消息", description = "通过钉钉/企业微信发送文本消息通知")
    public ResponseResult<Void> sendOaTextNotice(
            @RequestParam String phones,
            @RequestParam String content) {
        log.info("请求发送OA文本消息: receivers={}, content={}", phones, content);

        List<String> phoneList = Arrays.asList(phones.split(","));
        return oaService.sendTextNotice(phoneList, content);
    }

    /**
     * 发送 OA Markdown 格式消息通知
     * <p>
     * 支持 Markdown 语法的富文本消息
     *
     * @param phones   接收通知的手机号（多个用逗号分隔）
     * @param title    消息标题
     * @param markdown Markdown格式的消息内容
     * @return 发送结果
     */
    @PostMapping("/markdown-notice")
    @Operation(summary = "发送OA Markdown消息", description = "通过钉钉/企业微信发送Markdown格式富文本消息")
    public ResponseResult<Void> sendOaMarkdownNotice(
            @RequestParam String phones,
            @RequestParam String title,
            @RequestParam String markdown) {
        log.info("请求发送OA Markdown消息: receivers={}, title={}", phones, title);

        List<String> phoneList = Arrays.asList(phones.split(","));
        return oaService.sendMarkdownNotice(phoneList, title, markdown);
    }

    /**
     * 发送 OA 全员通知 (@all)
     * <p>
     * 发送消息给群组所有成员
     *
     * @param content 消息内容
     * @return 发送结果
     */
    @PostMapping("/notice-all")
    @Operation(summary = "发送OA全员通知", description = "发送消息给群组所有成员（@all）")
    public ResponseResult<Void> sendOaNoticeToAll(@RequestParam String content) {
        log.info("请求发送OA全员通知: content={}", content);
        return oaService.sendNoticeToAll(content);
    }
}
