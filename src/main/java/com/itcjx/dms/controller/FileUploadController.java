package com.itcjx.dms.controller;

import com.itcjx.dms.annotation.OperationLog;
import com.itcjx.dms.annotation.RateLimiterAnno;
import com.itcjx.dms.entity.DocAttachment;
import com.itcjx.dms.service.impl.DocAttachmentServiceImpl;
import com.itcjx.dms.util.FileUploadUtil;
import com.itcjx.dms.util.JwtTokenUtil;
import com.itcjx.dms.util.Result;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private FileUploadUtil fileUploadUtil;
    @Autowired
    private DocAttachmentServiceImpl docAttachmentService;

    /*
    * 上传文件
    * */
    @OperationLog("上传文件")
    @RateLimiterAnno(count = 10, time = 60, limitTip = "文件上传过于频繁，请稍后再试")
    @PostMapping("/upload")
    public Result<DocAttachment> uploadFile(@RequestHeader("Authorization") String token,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestParam(required = false) Long docId){
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return docAttachmentService.uploadFile(userId,file,docId);
    }

    /*
    * 获取文件附件列表
    * */
    @OperationLog("获取文件附件列表")
    @GetMapping("/getAttachments/{docId}")
    public Result<List<DocAttachment>> getDocAttachments(@RequestHeader("Authorization") String token,
                                                         @PathVariable Long docId){
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return docAttachmentService.getDocAttachments(userId,docId);
    }

    /*
    * 删除文件
    * */
    @OperationLog("删除文件")
    @PostMapping("/deleteAttachment/{attachmentId}")
    public Result<String> deleteAttachment(@RequestHeader("Authorization") String token,
                                           @PathVariable Long attachmentId){
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return docAttachmentService.deleteDocAttachment(userId,attachmentId);
    }

    /*
    * 下载附件
    * */
    @OperationLog("下载附件")
    @GetMapping("/downloadAttachment/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@RequestHeader("Authorization") String token,
                                                       @PathVariable Long attachmentId){
        token = token.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return docAttachmentService.downloadAttachment(userId,attachmentId);
    }

}
