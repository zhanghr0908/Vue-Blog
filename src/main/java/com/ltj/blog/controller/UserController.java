package com.ltj.blog.controller;


import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.common.vo.UserInfoVo;
import com.ltj.blog.entity.User;
import com.ltj.blog.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户前端控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 后台 - 用户管理， 分页查询用户
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:read")
    @GetMapping("/list")
    public Result queryUserListByPage(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize) {
        List<UserInfoVo> userInfoList = userService.getUserInfoList();
        int size = userInfoList.size();
        Page page = new Page(currentPage,pageSize);
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
            pageList.add(userInfoList.get(curIdx + i));
        }
        page.setTotal(userInfoList.size()).setRecords(pageList);
        return Result.succ(page);
    }

    /**
     * 后台 - 用户管理 - 添加用户， 创建用户
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:create")
    @PostMapping("/create")
    public Result saveUser(@Validated @RequestBody User user){
        if(user==null){
            return Result.fail("不能为空");
        }
        else{
            if(user.getRole().contains("role_root")){
                return Result.fail("禁止设置root用户");
            }
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setPassword(SecureUtil.md5(user.getPassword()));
            userService.saveOrUpdate(user);
            return Result.succ(null);
        }
    }

    /**
     * 后台 - 用户管理 - 是否禁用， 修改用户的状态
     */
    @RequiresRoles("role_root")
    @RequiresPermissions("user:update")
    @RequestMapping("/publish/{id}")
    public Result updateUserStatusById(@PathVariable(name = "id")Long id){
        User user = userService.getById(id);
        if(user.getRole().equals("role_root")){
            return Result.fail("禁止禁用此用户");
        }
        user.setStatus(user.getStatus() == 0 ? 1 : 0);
        userService.saveOrUpdate(user);
        return Result.succ(null);
    }

    /**
     *  后台 - 用户管理 - 编辑， 修改用户信息
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:update")
    @PostMapping("/update")
    public Result updateUser(@Validated @RequestBody User user){
        if(user==null){
            return Result.fail("不能为空");
        }
        else{
            user.setUpdateTime(LocalDateTime.now());
            User subUser = userService.getById(user.getId());
            if(subUser.getRole().equals("role_root")){
                return Result.fail("禁止修改此用户");
            }
            // 未修改密码赋值原密码，因为这里有@Validated注解，密码不能为空，所有这个逻辑可以没有
            if(user.getPassword().equals("")){
                user.setPassword(subUser.getPassword());
            }
            else{
                //存储在数据中的密码为md5加密后的
                user.setPassword(SecureUtil.md5(user.getPassword()));
            }
            userService.saveOrUpdate(user);
            return Result.succ(null);
        }
    }

    /**
     * 后台 - 用户管理 - 删除， 删除用户
     */
    @RequiresRoles("role_root")
    @RequiresAuthentication
    @RequiresPermissions("user:delete")
    @GetMapping("/delete/{id}")
    public Result deleteUserById(@PathVariable(name = "id") Long id) {
        User user = userService.getById(id);
        if(user.getRole().equals("role_root")){
            return Result.fail("禁止删除此用户");
        }
        if (userService.removeById(id)) {
            return Result.succ(null);
        } else {
            return Result.fail("删除失败");
        }
    }

}
