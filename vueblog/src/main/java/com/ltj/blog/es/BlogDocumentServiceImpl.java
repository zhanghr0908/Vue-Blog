package com.ltj.blog.es;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.entity.Type;
import com.ltj.blog.entity.User;
import com.ltj.blog.mapper.BlogMapper;
import com.ltj.blog.mapper.TypeMapper;
import com.ltj.blog.mapper.UserMapper;
import com.ltj.blog.mq.BlogMQIndexMessage;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BlogDocumentServiceImpl implements BlogDocumentService {

    @Resource
    private BlogDocumentMapper blogDocumentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private TypeMapper typeMapper;
    @Resource
    private BlogMapper blogMapper;

    private BlogDocument blogToBlogDocument(Blog blog) {
        BlogDocument blogDocument = new BlogDocument();
        blogDocument.setId(blog.getId());
        blogDocument.setBlogId(blog.getId());
        blogDocument.setTitle(blog.getTitle());
        blogDocument.setDescription(blog.getDescription());
        blogDocument.setCreateTime(DateUtil.date(blog.getCreateTime()).toJdkDate());

        blogDocument.setAuthorId(blog.getUserId());
        User user = userMapper.selectById(blog.getUserId());
        blogDocument.setAuthorName(user.getUsername());

        blogDocument.setCategoryId(blog.getTypeId());
        Type type = typeMapper.selectById(blog.getTypeId());
        blogDocument.setCategoryName(type.getTypeName());
        return blogDocument;
    }

    @Override
    public IPage searchData(Page page, String keyword) {
        // 分页信息 mybatis plus 的page 转成 jpa 的page
        Long current = page.getCurrent() - 1;
        Long size = page.getSize();
        PageRequest pageable = PageRequest.of(current.intValue(), size.intValue());
        // 搜索es得到pageData
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "categoryName");
        org.springframework.data.domain.Page<BlogDocument> documents = blogDocumentMapper.search(multiMatchQueryBuilder, pageable);
        log.info("查询 - {} - 得到的结果如下----->{}个查询结果，一共{}页",keyword, documents.getTotalElements(), documents.getTotalPages());
        // 结果信息 jpa的pageData 转成 mybatis plus 的pageData
        IPage pageData = new Page(page.getCurrent(), page.getSize(), documents.getTotalElements());
        pageData.setRecords(documents.getContent());
        return pageData;
    }

    @Override
    public int initData(List<Blog> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        List<BlogDocument> blogDocumentList = new ArrayList<>();
        for(Blog blog : records) {
            BlogDocument blogDocument = blogToBlogDocument(blog);
            blogDocumentList.add(blogDocument);
        }
        blogDocumentMapper.saveAll(blogDocumentList);
        return blogDocumentList.size();
    }

    @Override
    public void createOrUpdateIndex(BlogMQIndexMessage message) {
        Long blogId = message.getBlogId();
        Blog blog = blogMapper.selectById(blogId);
        if (blogDocumentMapper.existsById(blogId)) {
            deleteIndex(message);
        }
        BlogDocument blogDocument = blogToBlogDocument(blog);
        BlogDocument saveBlogDocument = blogDocumentMapper.save(blogDocument);
        log.info("es 索引更新成功-----------> {} ", saveBlogDocument.toString());
    }

    @Override
    public void deleteIndex(BlogMQIndexMessage message) {
        Long blogId = message.getBlogId();
        blogDocumentMapper.deleteById(blogId);
        log.info("es 索引删除成功-----------> {} ", message.toString());
    }
}
