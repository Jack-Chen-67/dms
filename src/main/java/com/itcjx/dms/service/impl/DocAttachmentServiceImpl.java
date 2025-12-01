package com.itcjx.dms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcjx.dms.entity.DocAttachment;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.mapper.DocAttachmentMapper;
import com.itcjx.dms.mapper.DocumentMapper;
import com.itcjx.dms.service.DocAttachmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.dms.util.FileUploadUtil;
import com.itcjx.dms.util.Result;
//import jakarta.annotation.Resource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 附件表 服务实现类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
@Service
public class DocAttachmentServiceImpl extends ServiceImpl<DocAttachmentMapper, DocAttachment> implements DocAttachmentService {

    @Autowired
    private FileUploadUtil fileUploadUtil;
    @Autowired
    private DocAttachmentMapper docAttachmentMapper;
    @Autowired
    private DocumentMapper documentMapper;

    // 上传文件
    @Override
    public Result<DocAttachment> uploadFile(Long userId, MultipartFile file, Long docId) {
        try{
            // 上传文件
            String filePath = fileUploadUtil.uploadFile(file, userId);

            // 保存附件信息到数据库
            DocAttachment attachment = new DocAttachment();
            attachment.setDocId(docId);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFilePath(filePath);
            attachment.setFileSize(file.getSize());
            attachment.setUploadTime(LocalDateTime.now());

            docAttachmentMapper.insert(attachment);

            return Result.success(attachment);
        }catch (Exception e){
            return Result.error(500,e.getMessage());
        }
    }

    // 获取文档附件列表
    @Override
    public Result<List<DocAttachment>> getDocAttachments(Long userId,Long docId) {
        if(docId == null){
            return Result.error(500,"文档错误");
        }
        Document document = documentMapper.selectById(docId);
        if(!document.getAuthorId().equals(userId)){
            return Result.error(500,"权限不足");
        }
        List<DocAttachment> attachments = docAttachmentMapper.selectList(new LambdaQueryWrapper<DocAttachment>()
                .eq(DocAttachment::getDocId, docId));
        return Result.success(attachments);
    }

    // 删除文档附件
    @Override
    public Result<String> deleteDocAttachment(Long userId,  Long attachmentId) {
        try{
            //获取附件信息
            DocAttachment attachment = docAttachmentMapper.selectById(attachmentId);
            if(attachment == null){
                return Result.error(500,"删除失败，附件不存在");
            }
            //检查权限
            Long docId = attachment.getDocId();
            Document document = documentMapper.selectById(docId);
            if(!document.getAuthorId().equals(userId)){
                return Result.error(500,"删除失败，权限不足");
            }
            //删除文件
            boolean isDeleted = fileUploadUtil.deleteFile(attachment.getFilePath());
            if(!isDeleted){
                return Result.error(500,"删除失败，文件不存在");
            }
            //删除数据库记录
            docAttachmentMapper.deleteById(attachmentId);
            return Result.success("删除成功");
        }catch (Exception e){
            return Result.error(500,"删除失败"+e.getMessage());
        }
    }

    // 下载文档附件
    @Override
    public ResponseEntity<Resource> downloadAttachment(Long userId, Long attachmentId) {
        try{
            //获取附件信息
            DocAttachment attachment = docAttachmentMapper.selectById(attachmentId);
            if(attachment == null){
                return ResponseEntity.notFound().build();
            }
            //检查权限
            Long docId = attachment.getDocId();
            Document document = documentMapper.selectById(docId);
            if(!document.getAuthorId().equals(userId)){
                return ResponseEntity.notFound().build();
            }
            // 检查文件是否存在
            Path filePath = Paths.get(attachment.getFilePath());
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            // 创建资源
            InputStreamResource resource = new InputStreamResource(new FileInputStream(attachment.getFilePath()));
            // 设置响应头
            String encodedFileName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8.toString());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .contentLength(attachment.getFileSize())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }catch (FileNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
