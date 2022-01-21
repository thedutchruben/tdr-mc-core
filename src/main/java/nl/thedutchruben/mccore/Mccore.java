package nl.thedutchruben.mccore;


import nl.thedutchruben.mccore.commands.CommandRegistry;
import nl.thedutchruben.mccore.listeners.ListenersRegistry;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;


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

        new ListenersRegistry(this);
    }

    public static Mccore getInstance() {
        return instance;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }
}
