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
    
    // Changed from direct sub-commands to sub-command groups
    private Map<String, TdrSubCommandGroup> subCommandGroups;
    
    // Keep legacy direct sub-commands for backward compatibility
    private Map<String, TdrSubCommand> directSubCommands;

    public TdrCommand(Command command) {
        this.command = command;
        this.subCommandGroups = new HashMap<>();
        this.directSubCommands = new HashMap<>();
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

    public Map<String, TdrSubCommandGroup> getSubCommandGroups() {
        return subCommandGroups;
    }

    public Map<String, TdrSubCommand> getDirectSubCommands() {
        return directSubCommands;
    }

    public void addSubCommandGroup(String name, TdrSubCommandGroup group) {
        subCommandGroups.put(name, group);
    }

    public void addDirectSubCommand(String name, TdrSubCommand subCommand) {
        directSubCommands.put(name, subCommand);
    }

    public TdrSubCommandGroup getSubCommandGroup(String name) {
        return subCommandGroups.get(name);
    }

    public TdrSubCommand getDirectSubCommand(String name) {
        return directSubCommands.get(name);
    }

    // Legacy method for backward compatibility
    @Deprecated
    public Map<String, TdrSubCommand> getSubCommand() {
        return directSubCommands;
    }
}
