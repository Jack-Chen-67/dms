package com.itcjx.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.dms.entity.DocumentVersion;

import java.util.List;

public interface DocumentVersionService extends IService<DocumentVersion> {
    /**
     * 获取文档的所有版本
     */
    List<DocumentVersion> getVersionsByDocumentId(Long documentId);

    /**
     * 获取文档的特定版本
     */
    DocumentVersion getVersionByDocumentIdAndVersion(Long documentId, Integer version);

    /**
     * 保存文档版本
     */
    void saveDocumentVersion(DocumentVersion documentVersion);

    /**
     * 回滚到指定版本
     */
    boolean rollbackToVersion(Long documentId, Integer version, Long userId);
}
