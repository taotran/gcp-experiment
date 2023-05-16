package com.ttran.gcp.aspects;

import com.ttran.gcp.annotations.GCloudLog;
import com.ttran.gcp.enumerations.InfoCode;
import com.ttran.gcp.logging.GcpLoggingManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class GCloudLogAspect {

    @Autowired
    private GcpLoggingManager loggingManager;

    @Around("@annotation(com.ttran.gcp.annotations.GCloudLog)")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Inside gcloudLogAspect!!!!");
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final GCloudLog gCloudLog = method.getAnnotation(GCloudLog.class);
        if (LogLevel.INFO == gCloudLog.level()) {
            loggingManager.writeJsonInfo(InfoCode.of(gCloudLog.code()),
                    joinPoint.getTarget().getClass().getCanonicalName());
        }
        return joinPoint.proceed();
    }
}
