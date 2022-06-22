package com.ltj.blog.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.blog.common.vo.BlogInfoVo;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.entity.Comment;
import com.ltj.blog.mapper.BlogMapper;
import com.ltj.blog.mapper.CommentMapper;
import com.ltj.blog.service.BlogService;
import com.ltj.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 服务实现类
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 通过分类名查找属于该分类的博客list
     */
    @Override
    public List<BlogInfoVo> queryBlogInfoListByCategoryName(String categoryName) {
        return blogMapper.queryBlogByTypeName(categoryName);
    }

    @Override
    public List<Blog> queryHotBlogByLimit(int limit) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Blog::getViews);
        queryWrapper.eq(Blog::getStatus, 1);
        queryWrapper.select(Blog::getId, Blog::getTitle);
        queryWrapper.last("limit " + limit);
        return blogMapper.selectList(queryWrapper);
    }

    @Override
    public List<Blog> queryNewBlogByLimit(int limit) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Blog::getCreateTime);
        queryWrapper.eq(Blog::getStatus, 1);
        queryWrapper.select(Blog::getId, Blog::getTitle);
        queryWrapper.last("limit " + limit);
        return blogMapper.selectList(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryWeekHotBlogByLimit(int limit) {
        // 七天内发表的博客
        List<Blog> blogList = blogMapper.selectList(new QueryWrapper<Blog>().ge("create_time", DateUtil.lastWeek().toJdkDate()).eq("status", 1));
        for(Blog blog : blogList) {
            String key = "dayRank:" + DateUtil.format(blog.getCreateTime(), DatePattern.PURE_DATE_PATTERN);
//            System.out.println("key========" + key);
            // 设置有效期,7天后自动过期
            int between = (int) DateUtil.between(new Date(), DateUtil.date(blog.getCreateTime()), DateUnit.DAY);
            int expireTime = (7 - between) * 24 * 60 * 60;
            // 该博客评论数
            Double commentNum = commentMapper.queryCountNum(blog.getId());
            // 缓存博客到set中，博客的评论数量作为排行标准
            redisService.saveValueToZSet(key, blog.getId(), commentNum);
            redisService.expire(key, expireTime);
            // 缓存博客基本信息
            hashCacheBlogIdAndTitle(blog, expireTime);
        }
        // 统计七天的博客集合并集
        zUnionAndStoreLastWeekForWeekRank();
        // 取出前limit条
        return redisService.getWeekHotBlogByLimit(limit);
    }

    // 缓存博客基本信息
    public void hashCacheBlogIdAndTitle(Blog blog, int expireTime) {
        boolean isExist = redisService.hasKey("rankBlog:" + blog.getId());
        if (!isExist) {
            redisService.saveKVToHash("rankBlog:" + blog.getId(), "blogId:", blog.getId());
            redisService.expire("blogId", expireTime);
            redisService.saveKVToHash("rankBlog:" + blog.getId(), "blogTitle:", blog.getTitle());
            redisService.expire("blogTitle", expireTime);
        }
    }

    // 统计七天的博客集合并集
    public void zUnionAndStoreLastWeekForWeekRank() {
        List<String> keys = new ArrayList<>();
        String currentKey = "dayRank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        for(int i = -7; i < 0; i++) {
            Date date = DateUtil.offsetDay(new Date(), i);
            keys.add("dayRank:" + DateUtil.format(date, DatePattern.PURE_DATE_PATTERN));
        }
        String destKey = "weekRank:";
        redisService.zUnionAndStore(currentKey, keys, destKey);
    }

    // 当添加或删除评论时，需要在缓存中自增或自减
    @Override
    public void incrCommentCountAndUnionForWeekRank(long blogId, boolean isIncr) {
        Blog blog = blogMapper.selectById(blogId);
        int between = (int) DateUtil.between(new Date(), DateUtil.date(blog.getCreateTime()), DateUnit.DAY);
        int expireTime = (7 - between) * 24 * 60 * 60;
        if (expireTime > 0) {
            String currentKey = "dayRank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
            redisService.expire(currentKey, expireTime);
            redisService.zIncrementScore(currentKey, blog.getId(), isIncr ? 1 : -1);
            // 缓存博客基本信息
            hashCacheBlogIdAndTitle(blog, expireTime);
            // 重新做并集
            zUnionAndStoreLastWeekForWeekRank();
        }
    }
}
