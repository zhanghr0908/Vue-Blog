package com.ltj.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ltj.blog.entity.Tag;

import java.util.List;

/**
 * 服务类
 */
public interface TagService extends IService<Tag> {

    List<Tag> queryHotTagsByLimit(int limit);

    List<Tag> queryTagsList();
}
