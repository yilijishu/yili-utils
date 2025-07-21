package com.yilijishu.utils.exceptions;

import lombok.Data;

/**
 * 重试多次的错误
 */
@Data
public class RetryBizException extends BizException {

    public RetryBizException() {
        super();
    }

    public RetryBizException(int code, String message) {
        super(code, message);
    }
}
