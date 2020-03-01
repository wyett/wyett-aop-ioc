package com.wyett.plugin;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author : wyettLei
 * @date : Created in 2020/3/1 14:55
 * @description: TODO
 */

public class CountTimesPlugin implements MethodBeforeAdvice {

    private int count;

    protected void count(Method m) {
        count++;
    }

    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        count(method);
        System.out.println(String.format("The method {} invoked {} times", method.getName(), count));

    }
}
