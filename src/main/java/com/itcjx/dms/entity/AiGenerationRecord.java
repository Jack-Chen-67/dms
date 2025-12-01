package com.itcjx.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * AI生成记录表
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Getter
@Setter
@ToString
@TableName("ai_generation_record")
public class AiGenerationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联doc_document.id
     */
    @TableField("doc_id")
    private Long docId;

    /**
     * 关联sys_user.id
     */
    @TableField("user_id")
    private Long userId;

    @TableField("prompt")
    private String prompt;

    @TableField("result")
    private String result;

    @TableField("create_time")
    private LocalDateTime createTime;
}
