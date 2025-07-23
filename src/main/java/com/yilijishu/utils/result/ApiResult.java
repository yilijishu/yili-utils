package com.yilijishu.utils.result;

import lombok.Data;

@Data
public class ApiResult<T> {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_DEFAULT_FAIL = -1;
    public static final int EXCEPTION_CODE = 200500;
    public static final int EXCEPTION_PARAM_CODE = 200503;
    public static final int NOT_FOUND_CODE = 200404;
    public static final int CHECK_FAIL = 200001;
    public static final String DEFAULT_MSG = "成功";
    public static final int PARAM_ERROR_CODE = 200301;
    public static final int LOCK_ERROR_CODE = 200700;
    public static final String LOCK_ERROR_MSG = "不可以重复提交";
    public static final Integer MONEY_ERROR = 300001;
    public static final String MONEY_ERROR_MSG = "余额不足，请充值后再抽取";
    private int code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> ApiResult resultSuccess(T data) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(CODE_SUCCESS);
        apiResult.setMessage(DEFAULT_MSG);
        apiResult.setData(data);
        return apiResult;
    }

    public static <T> ApiResult resultSuccess() {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(CODE_SUCCESS);
        apiResult.setMessage(DEFAULT_MSG);
        return apiResult;
    }

    public static <T> ApiResult resultFail() {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(CODE_DEFAULT_FAIL);
        apiResult.setMessage("错误");
        return apiResult;
    }

    public static <T> ApiResult resultFail(String msg) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(CODE_DEFAULT_FAIL);
        apiResult.setMessage(msg);
        return apiResult;
    }

    public static <T> ApiResult resultFail(int code, String msg) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(code);
        apiResult.setMessage(msg);
        return apiResult;
    }
}
