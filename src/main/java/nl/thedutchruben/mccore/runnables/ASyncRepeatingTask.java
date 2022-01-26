package nl.thedutchruben.mccore.runnables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ASyncRepeatingTask {

    int startTime() default 20;

    int repeatTime() default 20;

}
