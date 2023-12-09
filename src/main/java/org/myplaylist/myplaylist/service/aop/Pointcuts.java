package org.myplaylist.myplaylist.service.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

@Pointcut("@annotation(WarnIfExecutionExceeds)")
    public void WarnIfExecutionExceeds(){}
}
