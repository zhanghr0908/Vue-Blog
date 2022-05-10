package com.ltj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ltj.blog.common.vo.BlogInfoVo;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.entity.Blog;

import java.util.List;
import java.util.Map;

/**
 * 服务类
 */
public interface BlogService extends IService<Blog> {

    /**
     * 通过分类名查找属于该分类的博客list
     */
    List<BlogInfoVo> queryBlogInfoListByCategoryName(String categoryName);

    /**
     * 查询最热博客
     */
    List<Blog> queryHotBlogByLimit(int limit);

    /**
     * 查询最新博客
     */
    List<Blog> queryNewBlogByLimit(int limit);

    /**
     * 查询本周热议博客
     */
    List<Map<String, Object>> queryWeekHotBlogByLimit(int limit);


    void incrCommentCountAndUnionForWeekRank(long blogId, boolean isIncr);
}
