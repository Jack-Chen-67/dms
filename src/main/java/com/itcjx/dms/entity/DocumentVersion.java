package com.itcjx.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("doc_document_version")
public class DocumentVersion {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("document_id")
    private Long documentId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("version")
    private Integer version;

    @TableField("author_id")
    private Long authorId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
