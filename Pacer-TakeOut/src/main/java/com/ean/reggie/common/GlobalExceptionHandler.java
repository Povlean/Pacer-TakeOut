package com.ean.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {Controller.class , RestController.class})
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        // Duplicate entry 'zhangsan' for key 'employee.idx_username'
        // 添加提示信息
        if(ex.getMessage().contains("Duplicate entry")){
            String[] splits = ex.getMessage().split(" ");
            String username = splits[2];
            return Result.error(username + "用户已存在");
        }
        return Result.error("添加失败");
    }

    /**
    * @description:用于抛出关联异常
    * @author:Povlean
    * @date:2022/11/6 20:30
    * @param:* @param ex
    * @return:* @return Result<String>
    */
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){

        log.error(ex.getMessage());

        return Result.error(ex.getMessage());
    }
}
