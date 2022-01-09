package nl.thedutchruben.mccore.commands;

import java.lang.reflect.Method;

public class TdrCommand {
    private Object instance;

    private Method method;
    private Command command;
    private SubCommand subCommand;

    public TdrCommand(Method method, Object instance,Command command, SubCommand subCommand) {
        this.method = method;
        this.instance = instance;
        this.command = command;
        this.subCommand = subCommand;
    }
}

