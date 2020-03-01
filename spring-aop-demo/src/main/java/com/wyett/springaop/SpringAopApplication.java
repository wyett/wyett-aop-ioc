package com.wyett.springaop;

import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/29 17:42
 * @description: TODO
 */

public class SpringAopApplication {
    public static void main(String[] args) {
        //把定义的beans.xml的类加载到容器MAP
        ApplicationContext appContext= new ClassPathXmlApplicationContext("beans.xml");
        //获取增强的bean
        Calculator calc = appContext.getBean(Calculator.class);
        //calc最顶层实现的接口Advised增强
        if(calc instanceof Advised) {
            System.out.println("calc是实现advised");
        }
        System.out.println(calc.div(4, 2));
    }
}
