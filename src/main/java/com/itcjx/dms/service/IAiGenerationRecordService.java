package com.itcjx.dms.service;

import com.itcjx.dms.entity.AiGenerationRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.dms.util.Result;

/**
 * <p>
 * AI生成记录表 服务类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
public interface IAiGenerationRecordService extends IService<AiGenerationRecord> {

    // 生成文档摘要
    Result<String> summary(Long docId);
    // 获取文档问答
    Result<String> question(Long docId, String question);
    //保存ai生成记录
    boolean saveAiGenerationRecord(AiGenerationRecord aiGenerationRecord);
}
