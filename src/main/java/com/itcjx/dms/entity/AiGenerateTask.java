package com.itcjx.dms.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AiGenerateTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long docId;
    private Long userId;
    private String taskType; // 任务类型：summary(摘要), question(问答)
    private String content;  // 文档内容或问题
    private String prompt;   // 提示词
    private LocalDateTime createTime;
}
