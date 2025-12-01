package com.itcjx.dms.service;

import com.itcjx.dms.entity.DocAttachment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.dms.util.Result;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 附件表 服务类
 * </p>
 *
 * @author cjx
 * @since 2025-08-09
 */
public interface DocAttachmentService extends IService<DocAttachment> {
    /**
     * 上传文件
     * @param docId 文档id
     * @param userId 用户id
     * @param //fileName 文件名
     * @param //filePath 文件路径
     * @param //fileSize 文件大小
     * @return
     */

    //上传文件
    Result<DocAttachment> uploadFile(Long userId, MultipartFile file, Long docId);
    //获取文档附件列表
    Result<List<DocAttachment>> getDocAttachments(Long userId,Long docId);
    //删除文档附件
    Result<String> deleteDocAttachment(Long userId, Long attachmentId);
    //下载附件
    ResponseEntity<Resource> downloadAttachment(Long userId,Long attachmentId);

}
