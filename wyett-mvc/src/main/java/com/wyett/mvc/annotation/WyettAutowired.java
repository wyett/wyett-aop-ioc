package com.wyett.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/27 16:56
 * @description: TODO
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WyettAutowired {
    String value() default "";
}
