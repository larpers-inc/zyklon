package dev.vili.zyklon.eventbus;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    boolean lambda() default false;
}