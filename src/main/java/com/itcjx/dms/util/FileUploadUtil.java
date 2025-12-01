package com.itcjx.dms.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUploadUtil {

    // 文件存储路径，可以根据需要修改
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 上传文件
     * @param file 上传的文件
     * @param userId 用户ID（用于创建用户特定的文件夹）
     * @return 文件存储路径
     * @throws //IOException
     */
    public String uploadFile(MultipartFile file, Long userId) throws IOException {
        // 创建用户特定的文件夹
        String userDir = UPLOAD_DIR + userId + "/";
        File directory = new File(userDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(userDir + uniqueFilename);
        Files.write(filePath, file.getBytes());

        // 返回相对路径
        return userDir + uniqueFilename;
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }
}
