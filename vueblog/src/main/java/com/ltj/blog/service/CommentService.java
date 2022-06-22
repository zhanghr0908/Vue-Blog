package com.ltj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ltj.blog.common.vo.PageCommentVo;
import com.ltj.blog.entity.Comment;

import java.util.List;

/**
 * 服务类
 */
public interface CommentService extends IService<Comment> {

    /**
     * 通过博客id和父评论id查找所有子评论 并按照时间倒序排序
     */
    List<PageCommentVo> getPageCommentListByDesc(Long blogId, Long parentCommentId);

    /**
     * 通过博客id和父评论id查找所有子评论
     */
    List<PageCommentVo> getPageCommentList(Long blogId, Long parentCommentId);

    /**
     * 获取评论数
     */
    Double getCommentCount(Long blogId);
}
