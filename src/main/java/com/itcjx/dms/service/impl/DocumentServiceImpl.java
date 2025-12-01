package com.itcjx.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.entity.DocumentSearch;
import com.itcjx.dms.entity.DocumentVersion;
import com.itcjx.dms.mapper.DocumentMapper;
import com.itcjx.dms.mapper.DocumentVersionMapper;
import com.itcjx.dms.service.DocumentSearchService;
import com.itcjx.dms.service.DocumentVersionService;
import com.itcjx.dms.service.IDocumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.dms.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 文档表 服务实现类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Slf4j
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements IDocumentService {

    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DocumentSearchService documentSearchService;
    @Autowired
    private DocumentVersionService documentVersionService;

    private static final String DOC_CACHE_PREFIX = "doc:info:";
    private static final long DOC_CACHE_EXPIRE = 3600; // 1小时

    // 创建文档
    @Override
    public Result<Document> createDocument(Long userId, Document document) {
        if(userId == null){
            return Result.error(400,"用户不存在");
        }
        if(document.getFolderId() == null){
            return Result.error(400,"文件夹不存在");
        }
        if(document.getTitle() == null){
            return Result.error(400,"标题不能为空");
        }
        document.setAuthorId(userId);
        document.setStatus((byte)1);//默认发布状态
        int save = documentMapper.insert(document);
        if(save<=0){
            return Result.error(500,"创建失败");
        }
        //保存第一个版本到版本历史表
        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setDocumentId(document.getId());
        documentVersion.setTitle(document.getTitle());
        documentVersion.setContent(document.getContent());
        documentVersion.setVersion(1);
        documentVersion.setAuthorId(userId);
        documentVersionService.saveDocumentVersion(documentVersion);

        //同步到elasticsearch
        try {
            DocumentSearch documentSearch = new DocumentSearch();
            BeanUtils.copyProperties(document, documentSearch);
            documentSearch.setAuthorId(userId);
            log.info("同步新文档到Elasticsearch，文档ID: {}, 标题: {}", document.getId(), document.getTitle());
            documentSearchService.saveDocument(documentSearch);
            log.info("成功同步文档到Elasticsearch，文档ID: {}", document.getId());
        } catch (Exception e) {
            // 记录日志，但不影响主流程
            log.error("同步文档到Elasticsearch失败，文档ID: " + document.getId(), e);
        }


        return Result.success(document);
    }

    // 获取文档详情
    @Override
    public Result<Document> getDocument(Long userId, Long id) {
        if(userId == null){
            return Result.error(400,"用户不存在");
        }
        if(id == null){
            return Result.error(404,"文档不存在");
        }
        //先从缓存中获取
        String cacheKey = DOC_CACHE_PREFIX + id;
        //redisTemplate.delete(cacheKey);//先删除原先有的缓存
        Document document = null;
        //Document document = (Document) redisTemplate.opsForValue().get(cacheKey);
        try{
            Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
            if (cachedObject instanceof Document) {
                document = (Document) cachedObject;
            }
        }catch (Exception e){
            // 如果缓存读取失败，忽略缓存，直接从数据库获取
            // 同时删除错误的缓存
            redisTemplate.delete(cacheKey);
        }
        //缓存没有，从数据库中获取
        if(document == null){
            document = documentMapper.selectById(id);
            if(document == null || document.getIsDeleted() == 1){
                return Result.error(404,"文档不存在");
            }
            if(!document.getAuthorId().equals(userId)){
                return Result.error(403,"没有权限");
            }
            //放入缓存
            redisTemplate.opsForValue().set(cacheKey, document, DOC_CACHE_EXPIRE, TimeUnit.SECONDS);
        }
        //Document document = documentMapper.selectById(id);
        return Result.success(document);
    }

    // 更新文档
    @Override
    public Result<Document> updateDocument(Long userId,Document document) {
        if(userId == null){
            return Result.error(400,"用户不存在");
        }
        if(document.getId() == null){
            return Result.error(404,"文档不存在");
        }
        //检查操作人是否是文档作者
        Document documentDB = documentMapper.selectById(document.getId());
        if(documentDB == null){
            return Result.error(404,"文档不存在");
        }
        if(!documentDB.getAuthorId().equals(userId)){
            return Result.error(403,"没有权限");
        }

        // 处理版本号为null的情况
        if (document.getVersion() == null) {
            document.setVersion(documentDB.getVersion());
        }
        document.setVersion(document.getVersion()+1);
        int update = documentMapper.updateById(document);
        if(update<=0){
            return Result.error(500,"更新失败");
        }
        // 删除缓存
        String cacheKey = DOC_CACHE_PREFIX + document.getId();
        redisTemplate.delete(cacheKey);

        //保存新版本到版本历史表
        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setDocumentId(document.getId());
        documentVersion.setTitle(document.getTitle());
        documentVersion.setContent(document.getContent());
        documentVersion.setVersion(document.getVersion());
        documentVersion.setAuthorId(userId);
        documentVersionService.saveDocumentVersion(documentVersion);

        //同步到elasticsearch
        try {
            DocumentSearch documentSearch = new DocumentSearch();
            BeanUtils.copyProperties(document, documentSearch);
            documentSearch.setAuthorId(userId);
            // 确保时间字段也被正确设置
            documentSearch.setCreateTime(document.getCreateTime());
            documentSearch.setUpdateTime(document.getUpdateTime());
            documentSearchService.saveDocument(documentSearch);
        } catch (Exception e) {
            // 记录日志，但不影响主流程
            log.error("同步文档到Elasticsearch失败，文档ID: " + document.getId(), e);
        }

        return Result.success(document);
    }

    // 删除文档
    @Override
    public Result<String> deleteDocument(Long userId,Long id) {
        if(userId == null){
            return Result.error(400,"用户不存在");
        }
        if(id == null){
            return Result.error(404,"文档不存在");
        }
        //检查操作人是否是文档作者
        Document document = documentMapper.selectById(id);
        if(document == null){
            return Result.error(404,"文档不存在");
        }
        if(!document.getAuthorId().equals(userId)){
            return Result.error(403,"没有权限");
        }

        /*
        int delete = documentMapper.deleteById(id);
        if(delete<=0){
            return Result.error(500,"删除失败");
        }
         */

        // 逻辑删除
        document.setIsDeleted((byte) 1);
        documentMapper.updateById(document);

        // 删除缓存
        String cacheKey = DOC_CACHE_PREFIX + id;
        redisTemplate.delete(cacheKey);

        // 从Elasticsearch中删除
        documentSearchService.deleteDocument(id);

        return Result.success("删除成功");
    }

    // 分页查询文档
    @Override
    public Result<List<Document>> pageDocument(Long folderId, Integer pageNum, Integer pageSize) {
        // 只查询未删除的文档
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        if (folderId != null && folderId > 0) {
            queryWrapper.eq("folder_id", folderId);
        }
        queryWrapper.orderByDesc("create_time");

        java.util.List<Document> documents = documentMapper.selectList(queryWrapper);
        return Result.success(documents);
    }

    //获取文档中所有版本
    //@Override
    public Result<List<DocumentVersion>> getDocumentVersions(Long userId,Long documentId) {
        if(userId == null){
            return Result.error(400,"用户不存在");
        }
        if(documentId == null){
            return Result.error(404,"文档不存在");
        }
        Document document = documentMapper.selectById(documentId);
        if(document == null){
            return Result.error(404,"文档不存在");
        }
        //检查权限
        if(!document.getAuthorId().equals(userId)){
            return Result.error(403,"没有权限");
        }
        List<DocumentVersion> documentVersions = documentVersionService.getVersionsByDocumentId(documentId);
        return Result.success(documentVersions);
    }

    //回滚到指定版本
    public Result<String> rollbackToVersion(Long userId,Long documentId,Integer version) {
        if(userId == null){
            return Result.error(400,"用户不存在");
        }
        if(documentId == null){
            return Result.error(404,"文档不存在");
        }
        boolean success = documentVersionService.rollbackToVersion(documentId, version, userId);
        if (success) {
            // 删除缓存
            String cacheKey = DOC_CACHE_PREFIX + documentId;
            redisTemplate.delete(cacheKey);

            return Result.success("文档已成功回滚到版本 " + version);
        } else {
            return Result.error(400, "文档回滚失败");
        }
    }
}
