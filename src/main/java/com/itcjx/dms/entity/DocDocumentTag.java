package com.itcjx.dms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 文档-标签关联表
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Getter
@Setter
@ToString
@TableName("doc_document_tag")
public class DocDocumentTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联doc_document.id
     */
    @TableField("doc_id")
    private Long docId;

    /**
     * 关联doc_tag.id
     */
    @TableField("tag_id")
    private Long tagId;
}
