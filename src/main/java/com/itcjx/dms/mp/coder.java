package com.itcjx.dms.mp;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class coder {
    // 数据库配置
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/dms?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "a001";

    // 需要生成代码的表
    private static final List<String> TABLES = Arrays.asList(
            "sys_user", "sys_role", "sys_user_role", "sys_permission",
            "doc_folder", "doc_document", "doc_tag", "doc_document_tag",
            "doc_attachment", "ai_generation_record"
    );
    public static void main(String[] args) {
        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> builder
                        .author("cjx")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .commentDate("yyyy-MM-dd")
                        .disableOpenDir()// 生成后不打开文件夹
                )
                .packageConfig(builder -> builder
                        .parent("com.itcjx.dms")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .xml("mapper")
                        .pathInfo(Collections.emptyMap())//静止生成到默认路径
                )
                .strategyConfig(builder -> builder
                        .addInclude(TABLES)//添加所有表
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation() // 生成字段注解
                        .naming(NamingStrategy.underline_to_camel) // 表名下划线转驼峰
                        .columnNaming(NamingStrategy.underline_to_camel) // 字段下划线转驼峰
                        .idType(com.baomidou.mybatisplus.annotation.IdType.AUTO) // 主键策略
                        .logicDeleteColumnName("is_deleted")// 逻辑删除字段
                        .build()
                        .controllerBuilder()
                        .enableRestStyle()// 生成RESTful风格控制器
                        .enableHyphenStyle() // 使用连字符风格
                        .build()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .build()
                        .mapperBuilder()
                        .enableMapperAnnotation() // 启用Mapper注解
                        .enableBaseResultMap() // 生成基础ResultMap
                        .enableBaseColumnList() // 生成基础ColumnList
                        .build()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
