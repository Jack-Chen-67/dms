# 文档管理系统 DMS

## 项目简介
这是一个文档管理系统。项目采用现代化的 Java 技术栈构建，适合作为学习 Spring Boot 的实战项目。

## 核心功能
- 用户注册与登录（JWT 认证）
- 文档上传与在线预览
- 文件夹层级管理
- 标签分类系统
- 操作日志记录
- 接口限流与安全防护
- AI 文档生成（基于 Ollama）
- 全文搜索引擎（Elasticsearch）
- 异步消息处理（RabbitMQ）

## 技术架构
- 后端框架：Spring Boot 3.5 + MyBatis-Plus
- 数据库：MySQL 8.0
- 搜索引擎：Elasticsearch
- 消息队列：RabbitMQ
- 缓存：Redis
- 安全框架：Spring Security + JWT
- API 文档：SpringDoc OpenAPI (Swagger)
- 构建工具：Maven

## 环境依赖
- JDK 17+
- MySQL 8.0
- Elasticsearch 8.x
- RabbitMQ 3.x
- Redis 6.x
- Ollama (用于 AI 功能)

## 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/Jack-Chen-67/dms.git
cd dms
```

### 2. 数据库配置
在 `src/main/resources/application.yml` 中修改以下配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dms?...
    username: your_username
    password: your_password
```

### 3. 启动服务
```bash
# 使用 Maven 启动
./mvnw spring-boot:run

# 或者打包后运行
./mvnw clean package
java -jar target/dms-0.0.1-SNAPSHOT.jar
```
