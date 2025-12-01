package com.itcjx.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.entity.Folder;
import com.itcjx.dms.mapper.DocumentMapper;
import com.itcjx.dms.mapper.FolderMapper;
import com.itcjx.dms.service.IFolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.dms.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文件夹表 服务实现类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements IFolderService {

    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private DocumentMapper documentMapper;

    // 创建文件夹
    @Override
    public Result<Folder> createFolder(Long userId, Folder folder) {
        if(userId == null || folder.getCreateUserId() != userId){
            return Result.error(400,"用户错误");
        }
        folderMapper.insert(folder);
        return Result.success(folder);
    }

    // 获取子文件夹列表
    @Override
    public Result<List<Folder>> getChildren(Long id) {
        if(id == null){
            return Result.error(400,"文件夹错误");
        }
        List<Folder> folders = folderMapper.selectList(new LambdaQueryWrapper<Folder>()
                .eq(Folder::getParentId, id));
        return Result.success(folders);
    }

    // 获取文件夹下的文档列表
    @Override
    public Result<List<Document>> getDocuments(Long id) {
        if(id == null){
            return Result.error(400,"文件夹错误");
        }
        List<Document> documents = documentMapper.selectList(new LambdaQueryWrapper<Document>()
                .eq(Document::getFolderId, id));
        return Result.success(documents);
    }
}
