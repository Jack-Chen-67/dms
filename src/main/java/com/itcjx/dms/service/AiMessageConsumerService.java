package com.itcjx.dms.service;

import com.itcjx.dms.config.RabbitMQConfig;
import com.itcjx.dms.entity.AiGenerateTask;
import com.itcjx.dms.entity.AiGenerationRecord;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.mapper.DocumentMapper;
import com.itcjx.dms.service.impl.AiGenerationRecordServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AiMessageConsumerService {
    //创建消息消费者服务

    @Autowired
    private AiGenerationRecordServiceImpl aiGenerationRecordService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private ChatClient chatClient;

    /**
     * 监听ai生成任务队列
     */
    @RabbitListener(queues = RabbitMQConfig.AI_QUEUE)
    public void handleAiGenerateTask(AiGenerateTask task){
        try{
            log.info("开始处理ai生成任务"+ task.getTaskId());
            //调用ai服务处理任务
            String result = processAiTask(task);

            //保存ai生成记录
            AiGenerationRecord record = new AiGenerationRecord();
            record.setDocId(task.getDocId());
            record.setUserId(task.getUserId());
            record.setPrompt(task.getPrompt());
            record.setResult(result);
            record.setCreateTime(LocalDateTime.now());
            aiGenerationRecordService.saveAiGenerationRecord(record);

            // 通过WebSocket将结果推送给用户
            webSocketService.sendMessageToUser(task.getUserId(),
                    "{\"type\":\"ai_result\",\"taskId\":" + task.getTaskId() + ",\"result\":\"" + result + "\"}");

            log.info("处理ai生成任务完成"+ task.getTaskId());
        }catch (Exception e){
            log.error("处理ai生成任务失败"+ task.getTaskId()+",错误："+ e.getMessage());
            e.printStackTrace();

            // 保存错误信息到数据库
            AiGenerationRecord record = new AiGenerationRecord();
            record.setDocId(task.getDocId());
            record.setUserId(task.getUserId());
            record.setPrompt("任务ID: " + task.getTaskId() + ", 类型: " + task.getTaskType() + ", 提示: " + task.getPrompt());
            record.setResult("处理失败: " + e.getMessage() + " (任务ID: " + task.getTaskId() + ")");
            record.setCreateTime(LocalDateTime.now());
            aiGenerationRecordService.saveAiGenerationRecord(record);

            // 通过WebSocket将错误信息推送给用户
            webSocketService.sendMessageToUser(task.getUserId(),
                    "{\"type\":\"ai_error\",\"taskId\":" + task.getTaskId() + ",\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * 处理ai任务
     */
    private String processAiTask(AiGenerateTask task){
        try{
            Document document = documentMapper.selectById(task.getDocId());
            if(document == null){
                return "文档不存在";
            }

            String content = document.getContent();

            if("summary".equals(task.getTaskType())){
                // 调用AI生成摘要
                String prompt = "请为以下文档生成摘要:\n\n" + content;
                return chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
            } else if ("question".equals(task.getTaskType())) {
                // 调用AI问答
                String prompt = "请根据以下文档内容回答问题: " + task.getContent() + "\n\n文档内容:\n" + content;
                return chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
            }
        }catch (Exception e){
            log.error("AI处理失败: ", e);
            return "AI处理失败: " + e.getMessage();
        }

        return "AI生成内容";
    }
}
