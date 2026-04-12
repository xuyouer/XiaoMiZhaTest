package ltd.xiaomizha.xuyou.sms.service;

import ltd.xiaomizha.xuyou.common.response.ResponseResult;

import java.util.List;

/**
 * OA 消息通知服务
 */
public interface OaService {

    /**
     * 发送文本消息通知 (支持 @手机号)
     * <p>
     * 通过钉钉/企业微信机器人发送文本消息,
     * 支持通过手机号 @指定人员
     *
     * @param phoneList 接收通知的手机号列表
     * @param content   消息内容
     * @return 发送结果
     */
    ResponseResult<Void> sendTextNotice(List<String> phoneList, String content);

    /**
     * 发送 Markdown 格式消息通知
     * <p>
     * 支持 Markdown 语法的富文本消息
     *
     * @param phoneList 接收通知的手机号列表
     * @param title     消息标题
     * @param markdown  Markdown 格式的内容
     * @return 发送结果
     */
    ResponseResult<Void> sendMarkdownNotice(List<String> phoneList, String title, String markdown);

    /**
     * 发送全员通知 (@all)
     * <p>
     * 发送消息给群组所有成员
     *
     * @param content 消息内容
     * @return 发送结果
     */
    ResponseResult<Void> sendNoticeToAll(String content);
}
