package com.example.petapp.infrastructure.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j//todo : aop적용안되는중 봐야함.
public class LogAspect {
    @Around("execution(* com.example.petapp.application.service..*Service.*(..))")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[LOG] Method : {}", joinPoint.getSignature().toShortString());
        Object[] args = joinPoint.getArgs();

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                log.info("[LOG] Arg{}: {}", i, args[i]);
            }
        } else {
            log.info("[LOG] No Arguments");
        }

        return joinPoint.proceed();
    }
}
