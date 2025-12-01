package com.itcjx.dms.service.impl;

import com.itcjx.dms.entity.DocumentSearch;
import com.itcjx.dms.repository.DocumentSearchRepository;
import com.itcjx.dms.service.DocumentSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentSearchServiceImpl implements DocumentSearchService {

    @Autowired
    private DocumentSearchRepository documentSearchRepository;

    // 保存文档到Elasticsearch索引
    @Override
    public void saveDocument(DocumentSearch documentSearch) {
        log.info("准备保存文档到Elasticsearch，ID: {}, 标题: {}", documentSearch.getId(), documentSearch.getTitle());
        try {
            documentSearchRepository.save(documentSearch);
            log.info("成功保存文档到Elasticsearch，ID: {}", documentSearch.getId());
        } catch (Exception e) {
            log.error("保存文档到Elasticsearch失败，ID: " + documentSearch.getId(), e);
            throw e;
        }
    }

    @Override
    public void deleteDocument(Long id) {
        documentSearchRepository.deleteById(id);
    }

    @Override
    public Page<DocumentSearch> searchDocuments(String keyword, Pageable pageable) {
        log.info("搜索关键词: {}", keyword);
        Page<DocumentSearch> result = documentSearchRepository.findByTitleContainingOrContentContaining(
                keyword, keyword, pageable);
        log.info("搜索结果数量: {}", result.getTotalElements());

        // 如果没有结果，尝试使用原生查询
        if (result.getTotalElements() == 0) {
            log.info("尝试使用原生查询");
            result = documentSearchRepository.findByTitleOrContent(keyword, pageable);
            log.info("使用原生查询搜索结果数量: {}", result.getTotalElements());
        }
        return result;
    }

    @Override
    public Page<DocumentSearch> searchDocumentsByAuthor(Long authorId, Pageable pageable) {
        return documentSearchRepository.findByAuthorId(authorId, pageable);
    }

    @Override
    public Page<DocumentSearch> searchDocumentsByStatus(Integer status, Pageable pageable) {
        return documentSearchRepository.findByStatus(status, pageable);
    }

    @Override
    public void importAllDocuments(List<DocumentSearch> documents) {
        documentSearchRepository.saveAll(documents);
    }

    @Override
    public DocumentSearch findById(Long id) {
        Optional<DocumentSearch> documentSearch = documentSearchRepository.findById(id);
        return documentSearch.orElse(null);
    }
}