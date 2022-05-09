package com.ltj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.service.BlogService;
import com.ltj.blog.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class ThreadServiceImpl implements ThreadService {

    @Autowired
    private BlogService blogService;

    @Override
    @Async("taskExecutor")
    public void updateViewCount(Blog blog) {
        Blog blogUpdate = new Blog();
        blogUpdate.setViews(blog.getViews() + 1);
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getId, blog.getId());
        queryWrapper.eq(Blog::getViews, blog.getViews());
        blogService.update(blogUpdate, queryWrapper);
    }
}
