package com.itcjx.dms.service.impl;

import com.itcjx.dms.entity.AiGenerationRecord;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.mapper.AiGenerationRecordMapper;
import com.itcjx.dms.mapper.DocumentMapper;
import com.itcjx.dms.service.IAiGenerationRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.dms.util.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * AI生成记录表 服务实现类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Service
public class AiGenerationRecordServiceImpl extends ServiceImpl<AiGenerationRecordMapper, AiGenerationRecord> implements IAiGenerationRecordService {
    @Autowired
    private AiGenerationRecordMapper aiGenerationRecordMapper;
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private ChatClient chatClient;

    // 生成摘要
    @Override
    public Result<String> summary(Long docId) {
        Document document = documentMapper.selectById(docId);
        if(document == null){
            return Result.error(404,"文档不存在");
        }
        String content = document.getContent();
        String prompt = "请生成一个摘要，内容是：" + content;
        // 调用AI生成摘要
        // 调用AI生成摘要
        String summary = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        // 获取AI生成的结果
        return Result.success(summary);
    }

    // 文档问答
    @Override
    public Result<String> question(Long docId, String question) {
        Document document = documentMapper.selectById(docId);
        if(document == null){
            return Result.error(404,"文档不存在");
        }
        String content = document.getContent();
        String prompt = "请回答问题，内容是：" + content + "问题是：" + question;
        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return Result.success(answer);
    }

    //保存AI生成记录
    @Override
    public boolean saveAiGenerationRecord(AiGenerationRecord aiGenerationRecord) {
        if(aiGenerationRecord == null){
            return false;
        }
        return aiGenerationRecordMapper.insert(aiGenerationRecord) > 0;
    }
}
