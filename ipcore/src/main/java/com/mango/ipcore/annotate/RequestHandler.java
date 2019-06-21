package com.mango.ipcore.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: mango
 * Time: 2019/6/13 22:36
 * Version:
 * Desc: TODO()
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHandler {
    String value();
}
