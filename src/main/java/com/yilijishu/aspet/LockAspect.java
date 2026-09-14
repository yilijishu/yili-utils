package com.yilijishu.aspet;

import com.yilijishu.aspet.ann.Lock;
import com.yilijishu.redis.RedisDataManager;
import com.yilijishu.utils.exceptions.BizException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 锁机制实现类，通过aspect来横切方法，来执行注入式锁.
 *
 * @author zhangyang
 */
@Aspect
public class LockAspect extends BaseAspect {

    private RedisDataManager redisDataManager;

    public LockAspect(RedisDataManager redisDataManager) {
        super();
        this.redisDataManager = redisDataManager;
    }

    @Pointcut("@annotation(com.yilijishu.aspet.ann.Lock)")
    public void lockPoint() {

    }

    @Around(value = "lockPoint() && @annotation(lock)")
    public Object lockAroud(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        return doLock(pjp, lock);
    }

    public Object doLock(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        Object result = null;
        String key = "YILI_LOCK_".concat(parse(lock.key(), getMethod(pjp), pjp.getArgs()));
        if (redisDataManager.tryLock(key, key, lock.time(), lock.unit())) {
            try {
                result = pjp.proceed();
            } catch (BizException e) {
                throw e;
            } catch (Exception e) {
                throw e;
            } finally {
                if (lock.unlock()) {
                    redisDataManager.unLock(key, key);
                }
            }
        } else {
            throw lock.exception().throwException(lock.time(), lock.unit());
        }
        return result;
    }

}
