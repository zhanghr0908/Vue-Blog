package com.ltj.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ltj.blog.entity.SiteSetting;
import com.ltj.blog.mapper.SiteSettingMapper;
import com.ltj.blog.service.SiteSettingService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 */
@Service
public class SiteSettingServiceImpl extends ServiceImpl<SiteSettingMapper, SiteSetting> implements SiteSettingService {

}
