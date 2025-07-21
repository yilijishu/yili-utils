package com.yilijishu.utils.exceptions;

/**
 * 认证错误
 */
public class OauthException extends BizException {

    public static final int OAUTH_CODE = 401;

    public static final String OATH_MSG = "登录已过期，请重新登录";

    public OauthException(int code, String msg) {
        super(code, msg);
    }

    public OauthException() {
        super(OAUTH_CODE, OATH_MSG);
    }
}
