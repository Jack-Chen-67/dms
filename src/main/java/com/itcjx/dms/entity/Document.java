package com.itcjx.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文档表
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Getter
@Setter
@ToString
@TableName("doc_document")
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "文档标题不能为空")
    @Size(max = 200, message = "文档标题长度不能超过200个字符")
    @TableField("title")
    private String title;

    @NotBlank(message = "文档内容不能为空")
    @TableField("content")
    private String content;

    /**
     * 关联doc_folder.id
     */
    @NotNull(message = "文件夹ID不能为空")
    @TableField("folder_id")
    private Long folderId;

    /**
     * 关联sys_user.id
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 0-草稿 1-发布
     */
    @TableField("status")
    private Byte status;

    @TableField("version")
    private Integer version;

    /**
     * 0-未删 1-已删
     */
    @TableLogic
    @TableField("is_deleted")
    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField("create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField("update_time")
    private LocalDateTime updateTime;
}
