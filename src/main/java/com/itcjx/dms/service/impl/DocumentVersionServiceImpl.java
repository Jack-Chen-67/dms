package com.itcjx.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.entity.DocumentVersion;
import com.itcjx.dms.mapper.DocumentMapper;
import com.itcjx.dms.mapper.DocumentVersionMapper;
import com.itcjx.dms.service.DocumentVersionService;
import com.itcjx.dms.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentVersionServiceImpl extends ServiceImpl<DocumentVersionMapper, DocumentVersion> implements DocumentVersionService {

    @Autowired
    private DocumentVersionMapper documentVersionMapper;
    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public List<DocumentVersion> getVersionsByDocumentId(Long documentId) {
        return documentVersionMapper.getVersionsByDocumentId(documentId);
    }

    @Override
    public DocumentVersion getVersionByDocumentIdAndVersion(Long documentId, Integer version) {
        return documentVersionMapper.getVersionByDocumentIdAndVersion(documentId, version);
    }

    @Override
    public void saveDocumentVersion(DocumentVersion documentVersion) {
        documentVersionMapper.insert(documentVersion);
    }

    @Override
    public boolean rollbackToVersion(Long documentId, Integer version, Long userId) {
        DocumentVersion versionDoc = documentVersionMapper.getVersionByDocumentIdAndVersion(documentId, version);
        if(versionDoc == null){
            return false;
        }
        //获取当前文档
        Document currentDoc = documentMapper.selectById(documentId);
        if(currentDoc == null){
            return false;
        }
        //检查权限
        if(!currentDoc.getAuthorId().equals(userId)){
            return false;
        }
        // 保存当前版本到历史版本中
        DocumentVersion newVersion = new DocumentVersion();
        newVersion.setDocumentId(documentId);
        newVersion.setTitle(currentDoc.getTitle());
        newVersion.setContent(currentDoc.getContent());
        newVersion.setVersion(currentDoc.getVersion() + 1);
        newVersion.setAuthorId(userId);
        newVersion.setCreateTime(LocalDateTime.now());
        documentVersionMapper.insert(newVersion);

        // 更新文档到指定版本
        currentDoc.setTitle(versionDoc.getTitle());
        currentDoc.setContent(versionDoc.getContent());
        currentDoc.setVersion(versionDoc.getVersion());
        currentDoc.setUpdateTime(LocalDateTime.now());
        documentMapper.updateById(currentDoc);
        return true;
    }
}
