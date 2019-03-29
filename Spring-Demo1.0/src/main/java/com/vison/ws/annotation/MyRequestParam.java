package com.vison.ws.annotation;


import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {

    String value();
}
