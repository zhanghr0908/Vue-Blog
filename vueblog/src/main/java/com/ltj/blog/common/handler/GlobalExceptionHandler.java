package com.ltj.blog.common.handler;

import com.ltj.blog.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕捉shiro的授权异常处理
     */
    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseBody
    public Result jsonExceptionHandler(HttpServletRequest req, Exception e) {
        log.error("权限不足:-------------->{}", e.getMessage());
        return Result.fail(403, "你的级别还不够高,加油吧！少年。",null);
    }

    /**
     * 捕捉shiro的认证异常处理
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Result handler(ShiroException e) {
        log.error("未登录访问:-------------->{}", e.getMessage());
        return Result.fail(401, e.getMessage(), null);
    }

    /**
     * Assert的异常处理
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        log.error("Assert异常:-------------->{}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 实体校验异常处理
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        log.error("实体校验异常:-------------->{}", e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(objectError.getDefaultMessage());
    }

    /**
     * 运行时异常处理
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e) {
        log.error("运行时异常:-------------->{}", e.getMessage());
        return Result.fail(e.getMessage());
    }
}
