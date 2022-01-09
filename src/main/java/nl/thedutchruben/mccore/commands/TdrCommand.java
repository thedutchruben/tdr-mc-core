package nl.thedutchruben.mccore.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TdrCommand {

    private Command command;
    private Map<String,TdrSubCommand> subCommand;

    public TdrCommand(Command command) {

        this.command = command;
        this.subCommand = new HashMap<>();
    }


    public Command getCommand() {
        return command;
    }

    public Map<String, TdrSubCommand> getSubCommand() {
        return subCommand;
    }
}

