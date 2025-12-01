package com.itcjx.dms.controller;

import com.itcjx.dms.entity.Document;
import com.itcjx.dms.entity.Folder;
import com.itcjx.dms.service.impl.FolderServiceImpl;
import com.itcjx.dms.util.JwtTokenUtil;
import com.itcjx.dms.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 文件夹表 前端控制器
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderServiceImpl folderService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private FolderServiceImpl folderServiceImpl;
    /*
    POST   /api/folders                # 创建文件夹（参数：folderName, parentId）
    GET    /api/folders/{id}/children  # 获取子文件夹列表 （参数：id）
    GET    /api/folders/{id}/documents # 获取文件夹下的文档列表 （参数：id, pageNum, pageSize）
     */

    // 创建文件夹
    @PostMapping
    public Result<Folder> createFolder(@RequestHeader("Authorization") String token,
                                       @RequestBody Folder folder){
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return folderServiceImpl.createFolder(userId,folder);
    }

    // 获取子文件夹列表
    @GetMapping("/{id}/children")
    public Result<List<Folder>> getChildren(@PathVariable Long id){
        return folderServiceImpl.getChildren(id);
    }

    // 获取文件夹下的文档列表
    @GetMapping("/{id}/documents")
    public Result<List<Document>> getDocuments(@PathVariable Long id){
        return folderServiceImpl.getDocuments(id);
    }
}
