package com.wyett.plugin;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author : wyettLei
 * @date : Created in 2020/3/1 17:11
 * @description: TODO
 */

public class MethodInvokeLogPlugin implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println(method.getDeclaringClass().getName() + "."
                + method.getName() + ", args =" + Arrays.toString(objects) );
    }
}
