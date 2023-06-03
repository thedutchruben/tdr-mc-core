package nl.thedutchruben.mccore.spigot.commands;

import org.bukkit.command.CommandSender;

import java.util.Set;

public interface TabComplete {
    Set<String> getCompletions(CommandSender commandSender);
}
