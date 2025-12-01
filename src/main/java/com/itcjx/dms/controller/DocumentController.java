package com.itcjx.dms.controller;

import com.itcjx.dms.annotation.OperationLog;
import com.itcjx.dms.annotation.RateLimiterAnno;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.entity.DocumentSearch;
import com.itcjx.dms.entity.DocumentVersion;
import com.itcjx.dms.service.DocumentSearchService;
import com.itcjx.dms.service.impl.DocumentServiceImpl;
import com.itcjx.dms.util.JwtTokenUtil;
import com.itcjx.dms.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 文档表 前端控制器
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentServiceImpl documentServiceImpl;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private DocumentSearchService documentSearchService;
    /*
    POST   /api/documents              # 创建文档（参数：title, content, folderId...）
    GET    /api/documents/{id}         # 获取单个文档详情
    PUT    /api/documents/{id}         # 更新文档（参数：title, content...）
    DELETE /api/documents/{id}         # 删除文档（逻辑删除）
    GET    /api/documents              # 分页查询文档（参数：folderId, keyword, page, size）
     */

    // 创建文档
    @OperationLog(value = "创建文档")
    @PostMapping
    public Result<Document> createDocument(@RequestHeader("Authorization") String token,
                                      @Valid @RequestBody Document document) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return documentServiceImpl.createDocument(userId,document);
    }
    // 获取单个文档详情
    @OperationLog(value = "获取单个文档详情")
    @RateLimiterAnno(count = 20, time = 60, limitTip = "获取文档详情过于频繁，请稍后再试")
    @GetMapping("/{id}")
    public Result<Document> getDocumentById(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return documentServiceImpl.getDocument(userId,id);
    }
    // 更新文档
    @OperationLog(value = "更新文档")
    @PutMapping("/{id}")
    public Result<Document> updateDocument(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id,
                                            @Valid @RequestBody Document document) {
        document.setId(id);
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return documentServiceImpl.updateDocument(userId,document);
    }
    // 删除文档
    @OperationLog(value = "删除文档")
    @DeleteMapping("/{id}")
    public Result<String> deleteDocument(@RequestHeader("Authorization") String token,
                                         @PathVariable Long id) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return documentServiceImpl.deleteDocument(userId,id);
    }

    // 分页查询文档
    @OperationLog(value = "查询文档列表")
    @RateLimiterAnno(count = 20, time = 60, limitTip = "查询文档列表过于频繁，请稍后再试")
    @RequestMapping
    public Result<List< Document>> pageDocument(@RequestParam Long folderId,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return documentServiceImpl.pageDocument(folderId,pageNum,pageSize);
    }

    //搜索文档
    @OperationLog("搜索文档")
    @RateLimiterAnno(count = 15, time = 60, limitTip = "搜索文档过于频繁，请稍后再试")
    @GetMapping("/search")
    public Result<Page<DocumentSearch>> searchDocuments(@RequestParam String keyword,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentSearch> documents = documentSearchService.searchDocuments(keyword, pageable);
        return Result.success(documents);
    }

    // 获取文档版本历史
    @OperationLog("获取文档版本历史")
    @GetMapping("/{id}/versions")
    public Result<List<DocumentVersion>> getDocumentVersions(@RequestHeader("Authorization") String token,
                                                             @PathVariable Long id) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return documentServiceImpl.getDocumentVersions(userId, id);
    }

    // 回滚到指定版本
    @OperationLog("回滚文档版本")
    @PostMapping("/{id}/rollback")
    public Result<String> rollbackToVersion(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id,
                                            @RequestParam Integer version) {
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return documentServiceImpl.rollbackToVersion(userId, id, version);
    }

}
