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
 * 附件表
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Getter
@Setter
@ToString
@TableName("doc_attachment")
public class DocAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联doc_document.id
     */
    @TableField("doc_id")
    private Long docId;

    @TableField("file_name")
    private String fileName;

    /**
     * MinIO路径如bucket/doc/xxx.pdf
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 字节数
     */
    @TableField("file_size")
    private Long fileSize;

    @TableField("upload_time")
    private LocalDateTime uploadTime;
}
