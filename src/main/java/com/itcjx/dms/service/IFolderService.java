package com.itcjx.dms.service;

import com.itcjx.dms.entity.Document;
import com.itcjx.dms.entity.Folder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.dms.util.Result;

import java.util.List;

/**
 * <p>
 * 文件夹表 服务类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
public interface IFolderService extends IService<Folder> {

    // 创建文件夹
    public Result<Folder> createFolder(Long userId, Folder folder);

    // 删除文件夹
    //public Result<String> deleteFolder(Long userId, Long id);

    // 获取子文件夹列表
    public Result<List<Folder>> getChildren(Long id);

    // 获取文件夹下的文档列表
    public Result<List<Document>> getDocuments(Long id);

}
