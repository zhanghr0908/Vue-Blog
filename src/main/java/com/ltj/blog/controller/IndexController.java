package com.ltj.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.annotation.VisitLogger;
import com.ltj.blog.common.constant.RedisKeyConfig;
import com.ltj.blog.common.vo.BlogInfoVo;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.entity.Tag;
import com.ltj.blog.es.BlogDocumentService;
import com.ltj.blog.service.BlogService;
import com.ltj.blog.service.RedisService;
import com.ltj.blog.service.TagService;
import com.ltj.blog.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 博客前端控制器
 */
@RestController
public class IndexController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BlogDocumentService blogDocumentService;

    /**
     * 首页 - 按置顶、创建时间排序 分页查询公开的博客
     */
    @VisitLogger(behavior = "访问页面",content = "首页")
    @GetMapping("/blogs")
    public Result queryBlogByPage(@RequestParam Integer currentPage) {
        //有缓存直接返回
        if(redisService.hasHashKey(RedisKeyConfig.BLOG_INFO_CACHE,currentPage)){
            return Result.succ(redisService.getValueByHashKey(RedisKeyConfig.BLOG_INFO_CACHE,currentPage));
        }
        Page page = new Page(currentPage, 5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().eq("status", 1).orderByDesc("create_time"));
        // 把博客信息存入缓存
        redisService.saveKVToHash(RedisKeyConfig.BLOG_INFO_CACHE, currentPage,pageData);
        return Result.succ(pageData);
    }

    /**
     * 首页 - 查询某个公开博客详情
     */
    @VisitLogger(behavior = "查看博客")
    @GetMapping("/blog/{id}")
    public Result queryBlogDetailById(@PathVariable(name = "id") Integer id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客已删除！");
        if (blog.getStatus()!=1){
            return Result.fail("你没有权限查阅此博客");
        }
        /*
            查看完文章之后，新增阅读数。
            查看完文章之后，本应该直接返回数据，这时候做一个阅读数的更新操作，更新时加写锁，阻塞其它的读操作，性能会比较低
            更新增加了此次接口的耗时，一旦更新出问题，它不能影响查看文章的操作
            采用线程池技术增加阅读数，把更新操作扔到线程池中去执行
         */
//        threadService.updateViewCount(blog);


        // 在redis中做博客浏览量的增加
        if (redisService.getMapByHash(RedisKeyConfig.BLOG_VIEWS_MAP).containsKey(id)) {
            redisService.incrementByHashKey(RedisKeyConfig.BLOG_VIEWS_MAP, id, 1);
        } else {
            redisService.saveKVToHash(RedisKeyConfig.BLOG_VIEWS_MAP, id, 1);
        }
        return Result.succ(blog);
    }

    /**
     * 首页 - 最热博客
     */
    @GetMapping("/blog/hot")
    public Result queryHotBlogByLimit() {
        int limit = 5;
        List<Blog> blogList = blogService.queryHotBlogByLimit(limit);
        return Result.succ(blogList);
    }

    /**
     * 首页 - 本周热议博客
     */
    @GetMapping("/blog/weekhot")
    public Result queryWeekHotBlogByLimit() {
        int limit = 3;
        if (redisService.hasKey("weekRank:")) {
            List<Map<String, Object>> hotBlogList = redisService.getWeekHotBlogByLimit(limit);
            return Result.succ(hotBlogList);
        }
        List<Map<String, Object>> blogList = blogService.queryWeekHotBlogByLimit(limit);
        return Result.succ(blogList);
    }

    /**
     * 首页 - 最新博客
     */
    @GetMapping("/blog/new")
    public Result queryNewBlogByLimit() {
        int limit = 5;
        List<Blog> blogList = blogService.queryNewBlogByLimit(limit);
        return Result.succ(blogList);
    }

    /**
     * 首页 - 最热标签
     */
    @GetMapping("/tags/hot")
    public Result queryHotTagsByLimit() {
        int limit = 5;
        List<Tag> tagList = tagService.queryHotTagsByLimit(limit);
        return Result.succ(tagList);
    }

    /**
     * 导航栏 - 分类，按创建时间排序 分类 分页查询公开的博客简要信息列表
     */
    @VisitLogger(behavior = "查看分类")
    @GetMapping("/blogsByType")
    public Result blogsByType(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam String typeName) {
        // 查询缓存
        if (redisService.hasHashKey(RedisKeyConfig.CATEGORY_BLOG_CACHE, typeName+currentPage)) {
            return Result.succ(redisService.getValueByHashKey(RedisKeyConfig.CATEGORY_BLOG_CACHE, typeName+currentPage));
        }

        List<BlogInfoVo> blogInfoVoList = blogService.queryBlogInfoListByCategoryName(typeName);
        int pageSize = 10;
        Page page = new Page();
        int size = blogInfoVoList.size();
        if (size == 0) {
            return Result.fail("该分类下没有文章");
        }
        if (pageSize > size) {
            pageSize = size;
        }
        // 求出最大页数，防止currentPage越界
        int maxPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;
        if (currentPage > maxPage) {
            currentPage = maxPage;
        }
        // 当前页第一条数据的下标
        int curIdx = currentPage > 1 ? (currentPage - 1) * pageSize : 0;
        List pageList = new ArrayList();
        // 将当前页的数据放进pageList
        for (int i = 0; i < pageSize && curIdx + i < size; i++) {
            pageList.add(blogInfoVoList.get(curIdx + i));
        }
        page.setCurrent(currentPage).setSize(pageSize).setTotal(blogInfoVoList.size()).setRecords(pageList);

        // 存入缓存
        redisService.saveKVToHash(RedisKeyConfig.CATEGORY_BLOG_CACHE, typeName+currentPage, page);
        return Result.succ(page);
    }

    /**
     * 导航栏 - 归档，按创建时间排序 分页查询所有博客
     */
    @VisitLogger(behavior = "访问页面",content = "归档")
    @GetMapping("/blog/archives")
    public Result getBlogsArchives(@RequestParam(defaultValue = "1") Integer currentPage) {
        // 查询缓存
        if(redisService.hasHashKey(RedisKeyConfig.ARCHIVE_INFO_CACHE,currentPage)){
            return   Result.succ(redisService.getValueByHashKey(RedisKeyConfig.ARCHIVE_INFO_CACHE,currentPage));
        }
        Integer pageSize = 15;
        Page page = new Page(currentPage, pageSize);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().eq("status", 1).orderByDesc("create_time"));
        // 存入缓存
        redisService.saveKVToHash(RedisKeyConfig.ARCHIVE_INFO_CACHE, currentPage,pageData);
        return Result.succ(pageData);
    }

    /**
     * 导航栏 - 友链，查询友链的博客
     */
    @VisitLogger(behavior = "访问页面",content = "友链")
    @GetMapping("/friends")
    public Result friends() {
        // 查询缓存
        if (redisService.hasHashKey(RedisKeyConfig.BLOG_INFO_CACHE,RedisKeyConfig.FRIEND_BLOG_CACHE)) {
            return Result.succ(redisService.getValueByHashKey(RedisKeyConfig.BLOG_INFO_CACHE,RedisKeyConfig.FRIEND_BLOG_CACHE));
        }
        List<Blog> list = blogService.lambdaQuery().eq(Blog::getTitle, "友情链接").list();
        // 存入缓存
        redisService.saveKVToHash(RedisKeyConfig.BLOG_INFO_CACHE, RedisKeyConfig.FRIEND_BLOG_CACHE, list.get(0));
        return Result.succ(list.get(0));
    }

    /**
     * 导航栏 - 关于我，查询关于我的博客
     */
    @VisitLogger(behavior = "访问页面",content = "关于我")
    @GetMapping("/about")
    public Result aboutMe() {
        if (redisService.hasHashKey(RedisKeyConfig.BLOG_INFO_CACHE,RedisKeyConfig.ABOUT_INFO_CACHE)) {
            return Result.succ(redisService.getValueByHashKey(RedisKeyConfig.BLOG_INFO_CACHE,RedisKeyConfig.ABOUT_INFO_CACHE));
        }
        List<Blog> list = blogService.lambdaQuery().eq(Blog::getTitle, "关于我！！").list();
        redisService.saveKVToHash(RedisKeyConfig.BLOG_INFO_CACHE,RedisKeyConfig.ABOUT_INFO_CACHE, list.get(0));
        return Result.succ(list.get(0));
    }

    /**
     * 导航栏 - 搜索框，根据内容搜索公开博客
     */
//    @VisitLogger(behavior = "搜索博客")
//    @GetMapping("/search")
//    public Result search(@RequestParam String queryString) {
//        List<Blog> list = blogService.list(new QueryWrapper<Blog>().like("content", queryString).eq("status", 1).orderByDesc("create_time"));
//        return Result.succ(list);
//    }

    /**
     * 导航栏 - 搜索框，根据内容搜索公开博客  (通过ES和MQ来实现，如下，但是部署失败)
     */
    @VisitLogger(behavior = "搜索博客")
    @GetMapping("/search")
    public Result doSearch(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam String queryString) {
        Integer pageSize = 15;
        Page page = new Page(currentPage, pageSize);
        IPage pageData = blogDocumentService.searchData(page, queryString);
        return Result.succ(pageData);
    }
}
