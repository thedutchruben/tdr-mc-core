package nl.thedutchruben.mccore;


import nl.thedutchruben.mccore.commands.CommandRegistry;
import nl.thedutchruben.mccore.listeners.ListenersRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class Mccore {
    private JavaPlugin javaPlugin;
    private static Mccore instance;

    public Mccore(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        instance = this;
        try {
            CommandRegistry commandRegistry = new CommandRegistry(this);

            commandRegistry.setFailureHandler((reason, sender, command,subCommand) -> {
                switch (reason) {
                    case COMMAND_NOT_FOUND:
                        sender.sendMessage(ChatColor.RED + "Can't find the command.");
                        break;
                    case NO_PERMISSION:
                        if(subCommand != null){
                            sender.sendMessage(ChatColor.RED + "You don't have the permission "+ ChatColor.DARK_RED + subCommand.getSubCommand().permission());
                        }else{
                            sender.sendMessage(ChatColor.RED + "You don't have the permission "+ ChatColor.DARK_RED + command.getCommand().permission());
                        }
                        break;
                    case INSUFFICIENT_PARAMETER:
                        sender.sendMessage(ChatColor.RED + "Wrong usage :"+ ChatColor.DARK_RED + subCommand.getSubCommand().usage());
                        break;
                }
            });
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }


        CommandRegistry.getTabCompletable().put("player", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(commandSender instanceof Player){
                    if(((Player) commandSender).canSee(onlinePlayer)){
                        complete.add(onlinePlayer.getName());
                    }
                }else{
                    complete.add(onlinePlayer.getName());
                }
            }
            return complete;
        });

        CommandRegistry.getTabCompletable().put("plugin", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                complete.add(plugin.getName());
            }
            return complete;
        });

        CommandRegistry.getTabCompletable().put("permission", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Permission permission : Bukkit.getPluginManager().getPermissions()) {
                complete.add(permission.getName());
            }
            return complete;
        });

        new ListenersRegistry(this);
    }

    public static Mccore getInstance() {
        return instance;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }
}
