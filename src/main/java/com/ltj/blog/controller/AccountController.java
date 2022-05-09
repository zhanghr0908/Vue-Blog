package com.ltj.blog.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ltj.blog.common.util.JwtUtils;
import com.ltj.blog.common.vo.LoginDto;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.entity.Blog;
import com.ltj.blog.entity.User;
import com.ltj.blog.es.BlogDocumentService;
import com.ltj.blog.service.BlogService;
import com.ltj.blog.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 登录登出控制器
 */
@RestController
public class AccountController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogDocumentService blogDocumentService;

    /**
     * 登录请求处理
     */
    @CrossOrigin
    @PostMapping("/login")
    public Result doLogin(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) {
        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        Assert.notNull(user, "用户名不存在");
        if (!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
            return Result.fail("密码不正确");
        }
        if(user.getStatus() == 0){
            return Result.fail("账户已被禁用，请联系管理员");
        }
        String jwt = jwtUtils.generateToken(user.getId(),user.getUsername());
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        // 用户信息也可以通过另外一个接口获取封装再返回，这里直接返回
        return Result.succ(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("avatar", user.getAvatar())
                .put("email", user.getEmail())
                .put("role", user.getRole())
                .map()
        );
    }

    /**
     * 登出请求处理 （需要登录的权限才能退出）
     */
    @RequiresAuthentication
    @GetMapping("/logout")
    public Result doLogout() {
        SecurityUtils.getSubject().logout();
        return Result.succ("退出成功");
    }


    /**
     *  管理员手动初始化ES中数据
     */
    @RequiresRoles(value = {"role_root", "role_admin", "role_user"}, logical = Logical.OR)//用来判断是否拥有角色  具有其中一角色才能访问
    @RequiresAuthentication
    @PostMapping("/initesdata")
    public Result initData() {
        // 一次搜索10000条
        int size = 10000;
        Page page = new Page();
        page.setSize(size);
        long total = 0;

        for(int i = 1; i < 1000; i++) {
            page.setCurrent(i);
            IPage pageData = blogService.page(page, new QueryWrapper<Blog>().eq("status", 1));
            int num = blogDocumentService.initData(pageData.getRecords());
            total += num;

            // 当一页查不出10000条数据时，说明是最后一页了
            if (pageData.getRecords().size() < size) {
                break;
            }
        }

        return Result.succ("ES索引初始化成功，共 " + total + " 条记录");
    }

}
