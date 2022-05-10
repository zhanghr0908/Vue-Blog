package com.ltj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ltj.blog.common.vo.PageCommentVo;
import com.ltj.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 */
@Mapper
@Repository
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据创建时间倒序 根据博客id和父评论id查询所有子评论
     */
    @Select("select id, nickname, content, website,avatar, create_time, is_admin_comment,parent_comment_nickname  from comment where blog_id=#{blogId} and parent_comment_id=#{parentCommentId} order by create_time desc")
    List<PageCommentVo> getPageCommentListByPageAndParentCommentIdByDesc(@Param("blogId") long blogId, @Param("parentCommentId") long parentCommentId);

    /**
     * 根据博客id和父评论id查询所有子评论
     */
    @Select("select id, nickname, content, website,avatar, create_time, is_admin_comment,parent_comment_nickname  from comment where blog_id=#{blogId} and parent_comment_id=#{parentCommentId} order by create_time")
    List<PageCommentVo> getPageCommentListByPageAndParentCommentId(@Param("blogId") long blogId, @Param("parentCommentId") long parentCommentId);


    @Select("select count(*) from comment where blog_id=#{blogId}")
    Double queryCountNum(Long id);
}
