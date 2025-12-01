package com.itcjx.dms.repository;

import com.itcjx.dms.entity.DocumentSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentSearchRepository extends ElasticsearchRepository<DocumentSearch, Long> {
    /**
     * 根据标题或内容搜索文档
     * @param title 标题关键词
     * @param content 内容关键词
     * @param pageable 分页参数
     * @return 匹配的文档列表
     */
    Page<DocumentSearch> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    /**
     * 使用原生查询搜索文档
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 匹配的文档列表
     */
    @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}")
    Page<DocumentSearch> findByTitleOrContent(String keyword, Pageable pageable);


    /**
     * 根据作者ID搜索文档
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 匹配的文档列表
     */
    Page<DocumentSearch> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * 根据状态搜索文档
     * @param status 状态
     * @param pageable 分页参数
     * @return 匹配的文档列表
     */
    Page<DocumentSearch> findByStatus(Integer status, Pageable pageable);
}
