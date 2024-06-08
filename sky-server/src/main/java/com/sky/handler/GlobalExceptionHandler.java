package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        //错误信息
        // Duplicate entry 'liyunlong' for key 'employee.idx_username'
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String s = split[2]+ MessageConstant.ALREADY_EXISIS;    //常量:已存在
            return Result.error(s);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR); //常量：未知错误
        }
    }

}
