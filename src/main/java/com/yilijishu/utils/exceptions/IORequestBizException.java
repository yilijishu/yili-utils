package com.yilijishu.utils.exceptions;

/**
 * IO请求错误
 */
public class IORequestBizException extends BizException {

    public IORequestBizException(String message) {
        super(message);
    }

    public IORequestBizException(Exception e) {
        super(e);
    }
}
