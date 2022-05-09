package com.ltj.blog.es;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 *  博客文档实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "blog", createIndex = true)
public class BlogDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private Long blogId;

    // ik分词器
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;
    // ik分词器
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String description;

    private Long authorId;
    private String authorName;

    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Date)
    private Date createTime;
}
