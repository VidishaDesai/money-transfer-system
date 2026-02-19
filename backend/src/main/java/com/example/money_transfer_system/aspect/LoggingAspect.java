package com.example.money_transfer_system.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.example.money_transfer_system.service..*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.example.money_transfer_system.controller..*(..))")
    public void controllerMethods() {}

    @Around("serviceMethods() || controllerMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Sanitize sensitive data
        String sanitizedArgs = sanitizeArgs(args);

        log.info("[{}] Executing method: {} with args: {}", className, methodName, sanitizedArgs);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("[{}] Method {} executed successfully in {} ms", className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[{}] Method {} failed after {} ms with exception: {}", 
                    className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    private String sanitizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String argStr = arg.toString();
                    // Sanitize password fields
                    if (argStr.contains("password")) {
                        return "[SANITIZED]";
                    }
                    return argStr.length() > 100 ? argStr.substring(0, 100) + "..." : argStr;
                })
                .toList()
                .toString();
    }
}
