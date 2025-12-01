package com.itcjx.dms.service;

import org.springframework.data.domain.Page;
import com.itcjx.dms.entity.DocumentSearch;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DocumentSearchService {
    /**
     * 保存文档到ES
     */
    void saveDocument(DocumentSearch documentSearch);

    /**
     * 从ES删除文档
     */
    void deleteDocument(Long id);

    /**
     * 搜索文档
     */
    Page<DocumentSearch> searchDocuments(String keyword, Pageable pageable);

    /**
     * 根据作者搜索文档
     */
    Page<DocumentSearch> searchDocumentsByAuthor(Long authorId, Pageable pageable);

    /**
     * 根据状态搜索文档
     */
    Page<DocumentSearch> searchDocumentsByStatus(Integer status, Pageable pageable);

    /**
     * 批量导入文档
     */
    void importAllDocuments(List<DocumentSearch> documents);

    /**
     * 根据ID查找文档
     */
    DocumentSearch findById(Long id);
}
