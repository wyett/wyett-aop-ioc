package com.wyett.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/27 16:40
 * @description: TODO
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WyettController {
    String value() default "";
}
