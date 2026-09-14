package com.yilijishu.aspet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yilijishu.utils.exceptions.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Aspect
@Slf4j
public class LogAspect extends BaseAspect {

    private ObjectMapper objectMapper;

    public LogAspect(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(com.yilijishu.aspet.ann.Tracked)")
    public void trackedPointcut() {

    }

    @Around("trackedPointcut()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        return doLogAround(pjp);
    }


    public Object doLogAround(ProceedingJoinPoint pjp) throws Throwable {
        long times = System.currentTimeMillis();
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        Object[] args = pjp.getArgs();
        List<Object> argList = new ArrayList<>();
        if (args != null) {
            for (Object arg : args) {
                if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {
                    argList.add(arg);
                }
            }
        }
        log.info("【url:{}】【method:{}】【args:{}】", url, method, objectMapper.writeValueAsString(argList));
        int responeCode = 200;
        Object result;
        try {
            Object resultObj;
            result = pjp.proceed();
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                responeCode = responseEntity.getStatusCodeValue();
                resultObj = responseEntity.getBody();
            } else {
                resultObj = result;
            }
            log.info("【url:{}】【method:{}】【exec time:{}】【responsecode:{}】【response:{}】",
                    url, method, System.currentTimeMillis() - times, responeCode, objectMapper.writeValueAsString(resultObj));
        } catch (BizException e) {
            log.error("【url:{}】 【method:{}】【exec time:{}】【error code:{}】【error message:{}】",
                    url, method, System.currentTimeMillis() - times, e.getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("【url:{}】【method:{}】【exec time:{}】【error code:exception】【error message:{}】",
                    url, method, System.currentTimeMillis() - times, e.getMessage());
            throw e;
        }
        return result;
    }


}
