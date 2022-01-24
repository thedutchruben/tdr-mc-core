package nl.thedutchruben.mccore.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TabComplete {
    List<String> getCompletions(CommandSender commandSender);
}
