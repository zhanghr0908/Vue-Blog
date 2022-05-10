package com.ltj.blog.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 自定义jwt的token
 */
public class JwtToken implements AuthenticationToken {

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}

