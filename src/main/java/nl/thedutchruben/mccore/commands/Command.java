package nl.thedutchruben.mccore.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    String command();

    String description() default "";

    String permission() default "";

    boolean console() default false;

    String[] aliases() default "";
}
