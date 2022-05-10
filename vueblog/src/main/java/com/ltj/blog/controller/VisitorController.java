package com.ltj.blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.common.vo.VisitorNumVo;
import com.ltj.blog.common.constant.RedisKeyConfig;
import com.ltj.blog.entity.Visitor;
import com.ltj.blog.service.RedisService;
import com.ltj.blog.service.VisitorService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 访客前端控制器
 */
@RestController
public class VisitorController {
    @Autowired
    private VisitorService visitorService;
    @Autowired
    private RedisService redisService;

    /**
     * 获取总uv和pv （前端每一次路由发生变化的时候都会执行该方法）
     */
    @GetMapping("/visitornum")
    public Result getPvAndUv() {
        if (redisService.hasKey(RedisKeyConfig.PV_UV)) {
            return Result.succ(redisService.getValueByHashKey(RedisKeyConfig.PV_UV, RedisKeyConfig.All));
        }
        int uv = visitorService.list().size();
        int pv = visitorService.getPv();
        VisitorNumVo visitorNumVo = new VisitorNumVo(uv,pv);

        redisService.saveKVToHash(RedisKeyConfig.PV_UV, RedisKeyConfig.All, visitorNumVo);
        return Result.succ(visitorNumVo);
    }

    /**
     * 后台 - 访客统计， 分页查询所有游客
     */
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/visitorList")
    public Result queryVisitorListByPage(@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize) {
        Page page = new Page(currentPage, pageSize);
        IPage pageData = visitorService.page(page, new QueryWrapper<Visitor>().orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 访客统计 - 搜索， 根据访问时间 分页查询所有游客
     */
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/visitor/part")
    public Result queryVisitorListByTime(@RequestParam(defaultValue = "") String time,@RequestParam(defaultValue = "1") Integer currentPage,@RequestParam(defaultValue = "10") Integer pageSize) {
        String [] endStartTime = time.split(",");
        if (endStartTime.length != 2){
            return Result.fail("时间设置错误");
        }
        Page page = new Page(currentPage, pageSize);
        IPage pageData = visitorService.page(page, new QueryWrapper<Visitor>().le("last_time",endStartTime[1]).ge("last_time",endStartTime[0]).orderByDesc("create_time"));
        return Result.succ(pageData);
    }

    /**
     * 后台 - 访客统计 - 删除， 删除某个游客
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:delete")
    @GetMapping("/visitor/delete/{id}")
    public Result deleteVisitorById(@PathVariable(name = "id") Long id) {
        if (visitorService.removeById(id)) {
            return Result.succ(null);
        } else {
            return Result.fail("删除失败");
        }
    }

    /**
     * 查询所有游客
     */
//    @RequiresAuthentication
//    @RequiresPermissions("user:read")
//    @RequestMapping("/visitor")
//    public Result getAllVisiorList(){
//        List<Visitor> list = visitorService.list(new QueryWrapper<>());
//        return Result.succ(list);
//    }

    /**
     * 修改某个游客信息
     */
//    @RequiresAuthentication
//    @PostMapping("/visitor/update")
//    public Result updateVisitLog(@Validated @RequestBody Visitor visitor){
//        if(visitor ==null){
//            return Result.fail("不能为空");
//        }
//        else{
//            if(visitor.getId()==null){
//                visitor.setLastTime(LocalDateTime.now());
//                visitor.setCreateTime(LocalDateTime.now());
//            }
//            visitorService.saveOrUpdate(visitor);
//        }
//        return Result.succ(null);
//    }
}
