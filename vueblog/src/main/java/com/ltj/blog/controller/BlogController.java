package com.ltj.blog.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.config.RabbitMQConfig;
import com.ltj.blog.common.constant.RedisKeyConfig;
import com.ltj.blog.common.util.ShiroUtils;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.mq.BlogMQIndexMessage;
import com.ltj.blog.service.BlogService;
import com.ltj.blog.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客后台控制器
 */
@Slf4j
@RestController
public class BlogController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 后台 - 写文章， 创建博客
     */
    @RequiresAuthentication
    @RequiresPermissions("user:create")
    @PostMapping("/blog/create")
    public Result createBlog(@Validated @RequestBody Blog blog) {
        Blog temp = null;
        if (blog.getId() != null) {
            temp = blogService.getById(blog.getId());
        } else {
            temp = new Blog();
            temp.setUserId(ShiroUtils.getProfile().getId());
            temp.setCreateTime(LocalDateTime.now());
        }
        temp.setUpdateTime(LocalDateTime.now());
        BeanUtil.copyProperties(blog, temp, "id", "userId", "createTime", "updateTime");
        log.info("新增的博客是--------> {} ", temp);
        blogService.saveOrUpdate(temp);

        if (temp.getStatus() == 1) {
            // 通知mq增加一条数据
            amqpTemplate.convertAndSend(RabbitMQConfig.ES_EXCHANGE, RabbitMQConfig.ES_BINDING_KEY,
                    new BlogMQIndexMessage(temp.getId(), BlogMQIndexMessage.CREATE_OR_UPDATE));
            // 删除缓存，保证redis缓存更新一致性的解决方法
            redisService.deleteCacheByKey(RedisKeyConfig.BLOG_INFO_CACHE);
            redisService.deleteCacheByKey(RedisKeyConfig.ARCHIVE_INFO_CACHE);
            redisService.deleteCacheByKey(RedisKeyConfig.CATEGORY_BLOG_CACHE);
        }
        return Result.succ(null);
    }

    /**
     * 后台 - 文章管理， 按创建时间排序 分页查询所有博客
     */
    @RequiresPermissions("user:read")
    @GetMapping("/blogList")
    public Result queryBlogListByPage(@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize) {
        Page page = new Page(currentPage, pageSize);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 文章管理 - 搜索， 按创建时间排序 分页条件查询所有博客
     */
    @RequiresPermissions("user:read")
    @GetMapping("/blogList/search")
    public Result queryBlogListByPageAndQueryString(@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam String queryString) {
        Page page = new Page(currentPage, pageSize);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().like("title", queryString).orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 文章管理 - 可见性，修改博客状态
     */
    @RequiresAuthentication
    @RequiresPermissions("user:update")
    @RequestMapping("blog/publish/{id}")
    public Result updateBlogViewStatusById(@PathVariable(name = "id")String id){
        Blog blog = blogService.getById(id);
//        blog.setStatus(blog.getStatus() == 0 ? 1 : 0);
        if (blog.getStatus() == 0) {
            blog.setStatus(1);
            // 通知mq增加一条数据
            amqpTemplate.convertAndSend(RabbitMQConfig.ES_EXCHANGE, RabbitMQConfig.ES_BINDING_KEY,
                    new BlogMQIndexMessage(blog.getId(), BlogMQIndexMessage.CREATE_OR_UPDATE));
        } else {
            blog.setStatus(0);
            // 通知mq删除一条数据
            amqpTemplate.convertAndSend(RabbitMQConfig.ES_EXCHANGE, RabbitMQConfig.ES_BINDING_KEY,
                    new BlogMQIndexMessage(blog.getId(), BlogMQIndexMessage.DELETE));
        }
        blogService.saveOrUpdate(blog);

        // 删除缓存，保证redis缓存更新一致性的解决方法
        redisService.deleteCacheByKey(RedisKeyConfig.BLOG_INFO_CACHE);
        redisService.deleteCacheByKey(RedisKeyConfig.ARCHIVE_INFO_CACHE);
        redisService.deleteCacheByKey(RedisKeyConfig.CATEGORY_BLOG_CACHE);
        return Result.succ(null);
    }

    /**
     * 后台 - 文章管理 - 编辑，查询某个博客详情 （在前端页面的博客详情页通过点击编辑按钮也可以跳转到这里）
     */
    @RequiresPermissions("user:read")
    @GetMapping("/blog/detail/{id}")
    public Result queryBlogDetailById(@PathVariable(name = "id") Long id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已删除！");
        return Result.succ(blog);
    }

    /**
     * 后台 - 文章管理 - 编辑 - 更新，修改某个博客
     */
    @RequiresAuthentication
    @RequiresPermissions("user:update")
    @PostMapping("/blog/update")
    public Result updateBlog(@Validated @RequestBody Blog blog) {
        Blog temp = null;
        if (blog.getId() != null) {
            temp = blogService.getById(blog.getId());
            // 只能编辑自己的文章，这里可以通过角色权限来做
            // Assert.isTrue(temp.getUserId() == ShiroUtils.getProfile().getId(), "没有权限编辑");
        } else {
            temp = new Blog();
            temp.setUserId(ShiroUtils.getProfile().getId());
            temp.setCreateTime(LocalDateTime.now());
            temp.setStatus(0);
        }
        temp.setUpdateTime(LocalDateTime.now());
        BeanUtil.copyProperties(blog, temp, "id", "userId", "createTime", "updateTime");
        blogService.saveOrUpdate(temp);

        // 通知mq增加一条数据
        amqpTemplate.convertAndSend(RabbitMQConfig.ES_EXCHANGE, RabbitMQConfig.ES_BINDING_KEY,
                new BlogMQIndexMessage(blog.getId(), BlogMQIndexMessage.CREATE_OR_UPDATE));

        // 删除缓存，保证redis缓存更新一致性的解决方法
        redisService.deleteCacheByKey(RedisKeyConfig.BLOG_INFO_CACHE);
        redisService.deleteCacheByKey(RedisKeyConfig.ARCHIVE_INFO_CACHE);
        redisService.deleteCacheByKey(RedisKeyConfig.CATEGORY_BLOG_CACHE);
        return Result.succ(null);
    }

    /**
     * 后台 - 文章管理 - 删除， 删除某个博客
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:delete")
    @GetMapping("/blog/delete/{id}")
    public Result deleteBlogById(@PathVariable(name = "id") Long id) {
        if (blogService.removeById(id)) {
            // 删除缓存，保证redis缓存更新一致性的解决方法
            redisService.deleteCacheByKey(RedisKeyConfig.BLOG_INFO_CACHE);
            redisService.deleteCacheByKey(RedisKeyConfig.ARCHIVE_INFO_CACHE);
            redisService.deleteCacheByKey(RedisKeyConfig.CATEGORY_BLOG_CACHE);

            // 本周热议中如果有要删掉
            if (redisService.hasKey("rankBlog:" + id)) {
                redisService.deleteByHashKey("rankBlog:" + id, "blogId:", "blogTitle:");
            }

            // 通知mq删除一条数据
            amqpTemplate.convertAndSend(RabbitMQConfig.ES_EXCHANGE, RabbitMQConfig.ES_BINDING_KEY,
                    new BlogMQIndexMessage(id, BlogMQIndexMessage.DELETE));

            return Result.succ(null);
        } else {
            return Result.fail("删除失败");
        }
    }

    /**
     * 查询所有博客 (后台点击评论管理会调用这个接口)
     */
    @RequiresPermissions("user:read")
    @GetMapping("/blog/all")
    public Result queryBlogAll() {
        List<Blog> list = blogService.list(new QueryWrapper<>());
        return Result.succ(list);
    }

}

