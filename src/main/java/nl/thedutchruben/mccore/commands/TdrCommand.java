package nl.thedutchruben.mccore.commands;

import java.lang.reflect.Method;

public class TdrCommand {
    private Object instance;

    private Method method;
    private Command annotation;

    public TdrCommand(Method method, Object instance, Command annotation) {
        this.method = method;
        this.instance = instance;
        this.annotation = annotation;
    }
}

