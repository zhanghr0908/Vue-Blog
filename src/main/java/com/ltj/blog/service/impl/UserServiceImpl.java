package com.ltj.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.blog.common.vo.UserInfoVo;
import com.ltj.blog.entity.User;
import com.ltj.blog.mapper.UserMapper;
import com.ltj.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询所有用户（只含有部分信息）
     */
    @Override
    public List<UserInfoVo> getUserInfoList(){
        return userMapper.getUserInfo();
    }
}
