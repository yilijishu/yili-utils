package com.yilijishu.utils.exceptions;

/**
 * feign接口解析错误
 */
public class FeignParserException extends BizException {

    public FeignParserException(int code, String message) {
        super(code, message);
    }

    public FeignParserException(String message) {
        super(message);
    }

    public FeignParserException(Exception e) {
        super(e);
    }
}
