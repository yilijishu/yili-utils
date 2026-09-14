package com.yilijishu.aspet.ann;


import com.yilijishu.utils.exceptions.BizException;
import com.yilijishu.utils.result.ApiResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 加锁操作,
 * 抢夺执行权。
 * unlock默认为true 方法执行结束会主动释放锁。
 * 未检测到主动释放锁，则会在1分钟后自动释放锁.
 * key: 锁的KEY。必须填写。不可于其他锁的KEY重复。
 * time: 锁时间。默认为1
 * unit: 锁时间单位。默认为分钟.
 * exception: 未抢夺到执行权的抛错.
 * unlock: 是否主动解锁。默认为是
 *
 * @author zhangyang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Lock {

    String key();

    long time() default 1;

    TimeUnit unit() default TimeUnit.MINUTES;

    ExceptionConstants exception() default ExceptionConstants.DEFAULT_LOCK_BIZEXCEPTION;

    boolean unlock() default true;

    enum ExceptionConstants {
        DEFAULT_LOCK_BIZEXCEPTION {
            public BizException throwException(long time, TimeUnit unit) {
                return new BizException(ApiResult.LOCK_ERROR_CODE, time + " " + unit.name() + " 内不可多次请求");
            }
        };

        public BizException throwException(long time, TimeUnit unit) {
            throw new BizException(ApiResult.LOCK_ERROR_CODE, ApiResult.LOCK_ERROR_MSG);
        }
    }

}
