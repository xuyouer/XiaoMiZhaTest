package ltd.xiaomizha.xuyou.sms.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.xiaomizha.xuyou.common.enums.ResultEnum;
import ltd.xiaomizha.xuyou.common.response.ResponseBuilder;
import ltd.xiaomizha.xuyou.common.response.ResponseResult;
import ltd.xiaomizha.xuyou.sms.dto.OaDTO;
import ltd.xiaomizha.xuyou.sms.service.OaService;
import org.dromara.oa.api.OaSender;
import org.dromara.oa.comm.entity.Request;
import org.dromara.oa.comm.entity.Response;
import org.dromara.oa.comm.enums.MessageType;
import org.dromara.oa.core.provider.factory.OaFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OaServiceImpl implements OaService {

    private final OaDTO oaDTO;

    public OaServiceImpl(OaDTO oaDTO) {
        this.oaDTO = oaDTO;
    }

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
    @Override
    public ResponseResult<Void> sendTextNotice(List<String> phoneList, String content) {
        try {
            // 参数校验
            if (phoneList == null || phoneList.isEmpty()) {
                return ResponseBuilder.error(ResultEnum.BAD_REQUEST, "接收人列表不能为空");
            }
            if (content == null || content.trim().isEmpty()) {
                return ResponseBuilder.error(ResultEnum.BAD_REQUEST, "消息内容不能为空");
            }

            // 获取 OA 发送实例
            OaSender oaSender = OaFactory.getSmsOaBlend(oaDTO.getOaConfigId());

            // 构建请求对象
            Request request = new Request();
            request.setPhoneList(new ArrayList<>(phoneList));
            request.setContent(content);

            // 发送文本消息
            Response response = oaSender.sender(request, MessageType.DING_TALK_TEXT);

            // 处理返回结果
            if (response.isSuccess()) {
                log.info("OA 文本消息发送成功: receivers={}, content={}", phoneList.size(), content);
                return ResponseBuilder.success();
            } else {
                log.error("OA 文本消息发送失败: error={}", response.getData());
                return ResponseBuilder.error("OA 消息发送失败: " + response.getData());
            }
        } catch (Exception e) {
            log.error("发送 OA 文本消息异常", e);
            return ResponseBuilder.error("发送 OA 消息失败: " + e.getMessage());
        }
    }

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
    @Override
    public ResponseResult<Void> sendMarkdownNotice(List<String> phoneList, String title, String markdown) {
        try {
            // 参数校验
            if (phoneList == null || phoneList.isEmpty()) {
                return ResponseBuilder.error(ResultEnum.BAD_REQUEST, "接收人列表不能为空");
            }

            // 获取 OA 发送实例
            OaSender oaSender = OaFactory.getSmsOaBlend(oaDTO.getOaConfigId());

            // 构建请求对象 (Markdown 格式)
            Request request = new Request();
            request.setPhoneList(new ArrayList<>(phoneList));
            request.setTitle(title); // 标题
            request.setContent(markdown); // Markdown 内容

            // 发送 Markdown 消息
            Response response = oaSender.sender(request, MessageType.DING_TALK_MARKDOWN);

            // 处理返回结果
            if (response.isSuccess()) {
                log.info("OA Markdown 消息发送成功: receivers={}, title={}", phoneList.size(), title);
                return ResponseBuilder.success();
            } else {
                log.error("OA Markdown 消息发送失败: error={}", response.getData());
                return ResponseBuilder.error("OA 消息发送失败: " + response.getData());
            }
        } catch (Exception e) {
            log.error("发送 OA Markdown 消息异常", e);
            return ResponseBuilder.error("发送 OA 消息失败: " + e.getMessage());
        }
    }

    /**
     * 发送全员通知 (@all)
     * <p>
     * 发送消息给群组所有成员
     *
     * @param content 消息内容
     * @return 发送结果
     */
    @Override
    public ResponseResult<Void> sendNoticeToAll(String content) {
        try {
            // 参数校验
            if (content == null || content.trim().isEmpty()) {
                return ResponseBuilder.error(ResultEnum.BAD_REQUEST, "消息内容不能为空");
            }

            // 获取 OA 发送实例
            OaSender oaSender = OaFactory.getSmsOaBlend(oaDTO.getOaConfigId());

            // 构建请求对象（@all 全员）
            Request request = new Request();
            request.setContent(content);
            request.setIsNoticeAll(true); // 设置 @all

            // 发送文本消息
            Response response = oaSender.sender(request, MessageType.DING_TALK_TEXT);

            // 处理返回结果
            if (response.isSuccess()) {
                log.info("OA 全员通知发送成功: content={}", content);
                return ResponseBuilder.success();
            } else {
                log.error("OA 全员通知发送失败: error={}", response.getData());
                return ResponseBuilder.error("OA 消息发送失败: " + response.getData());
            }
        } catch (Exception e) {
            log.error("发送 OA 全员通知异常", e);
            return ResponseBuilder.error("发送 OA 消息失败: " + e.getMessage());
        }
    }

}
