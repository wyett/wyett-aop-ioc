package com.wyett.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/27 16:58
 * @description: TODO
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WyettRequestMapping {
    String value() default "";
}
