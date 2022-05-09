package com.ltj.blog.schedule;

import com.ltj.blog.common.constant.RedisKeyConfig;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.service.BlogService;
import com.ltj.blog.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 定时任务
 */
@Component
@EnableScheduling
@EnableAsync
public class RedisSyncScheduleTask {

    @Autowired
    private RedisService redisService;
    @Autowired
    private BlogService blogService;

    Logger logger = LoggerFactory.getLogger(RedisSyncScheduleTask.class);

    /**
     * 从Redis同步博客文章浏览量到数据库
     */
    @Async
    @Scheduled(fixedDelay = 24*60*60*1000)  //间隔24小时
    public void syncBlogViewsToDatabase() {
        logger.info("==========定时任务执行开始==========");
        String redisKey = RedisKeyConfig.BLOG_VIEWS_MAP;
        Map blogViewsMap = redisService.getMapByHash(redisKey);
        Set<Integer> keys = blogViewsMap.keySet();
        for (Integer key : keys) {
            Integer views = (Integer) blogViewsMap.get(key);
            Blog blog = blogService.getById(key);
            blog.setViews(blog.getViews() + views);
            blogService.saveOrUpdate(blog);
        }
        deleteAllCache();
        logger.info("==========定时任务执行完成==========");
    }

    /**
     * 清除所有缓存
     */
    public void deleteAllCache() {
        redisService.deleteAllKeys();
    }

}
