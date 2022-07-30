package nl.thedutchruben.mccore;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.ComponentBuilder;
import nl.thedutchruben.mccore.spigot.commands.CommandRegistry;
import nl.thedutchruben.mccore.config.UpdateCheckerConfig;
import nl.thedutchruben.mccore.spigot.listeners.ListenersRegistry;
import nl.thedutchruben.mccore.spigot.runnables.RunnableRegistry;
import nl.thedutchruben.mccore.spigot.updates.PlayerLoginListener;
import nl.thedutchruben.mccore.utils.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


public final class Mccore {
    private JavaPlugin javaPlugin;
    private static Mccore instance;
    private UpdateCheckerConfig updateCheckerConfig;
    private String tdrId;

    private String projectId;
    private PluginType type;
    /**
     * Start application
     * @param javaPlugin
     */
    public Mccore(JavaPlugin javaPlugin,String tdrId,String projectId,PluginType type) {
        this.javaPlugin = javaPlugin;
        this.tdrId = tdrId;
        this.projectId = projectId;
        this.type = type;
        instance = this;

        switch (type){
            case BUNGEE:
                break;
            case SPIGOT:
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

                new RunnableRegistry(this);
                break;
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


    }

    public void startUpdateChecker(UpdateCheckerConfig updateCheckerConfig)
    {
        this.updateCheckerConfig = updateCheckerConfig;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.javaPlugin, () -> getUpdate(Bukkit.getConsoleSender()),60,updateCheckerConfig.getCheckTime());
        Bukkit.getPluginManager().registerEvents(new PlayerLoginListener(this),javaPlugin);
    }

    @SneakyThrows
    public void getUpdate(CommandSender commandSender){
        File bStatsFolder = new File(javaPlugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        URL url = new URL("https://api.thedutchruben.nl/api/v1/version/" + this.projectId );

        Bukkit.getScheduler().runTaskAsynchronously(this.javaPlugin, () -> {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Plugin", this.javaPlugin.getName());
                connection.setRequestProperty("Plugin-Type", type.name() );
                connection.setRequestProperty("Plugin-Version", this.javaPlugin.getDescription().getVersion());
                connection.setRequestProperty("Plugin-Server-Version", this.javaPlugin.getServer().getVersion());
                connection.setRequestProperty("Plugin-Server-BukkitVersion", this.javaPlugin.getServer().getBukkitVersion());
                connection.setRequestProperty("Plugin-Server-Id", config.getString("serverUuid"));
                connection.setRequestProperty("Plugin-Server-Players", String.valueOf(this.javaPlugin.getServer().getOnlinePlayers().size()));

                BufferedReader br = null;
                if(connection.getResponseCode() != 200){
                    return;
                }
                if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                String data = br.readLine();
                JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
            String pluginVersion = javaPlugin.getDescription().getVersion();
            int diff = versionCompare(jsonObject.get("version").getAsString(),pluginVersion);
             if(diff > 0){
                 commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7There is a plugin update of &9"+javaPlugin.getDescription().getName()+"&7 available."));
                 if(commandSender instanceof Player){
                     commandSender.spigot().sendMessage(new ComponentBuilder()
                             .append(MessageUtil.getUrlMessage("&9&lDownload",jsonObject.get("downloadUrl").getAsString(),"Download newest version"))
                             .append(ChatColor.translateAlternateColorCodes('&'," &7 | "))
                             .append(MessageUtil.getUrlMessage("&9&lDonate",jsonObject.get("donateUrl").getAsString(),"Donate a coffee"))
                             .append(ChatColor.translateAlternateColorCodes('&'," &7 | "))
                             .append(MessageUtil.getUrlMessage("&9&lChangelog",jsonObject.get("changeLog").getAsString(),"See what is changed")).create());
                 }else{
                     commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&9&lDownload: &r&7" + jsonObject.get("downloadUrl").getAsString()));
                     commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&9&lDonate: &r&7"+ jsonObject.get("donateUrl").getAsString()));
                     commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&9&lChangelog: &r&7" + jsonObject.get("changeLog").getAsString()));
                 }
                 commandSender.sendMessage(" ");
                 commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8Lastest version: &a"+jsonObject.get("version").getAsString()+"&8 | Your version: &c" + pluginVersion));

             }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public int versionCompare(String v1, String v2)
    {
        // vnum stores each numeric part of version
        int vnum1 = 0, vnum2 = 0;

        // loop until both String are processed
        for (int i = 0, j = 0; (i < v1.length()
                || j < v2.length());) {
            // Storing numeric part of
            // version 1 in vnum1
            while (i < v1.length()
                    && v1.charAt(i) != '.') {
                vnum1 = vnum1 * 10
                        + (v1.charAt(i) - '0');
                i++;
            }

            // storing numeric part
            // of version 2 in vnum2
            while (j < v2.length()
                    && v2.charAt(j) != '.') {
                vnum2 = vnum2 * 10
                        + (v2.charAt(j) - '0');
                j++;
            }

            if (vnum1 > vnum2)
                return 1;
            if (vnum2 > vnum1)
                return -1;

            // if equal, reset variables and
            // go for next numeric part
            vnum1 = vnum2 = 0;
            i++;
            j++;
        }
        return 0;
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

    public UpdateCheckerConfig getUpdateCheckerConfig() {
        return updateCheckerConfig;
    }

    public enum PluginType{
        BUNGEE,
        SPIGOT
    }
}
