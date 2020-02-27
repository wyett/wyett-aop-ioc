package com.wyett.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/27 17:02
 * @description: TODO
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WyettService {
    String value() default "";
}
