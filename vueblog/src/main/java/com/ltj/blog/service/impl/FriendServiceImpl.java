package com.ltj.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.blog.entity.Friend;
import com.ltj.blog.mapper.FriendMapper;
import com.ltj.blog.service.FriendService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

}
