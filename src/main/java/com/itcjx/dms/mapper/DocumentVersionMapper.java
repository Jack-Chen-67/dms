package com.itcjx.dms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcjx.dms.entity.DocumentVersion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DocumentVersionMapper extends BaseMapper<DocumentVersion> {

    //获取文档所有版本
    @Select("SELECT * FROM doc_document_version WHERE document_id = #{documentId} ORDER BY version DESC")
    List<DocumentVersion> getVersionsByDocumentId(@Param("documentId") Long documentId);
    //获取文档特定版本
    @Select("SELECT * FROM doc_document_version WHERE document_id = #{documentId} AND version = #{version}")
    DocumentVersion getVersionByDocumentIdAndVersion(@Param("documentId") Long documentId,@Param("version") Integer version);
}
