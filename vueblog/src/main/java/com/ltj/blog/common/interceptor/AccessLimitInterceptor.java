package com.ltj.blog.common.interceptor;

import cn.hutool.json.JSONObject;
import com.ltj.blog.common.annotation.AccessLimit;
import com.ltj.blog.common.util.IpAddressUtils;
import com.ltj.blog.common.vo.Result;
import com.ltj.blog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 自定义拦截器：
 * 控制接口访问频率
 */
@Component
public class AccessLimitInterceptor  implements HandlerInterceptor  {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断请求的接口路径是否为HandlerMethod（controller方法）
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            //方法上没有访问控制的注解，直接通过
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
//            String ip = request.getHeader("x-forwarded-for");
            String ip = IpAddressUtils.getIpAddr(request);
            String method = request.getMethod();
            String requestURI = request.getRequestURI();
            String redisKey = ip + ":" + method + ":" + requestURI;
            Integer count = (Integer) redisService.getObjectByValue(redisKey);
            if (count == null) {
                //在规定周期内第一次访问，存入redis
                redisService.saveObjectToValue(redisKey, 1);
                redisService.expire(redisKey, seconds);
            } else {
                if (count >= maxCount) {
                    //超出访问限制次数
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    Result result = Result.fail(403, accessLimit.msg(),null);
                    out.write(String.valueOf(new JSONObject(result)));
                    out.flush();
                    out.close();
                    return false;
                } else {
                    //没超出访问限制次数
                    redisService.incrementByKey(redisKey, 1);
                }
            }
        }
        return true;
    }
}
