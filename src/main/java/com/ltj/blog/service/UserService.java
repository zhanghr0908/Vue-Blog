package com.ltj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ltj.blog.common.vo.UserInfoVo;
import com.ltj.blog.entity.User;

import java.util.List;

/**
 * 服务类
 */
public interface UserService extends IService<User> {
    /**
     * 查询所有用户（只含有部分信息）
     */
    List<UserInfoVo> getUserInfoList();

}
