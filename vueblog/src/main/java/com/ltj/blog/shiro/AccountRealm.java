package com.ltj.blog.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.ltj.blog.entity.User;
import com.ltj.blog.service.UserService;
import com.ltj.blog.common.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 登录 认证和授权
 */
@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 角色授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("执行doGetAuthorizationInfo方法授权开始");
//        String username = JwtUtil.getUsername(principalCollection.toString());
//        log.info("用户: {}", principals.toString());
//        log.info("登录的用户:" + username);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        AccountProfile accountProfile = (AccountProfile)principals.getPrimaryPrincipal();
        String[] roles = accountProfile.getRole().split(",");
        log.info("当前用户角色: {}", roles);
        for(String role : roles){
            info.addRole(role);
            // 进行授权
            if(role.equals("role_root")){
                info.addStringPermission("user:create");
                info.addStringPermission("user:update");
                info.addStringPermission("user:read");
                info.addStringPermission("user:delete");
            }
            else if( role.equals("role_admin")){
                info.addStringPermission("user:create");
                info.addStringPermission("user:update");
                info.addStringPermission("user:read");
            }
            else if( role.equals("role_user")){
                info.addStringPermission("user:create");
                info.addStringPermission("user:read");
            }
            else if(role.equals("role_guest")){
                info.addStringPermission("user:read");
            }
        }
        log.info("执行doGetAuthorizationInfo方法授权结束");
        return info;
    }

    /**
     * 身份认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;
//        log.info("jwt----------------->{}", jwtToken);
        String userId = (String) jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).get("userId");
        String username = (String) jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).get("username");
        User user = userService.getById(Long.parseLong(userId));
        if (user == null) {
            throw new UnknownAccountException("账户不存在！");
        }
        if (user.getStatus() == -1) {
            throw new LockedAccountException("账户已被锁定！");
        }
        if(!user.getUsername().equals(username)){
            throw new UnknownAccountException("userId与username不一致");
        }
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
//        log.info("profile----------------->{}", profile.toString());
        return new SimpleAuthenticationInfo(profile, jwtToken.getCredentials(), getName());
    }
}

