package com.itcjx.dms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcjx.dms.entity.Document;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.dms.util.Result;

import java.util.List;

/**
 * <p>
 * 文档表 服务类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
public interface IDocumentService extends IService<Document> {
    // 创建文档
    Result<Document> createDocument(Long userId,Document document);
    // 获取文档详情
    Result<Document> getDocument(Long userId,Long id);
    // 更新文档
    Result<Document> updateDocument(Long userId,Document document);
    // 删除文档
    Result<String> deleteDocument(Long userId,Long id);
    // 分页查询文档
    Result<List< Document>> pageDocument(Long folderId,Integer pageNum,Integer pageSize);
}
