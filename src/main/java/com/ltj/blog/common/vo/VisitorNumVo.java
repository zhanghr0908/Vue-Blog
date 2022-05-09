package com.ltj.blog.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 传给前端的PV和UV的vo
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class VisitorNumVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int uv;
    private int pv;
}