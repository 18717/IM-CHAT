package com.li2n.im_server.exception;

import com.li2n.im_server.vo.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-02-04 11:37
 */

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(SQLException.class)
    public ResponseResult mySqlException(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return ResponseResult.error("该数据有关联数据，操作失败", e.toString());
        }
        return ResponseResult.error("数据库异常：" + e.getMessage(), e.toString());
    }

}
