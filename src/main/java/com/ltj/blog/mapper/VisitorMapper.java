package com.ltj.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ltj.blog.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
@Mapper
@Repository
public interface VisitorMapper extends BaseMapper<Visitor> {
    /**
     * 查询uuid是否已经存在
     */
    int hasUUID(String uuid);

    /**
     * 通过uuid找到访客
     */
    @Select("select id, uuid, ip, ip_source, os, browser, create_time, last_time, pv,user_agent from visitor  where uuid=#{uuid}")
    Visitor selectByUuid(@Param("uuid")String uuid);

    /**
     * 计算pv
     */
    @Select("select COALESCE(sum(pv),0) from visitor")
    int getPv();
}
