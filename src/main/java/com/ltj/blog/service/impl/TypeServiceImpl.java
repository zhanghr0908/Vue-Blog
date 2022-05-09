package com.ltj.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.blog.entity.Type;
import com.ltj.blog.mapper.TypeMapper;
import com.ltj.blog.service.TypeService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

}
