package com.wyett.springaop.aspects;

import org.aspectj.lang.JoinPoint;

import java.util.Arrays;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/29 16:49
 * @description: TODO
 */

public class LogAspects {

    // before func execution
    public void logStart(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getName() // func name
                + "正在运行.....参数是"
                + Arrays.asList(joinPoint.getArgs())); // args
    }

    // after func execution
    public void logEnd(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getName() // func name
                + "运行结束.....");
    }

    public void logReturn(Object result) {
        System.out.println("运行异常...运行结果是" + result);
    }

    public void logException(Exception exception) {
        System.out.println("运行异常...异常信息" + exception);
    }

}
