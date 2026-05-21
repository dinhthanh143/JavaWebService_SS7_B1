package com.bank.digital.transaction.aspect;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {
    @Around("execution(* com.bank.digital.transaction.service.*.*(..))")
    public Object logExecutionTime(org.aspectj.lang.ProceedingJoinPoint joinPoint) throws Throwable{
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        System.out.println("Bat dau");
            Object result;
            try{
                result = joinPoint.proceed();
            }catch (Throwable e){
                System.out.println("Loi" + e.getMessage());
                throw e;
            }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Ket thuc" + methodName + " - Thoi gian: " + duration + "ms");
        return result;
    }
}
