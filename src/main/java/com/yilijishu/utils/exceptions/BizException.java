package com.yilijishu.utils.exceptions;

import lombok.Data;

/**
 * 通用的异常处理
 */
@Data
public class BizException extends RuntimeException {

    private int code = 200600;

    private String message = "请不要偷偷尝试禁果哦.";

    public BizException(Exception e) {
        super(e);
    }

    public BizException() {

    }

    public BizException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException(String message, Exception e) {
        super(e);
        this.message = message;
    }

    public BizException(int code) {
        super();
        this.code = code;
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }

}
