package de.codingair.warpsystem.spigot.base.setupassistant.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(Functions.class)
public @interface Function {
    String name();
    String since() default "v4.2.7";
    String config() default "";
    String description() default "";
    String defaultValue();
    Class<?> clazz();
    String configPath();
}
