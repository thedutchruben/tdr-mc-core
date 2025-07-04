package nl.thedutchruben.mccore.spigot.commands;

import java.util.HashMap;
import java.util.Map;

public class TdrSubCommandGroup {
    private final SubCommandGroup subCommandGroup;
    private final Object instance;
    private final Map<String, TdrSubCommand> subCommands = new HashMap<>();

    public TdrSubCommandGroup(SubCommandGroup subCommandGroup, Object instance) {
        this.subCommandGroup = subCommandGroup;
        this.instance = instance;
    }

    public SubCommandGroup getSubCommandGroup() {
        return subCommandGroup;
    }

    public Object getInstance() {
        return instance;
    }

    public Map<String, TdrSubCommand> getSubCommands() {
        return subCommands;
    }

    public void addSubCommand(String name, TdrSubCommand subCommand) {
        subCommands.put(name, subCommand);
    }

    public TdrSubCommand getSubCommand(String name) {
        return subCommands.get(name);
    }
}
