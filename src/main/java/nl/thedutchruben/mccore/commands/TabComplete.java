package nl.thedutchruben.mccore.commands;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

public interface TabComplete {
    Set<String> getCompletions(CommandSender commandSender);
}
