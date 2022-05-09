package com.ltj.blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.entity.VisitLog;
import com.ltj.blog.service.VisitLogService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 访客记录前端控制器
 */
@RestController
public class VisitLogController {
    @Autowired
    private VisitLogService visitLogService;

    /**
     * 后台 - 访问日志， 分页查询所有游客
     */
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/visitLogList")
    public Result queryVisitorListByPage(@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize) {
        Page page = new Page(currentPage, pageSize);
        IPage pageData = visitLogService.page(page, new QueryWrapper<VisitLog>().orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 访问日志 - 搜索， 根据 uuid 访问时间范围 分页查询所有游客日志
     */
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/visitLog/part")
    public Result getVisitorList(@RequestParam(defaultValue = "") String uuid,@RequestParam(defaultValue = "") String time,@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize) {
        String [] endStartTime = time.split(",");
        if (time.equals("") && uuid.equals("")){
            Page page = new Page(currentPage, pageSize);
            IPage pageData = visitLogService.page(page, new QueryWrapper<VisitLog>().orderByDesc("create_time"));
            return Result.succ(pageData);
        }
        if (time.equals("")){
            Page page = new Page(currentPage, pageSize);
            IPage pageData = visitLogService.page(page, new QueryWrapper<VisitLog>().eq("uuid",uuid ).orderByDesc("create_time"));
            return Result.succ(pageData);
        } else if (uuid.equals("")){
            Page page = new Page(currentPage, pageSize);
            IPage pageData = visitLogService.page(page, new QueryWrapper<VisitLog>().le("create_time",endStartTime[1]).ge("create_time",endStartTime[0]).orderByDesc("create_time"));
            return Result.succ(pageData);
        } else {
            Page page = new Page(currentPage, pageSize);
            IPage pageData = visitLogService.page(page, new QueryWrapper<VisitLog>().eq("uuid",uuid ).le("create_time",endStartTime[1]).ge("create_time",endStartTime[0]).orderByDesc("create_time"));
            return Result.succ(pageData);
        }

    }

    /**
     * 后台 - 访问日志 - 删除， 删除某个浏览日志
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:delete")
    @GetMapping("/visitLog/delete/{id}")
    public Result deleteVisitLogById(@PathVariable(name = "id") Long id) {
        if (visitLogService.removeById(id)) {
            return Result.succ(null);
        } else {
            return Result.fail("删除失败");
        }
    }

    /**
     * 查询所有游客浏览日志
     */
//    @RequiresAuthentication
//    @RequiresPermissions("user:read")
//    @RequestMapping("/visitLog/all")
//    public Result getFriendList(){
//        List<VisitLog> list = visitLogService.list(new QueryWrapper<>());
//        return Result.succ(list);
//    }

    /**
     * 修改游客访问日志
     */
//    @RequiresAuthentication
//    @PostMapping("/visitLog/update")
//    public Result updateVisitLog(@Validated @RequestBody VisitLog visitLog){
//        if(visitLog ==null){
//            return Result.fail("不能为空");
//        }
//        else{
//            if(visitLog.getId()==null){
//                visitLog.setCreateTime(LocalDateTime.now());
//            }
//            visitLogService.saveOrUpdate(visitLog);
//        }
//        return Result.succ(null);
//    }

}
