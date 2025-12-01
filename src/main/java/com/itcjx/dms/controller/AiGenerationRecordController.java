package com.itcjx.dms.controller;

import com.itcjx.dms.annotation.OperationLog;
import com.itcjx.dms.annotation.RateLimiterAnno;
import com.itcjx.dms.entity.AiGenerateTask;
import com.itcjx.dms.entity.AiGenerationRecord;
import com.itcjx.dms.service.AiMessageProducerService;
import com.itcjx.dms.service.impl.AiGenerationRecordServiceImpl;
import com.itcjx.dms.util.JwtTokenUtil;
import com.itcjx.dms.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * AI生成记录表 前端控制器
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@RestController
@RequestMapping("/api/ai")
public class AiGenerationRecordController {
    /*
    POST /api/ai/summary/{docId}       # 生成文档摘要（参数：无 → 返回summary）
    POST /api/ai/question/{docId}      # 文档内问答（参数：question → 返回answer）
     */
    @Autowired
    private AiGenerationRecordServiceImpl aiGenerationRecordServiceImpl;
    @Autowired
    private AiMessageProducerService aiMessageProducerService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 生成文档摘要
    @OperationLog("生成文档摘要")
    @RateLimiterAnno(count = 5, time = 60, limitTip = "摘要生成请求过于频繁，请稍后再试")
    @PostMapping("/summary/{docId}")
    public Result<String> summary(@RequestHeader("Authorization") String token,
                                  @PathVariable Long docId) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        //创建ai任务
        AiGenerateTask task = new AiGenerateTask();
        task.setTaskId(System.currentTimeMillis());
        task.setDocId(docId);
        task.setUserId(userId);
        task.setTaskType("summary");
        task.setPrompt("请生成文档摘要");
        task.setCreateTime(LocalDateTime.now());
        //发送到消息队列
        aiMessageProducerService.sendAiGenerateTask(task);
        return Result.success("摘要任务已提交，任务ID: " + task.getTaskId() + "，请连接WebSocket获取结果: ws://localhost:8080/ws/ai-results?userId=" + userId);

        //return aiGenerationRecordServiceImpl.summary(docId);
    }

    // 文档内问答
    @OperationLog("文档问答")
    @RateLimiterAnno(count = 10, time = 60, limitTip = "问答请求过于频繁，请稍后再试")
    @PostMapping("/question/{docId}")
    public Result<String> question(@RequestHeader("Authorization") String token,
                                   @PathVariable Long docId, String question) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        // 创建AI任务
        AiGenerateTask task = new AiGenerateTask();
        task.setTaskId(System.currentTimeMillis());
        task.setDocId(docId);
        task.setUserId(userId);
        task.setTaskType("question");
        task.setContent(question);
        task.setPrompt("请回答问题: " + question);
        task.setCreateTime(LocalDateTime.now());

        // 发送到消息队列
        aiMessageProducerService.sendAiGenerateTask(task);

        return Result.success("问答生成任务已提交，任务ID: " + task.getTaskId() + "，请连接WebSocket获取结果: ws://localhost:8080/ws/ai-results?userId=" + userId);

        //return aiGenerationRecordServiceImpl.question(docId, question);
    }

    @OperationLog("获取AI生成记录")
    @GetMapping("/records")
    public Result<List<AiGenerationRecord>> getAiRecords(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);

        List<AiGenerationRecord> records = aiGenerationRecordServiceImpl.lambdaQuery()
                .eq(AiGenerationRecord::getUserId, userId)
                .orderByDesc(AiGenerationRecord::getCreateTime)
                .list();

        return Result.success(records);
    }

    @OperationLog("根据任务ID获取AI生成结果")
    @GetMapping("/result/{taskId}")
    public Result<AiGenerationRecord> getAiResultByTaskId(@PathVariable Long taskId, @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);

        AiGenerationRecord record = aiGenerationRecordServiceImpl.lambdaQuery()
                .eq(AiGenerationRecord::getUserId, userId)
                .like(AiGenerationRecord::getPrompt, "任务ID: " + taskId)
                .or()
                .like(AiGenerationRecord::getResult, "任务ID: " + taskId)
                .orderByDesc(AiGenerationRecord::getCreateTime)
                .last("LIMIT 1")
                .one();

        if (record == null) {
            return Result.error(404, "结果尚未生成或任务不存在");
        }

        return Result.success(record);
    }
}
