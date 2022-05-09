package com.ltj.blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.annotation.VisitLogger;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.common.constant.RedisKeyConfig;
import com.ltj.blog.entity.Friend;
import com.ltj.blog.service.FriendService;
import com.ltj.blog.service.RedisService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 友链前端控制器
 */
@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private RedisService redisService;

    /**
     * 导航栏 - 友链， 查询所有公开的友链，这些友链在那个友链博客中
     */
    @GetMapping("/friend/all")
    public Result queryFriendList(){
        if (redisService.hasHashKey(RedisKeyConfig.FRIEND_INFO_CACHE, RedisKeyConfig.All)) {
            return Result.succ(redisService.getValueByHashKey(RedisKeyConfig.FRIEND_INFO_CACHE, RedisKeyConfig.All));
        }
        List<Friend> list = friendService.list(new QueryWrapper<Friend>().eq("is_published", 1));
        redisService.saveKVToHash(RedisKeyConfig.FRIEND_INFO_CACHE, RedisKeyConfig.All,list);
        return Result.succ(list);
    }


    /**
     * 导航栏 - 友链 - 点击具体友链， 友链浏览次数加一
     */
    @VisitLogger(behavior = "点击友链")
    @RequestMapping("/friend/onclick")
    public Result increaseFriendWatchNum(@RequestParam(name = "")String nickname ){
        if(nickname.equals("")){
            return Result.fail("访问出错");
        }
        Friend friend = friendService.getOne(new QueryWrapper<Friend>().eq("nickname",nickname));
        friend.setViews(friend.getViews()+1);
        friendService.saveOrUpdate(friend);
        return Result.succ(null);
    }

    /**
     * 后台 - 友链管理， 分页查询所有友链
     */
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/friendList")
    public Result queryFriendListByPage(@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize) {
        Page page = new Page(currentPage, pageSize);
        IPage pageData = friendService.page(page, new QueryWrapper<Friend>().orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 友链管理 - 添加友链， 增加友链
     */
    @RequiresAuthentication
    @RequiresPermissions("user:create")
    @PostMapping("/friend/create")
    public Result saveFriend(@Validated @RequestBody Friend friend){
        if (friend == null){
            return Result.fail("不能为空");
        } else {
            if (friend.getId() == null){
                friend.setCreateTime(LocalDateTime.now());
            }
            friendService.saveOrUpdate(friend);
            redisService.deleteCacheByKey(RedisKeyConfig.FRIEND_INFO_CACHE);
        }
        return Result.succ(null);
    }

    /**
     * 后台 - 友链管理 - 是否公开， 修改友链的状态
     */
    @RequiresAuthentication
    @RequiresPermissions("user:update")
    @RequestMapping("friend/publish/{id}")
    public Result updateFriendViewStatusById(@PathVariable(name = "id")Long id){
        Friend friend = friendService.getById(id);
        friend.setIsPublished(!friend.getIsPublished());
        friendService.saveOrUpdate(friend);

        redisService.deleteCacheByKey(RedisKeyConfig.FRIEND_INFO_CACHE);
        return Result.succ(null);
    }

    /**
     * 后台 - 友链管理 - 编辑， 修改友链
     */
    @RequiresAuthentication
    @RequiresPermissions("user:update")
    @PostMapping("/friend/update")
    public Result updateFriend(@Validated @RequestBody Friend friend){
        if (friend == null){
            return Result.fail("不能为空");
        } else {
            if (friend.getId() == null){
                friend.setCreateTime(LocalDateTime.now());
            }
            friendService.saveOrUpdate(friend);
            redisService.deleteCacheByKey(RedisKeyConfig.FRIEND_INFO_CACHE);
        }
        return Result.succ(null);
    }

    /**
     * 后台 - 友链管理 - 删除 删除友链
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:delete")
    @GetMapping("/friend/delete/{id}")
    public Result deleteFriendById(@PathVariable(name = "id") Long id) {
        if (friendService.removeById(id)) {
            redisService.deleteCacheByKey(RedisKeyConfig.FRIEND_INFO_CACHE);
            return Result.succ(null);
        } else {
            return Result.fail("删除失败");
        }
    }
}
