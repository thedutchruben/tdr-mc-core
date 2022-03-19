package nl.thedutchruben.mccore;


import nl.thedutchruben.mccore.commands.CommandRegistry;
import nl.thedutchruben.mccore.config.UpdateCheckerConfig;
import nl.thedutchruben.mccore.listeners.ListenersRegistry;
import nl.thedutchruben.mccore.runnables.RunnableRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;


public final class Mccore {
    private JavaPlugin javaPlugin;
    private static Mccore instance;
    private String tdrId;
    /**
     * Start application
     * @param javaPlugin
     */
    public Mccore(JavaPlugin javaPlugin,String tdrId) {
        this.javaPlugin = javaPlugin;
        this.tdrId = tdrId;
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

        CommandRegistry.getTabCompletable().put("playername", commandSender -> {
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

        CommandRegistry.getTabCompletable().put("uuid", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(commandSender instanceof Player){
                    if(((Player) commandSender).canSee(onlinePlayer)){
                        complete.add(onlinePlayer.getUniqueId().toString());
                    }
                }else{
                    complete.add(onlinePlayer.getUniqueId().toString());
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

        CommandRegistry.getTabCompletable().put("world", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (World world : Bukkit.getWorlds()) {
                complete.add(world.getName());
            }
            return complete;
        });

        CommandRegistry.getTabCompletable().put("entitytype", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (EntityType value : EntityType.values()) {
                complete.add(value.getKey().getKey());
            }
            return complete;
        });

        CommandRegistry.getTabCompletable().put("material", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Material value : Material.values()) {
                complete.add(value.name());
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

        new RunnableRegistry(this);
    }

    public void startUpdateChecker(UpdateCheckerConfig updateCheckerConfig)
    {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.javaPlugin, () -> getUpdate(Bukkit.getConsoleSender()),0,updateCheckerConfig.getCheckTime());

    }

    public void getUpdate(CommandSender commandSender){
        String url = "api.theduchruben.nl/api/v1/spigot/plugin/" + this.tdrId + "/version";
        Bukkit.getScheduler().runTaskAsynchronously(this.javaPlugin, () -> {
            //todo send request
            String data = "{pluginId:'13',spigotId:'1234',version:'1.1.1}";
            String pluginVersion = javaPlugin.getDescription().getVersion();
            if(true){
                commandSender.sendMessage("New update version %version %!");
            }else{
                if(commandSender instanceof ConsoleCommandSender){
                    Bukkit.getLogger().log(Level.INFO,"No update found");
                }
            }
        });
    }

    public static Mccore getInstance() {
        return instance;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public String getTdrId() {
        return tdrId;
    }
}
