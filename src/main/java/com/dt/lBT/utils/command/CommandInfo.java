package com.dt.guildGate.utils.command;


import lombok.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    /**
     * Name string.
     *
     * @return the string
     */
    @NonNull String name();

    /**
     * Permission string.
     *
     * @return the string
     */
    String permission() default "";

}
