package com.ttran.gcp.annotations;

import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GCloudLog {

    LogLevel level() default LogLevel.INFO;

    String code();

    String message();
}
