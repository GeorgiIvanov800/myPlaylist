package org.myplaylist.myplaylist.service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Aspect
@Component
public class MonitoringAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringAspect.class);


    @Around("Pointcuts.WarnIfExecutionExceeds()")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        WarnIfExecutionExceeds annotation = getAnnotation(pjp);
        long time = annotation.timeInMillis();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        var returnValue = pjp.proceed();
        stopWatch.stop();

        if (stopWatch.getTotalTimeMillis() > time) {
            LOGGER.warn("Method {} ran for {} millis more than expected {}",
                    pjp.getSignature(),
                    stopWatch.getTotalTimeMillis(),
                    time);
        }

        return returnValue;
    }

    private static WarnIfExecutionExceeds getAnnotation(ProceedingJoinPoint pjp) {
        //Get the method
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        try {
            return pjp
                    .getTarget()
                    .getClass()
                    .getMethod(method.getName(), method.getParameterTypes()) // Get the method with its parameters
                    .getAnnotation(WarnIfExecutionExceeds.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
