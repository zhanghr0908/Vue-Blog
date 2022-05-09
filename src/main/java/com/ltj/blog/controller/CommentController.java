package com.ltj.blog.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.annotation.AccessLimit;
import com.ltj.blog.common.annotation.VisitLogger;
import com.ltj.blog.common.util.MyBlogUtils;
import com.ltj.blog.common.vo.PageCommentVo;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.entity.Comment;
import com.ltj.blog.service.BlogService;
import com.ltj.blog.service.CommentService;
import com.ltj.blog.service.MailService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论前端控制器
 */
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private  MailService mailService;
    @Autowired
    private BlogService blogService;

    Logger logger = LoggerFactory.getLogger(CommentController.class);

    /**
     * 首页 - 点击具体博客， 获取某个博客下的所有评论
     */
    @GetMapping("/comment/{blogId}")
    public Result queryCommentByBlogId(@PathVariable(name = "blogId") Long blogId) {
        // 查询父评论
        List<PageCommentVo> pageCommentVoList = commentService.getPageCommentListByDesc(blogId, -1L);
        // 查询子评论
        for (PageCommentVo pageCommentVo : pageCommentVoList) {
            List<PageCommentVo> reply = commentService.getPageCommentList(blogId, pageCommentVo.getId());
            pageCommentVo.setReplyComments(reply);
        }
        return Result.succ(pageCommentVoList);
    }

    /**
     * 首页 - 点击具体博客 - 发表评论， 提交评论
     */
    @AccessLimit(seconds = 30, maxCount = 1, msg = "30秒内只能提交一次评论")
    @VisitLogger(behavior = "提交评论")
    @PostMapping("/comment/add")
    public Result saveComment(@Validated @RequestBody Comment comment, HttpServletRequest request) {
        if (comment.getContent().contains("<script>") || comment.getEmail().contains("<script>") || comment.getNickname().contains("<script>") || comment.getWebsite().contains("<script>")) {
            return Result.fail("非法输入");
        }
        Comment temp = new Comment();
        temp.setCreateTime(LocalDateTime.now());
        temp.setIp(request.getHeader("x-forwarded-for"));
        BeanUtil.copyProperties(comment, temp, "id", "ip", "createTime");
        MyBlogUtils.cleanString(comment.getContent());
        commentService.saveOrUpdate(temp);
        // 本周热议可能会发生变化，要对redis中缓存进行更改
        blogService.incrCommentCountAndUnionForWeekRank(comment.getBlogId(), true);
        //博主的回复向被回复者发送提示邮件
        if(comment.getIsAdminComment() == 1 && comment.getParentCommentId() != -1){
//            Comment parentComment = commentService.getOne(new QueryWrapper<Comment>().eq("nickname", comment.getParentCommentNickname()));
            Comment parentComment = commentService.getOne(new QueryWrapper<Comment>().eq("id", comment.getParentCommentId()));
            String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
            if (parentComment.getEmail().matches(regex)) {
                mailService.sendSimpleMail(parentComment.getEmail(), "辣条君博客评论回复", "您的的评论："+parentComment.getContent()+"\n博主回复内容："+comment.getContent());
                logger.info("邮件发送成功");
            }
        }
        return Result.succ(null);
    }

    /**
     * 后台 - 搜索框， 分页查询某个博客下的评论
     */
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/comment/detail")
    public Result queryCommentListByPageId(@RequestParam(defaultValue = "1") Long blogId, @RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize ) {
        Page page = new Page(currentPage, pageSize);
        IPage pageData = commentService.page(page, new QueryWrapper<Comment>().eq("blog_id",blogId).orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 评论管理， 分页查询所有评论
     */
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/commentList")
    public Result queryCommentListByPage(@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize ) {
        Page page = new Page(currentPage, pageSize);
        IPage pageData = commentService.page(page, new QueryWrapper<Comment>().orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 评论管理 - 是否公开， 修改评论的状态
     */
    @RequiresAuthentication
    @RequiresPermissions("user:update")
    @RequestMapping("comment/publish/{id}")
    public Result updateCommentViewStatusById(@PathVariable(name = "id")String id){
        Comment comment = commentService.getById(id);
        comment.setIsPublished(!comment.getIsPublished());
        commentService.saveOrUpdate(comment);
        return Result.succ(null);
    }

    /**
     * 后台 - 评论管理 - 编辑， 修改评论
     */
    @RequiresAuthentication
    @RequiresPermissions("user:update")
    @RequestMapping("comment/update")
    public Result updateCommentById(@Validated @RequestBody Comment comment){
        if(comment==null){
            return Result.fail("不能为空");
        }
        MyBlogUtils.cleanString(comment.getContent());
        commentService.saveOrUpdate(comment);
        return Result.succ(null);
    }

    /**
     * 后台 - 评论管理 - 删除， 删除评论
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:delete")
    @RequestMapping("comment/delete/{id}")
    public Result deleteCommentById(@PathVariable(name = "id")String id){
        Long blogId = commentService.getById(id).getBlogId();
        if (commentService.removeById(id)) {
            // 本周热议可能会发生变化，要对redis中缓存进行更改
            blogService.incrCommentCountAndUnionForWeekRank(blogId, false);
            return Result.succ(null);
        } else {
            return Result.fail("删除失败");
        }
    }

}
