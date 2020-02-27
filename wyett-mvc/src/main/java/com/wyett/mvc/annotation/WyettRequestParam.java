package com.wyett.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/27 17:04
 * @description: TODO
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WyettRequestParam {
    String value() default "";
}
