package nl.thedutchruben.mccore.spigot.commands;

import java.lang.reflect.Method;

public class TdrSubCommand {
    private Object instance;

    private Method method;
    private SubCommand subCommand;

    public TdrSubCommand(Method method, Object instance,SubCommand subCommand) {
        this.method = method;
        this.instance = instance;
        this.subCommand = subCommand;
    }


    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }
}

