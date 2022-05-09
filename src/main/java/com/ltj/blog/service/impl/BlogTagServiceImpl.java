package com.ltj.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.blog.entity.BlogTag;
import com.ltj.blog.mapper.BlogTagMapper;
import com.ltj.blog.service.BlogTagService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {

}
