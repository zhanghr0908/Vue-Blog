package com.ltj.blog.es;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.mq.BlogMQIndexMessage;

import java.util.List;

public interface BlogDocumentService {
    /*
        分页搜索
     */
    IPage searchData(Page page, String keyword);

    /*
        es初始化
     */
    int initData(List<Blog> records);

    /*
        添加或更新索引
     */
    void createOrUpdateIndex(BlogMQIndexMessage message);

    /*
        删除索引
     */
    void deleteIndex(BlogMQIndexMessage message);
}
