package com.ltj.blog.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 *  服务之间消息通讯模板
 */
@Data
@AllArgsConstructor
public class BlogMQIndexMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    // 两种type
    public final static String CREATE_OR_UPDATE = "create_or_update";
    public final static String DELETE = "delete";

    private Long blogId;
    private String type;
}
