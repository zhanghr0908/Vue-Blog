package com.ltj.blog.common.util;

import com.ltj.blog.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;

public class ShiroUtils {

    public static AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

}
