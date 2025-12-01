package com.itcjx.dms.service;

import com.itcjx.dms.DmsApplication;
import com.itcjx.dms.entity.Document;
import com.itcjx.dms.service.impl.DocumentServiceImpl;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest(classes = DmsApplication.class)
@SpringJUnitConfig
public class DocumentServiceTest {

    @Autowired
    private DocumentServiceImpl documentService;

    @Test
    public void testCreateDocument() {
        Document document = new Document();
        document.setTitle("测试文档");
        document.setContent("测试内容");
        document.setFolderId(0L);

        // 测试创建文档
        // 注意：这里需要真实的用户ID
        // com.itcjx.dms.util.Result<Document> result = documentService.createDocument(1L, document);
        // assertNotNull(result);
        // assertEquals(200, result.getCode());
        // assertNotNull(result.getData().getId());
    }

    @Test
    public void testUpdateDocument() {
        // 测试更新文档逻辑
    }
}
