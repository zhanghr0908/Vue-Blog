package com.ltj.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.blog.entity.Tag;
import com.ltj.blog.mapper.TagMapper;
import com.ltj.blog.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 服务实现类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Override
    public List<Tag> queryHotTagsByLimit(int limit) {
        List<Long> hotTagsIds = tagMapper.selectHotTagsIdsByLimit(limit);
        if (CollectionUtils.isEmpty(hotTagsIds)) {
            return Collections.emptyList();
        }
        return tagMapper.selectHotTagsByHotTagsId(hotTagsIds);
    }

    @Override
    public List<Tag> queryTagsList() {
        List<Tag> tagList = tagMapper.selectList(new QueryWrapper<>());
        return tagList;
    }
}
