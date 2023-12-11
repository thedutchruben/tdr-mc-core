package nl.thedutchruben.mccore.spigot.commands;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TdrCommand {

    private Command command;
    private Default aDefault;
    private Fallback aFallback;

    @Getter
    private TdrSubCommand defaultCommand;
    @Getter
    private TdrSubCommand fallBackCommand;
    private Map<String,TdrSubCommand> subCommand;

    public TdrCommand(Command command) {

        this.command = command;
        this.subCommand = new HashMap<>();
    }

    public void setDefaultCommand(TdrSubCommand defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    public void setFallBackCommand(TdrSubCommand fallBackCommand) {
        this.fallBackCommand = fallBackCommand;
    }

    public void setaDefault(Default aDefault) {
        this.aDefault = aDefault;
    }

    public void setaFallback(Fallback aFallback) {
        this.aFallback = aFallback;
    }

    public Default getDefault() {
        return aDefault;
    }

    public Command getCommand() {
        return command;
    }

    public Map<String, TdrSubCommand> getSubCommand() {
        return subCommand;
    }
}

