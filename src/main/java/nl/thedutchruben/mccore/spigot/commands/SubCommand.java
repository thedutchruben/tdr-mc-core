package nl.thedutchruben.mccore.spigot.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {
    String subCommand();

    String description() default "";

    String permission() default "";

    String usage() default "";

    int minParams() default 0;

    int maxParams() default 0;

    boolean console() default false;
}