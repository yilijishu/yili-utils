package com.yilijishu.utils.exceptions;


import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * 超时错误
 */
@EqualsAndHashCode(callSuper = false)
public class TimeOutException extends RequestBizException {

    public TimeOutException(String message) {
        super(message);
    }


    public TimeOutException(Exception e) {
        super(e);
    }

    public TimeOutException(HttpStatus httpStatus, String responseBody, Exception e) {
        super(httpStatus, responseBody, e);
    }




}
