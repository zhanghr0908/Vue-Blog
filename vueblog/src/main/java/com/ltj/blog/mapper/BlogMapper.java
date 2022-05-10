package com.ltj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ltj.blog.common.vo.BlogInfoVo;
import com.ltj.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 */
@Mapper
@Repository
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     * 根据分类查询博客
     */
    List<BlogInfoVo> queryBlogByTypeName(String typeName);
}
