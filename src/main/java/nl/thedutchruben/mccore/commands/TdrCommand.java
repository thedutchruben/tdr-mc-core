package nl.thedutchruben.mccore.commands;

import java.util.HashMap;
import java.util.Map;

public class TdrCommand {

    private Command command;
    private Default aDefault;
    private TdrSubCommand defaultCommand;
    private Map<String,TdrSubCommand> subCommand;

    public TdrCommand(Command command) {

        this.command = command;
        this.subCommand = new HashMap<>();
    }

    public void setDefaultCommand(TdrSubCommand defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    public TdrSubCommand getDefaultCommand() {
        return defaultCommand;
    }

    public void setaDefault(Default aDefault) {
        this.aDefault = aDefault;
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

