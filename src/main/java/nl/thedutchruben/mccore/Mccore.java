package nl.thedutchruben.mccore;

import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.ComponentBuilder;
import nl.thedutchruben.mccore.config.UpdateCheckerConfig;
import nl.thedutchruben.mccore.global.caching.CachingManager;
import nl.thedutchruben.mccore.spigot.commands.CommandRegistry;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Mccore {
    private JavaPlugin javaPlugin;
    private static Mccore instance;
    private UpdateCheckerConfig updateCheckerConfig;
    private String tdrId;
    private CachingManager cachingManager;

    private String projectId;
    private PluginType type;

    /**
     * Start application
     * 
     * @param javaPlugin
     */
    public Mccore(JavaPlugin javaPlugin, String tdrId, String projectId, PluginType type) {
        instance = this;
        this.javaPlugin = javaPlugin;
        this.tdrId = tdrId;
        this.projectId = projectId;
        this.type = type;
        this.cachingManager = new CachingManager();

        switch (type) {
            case BUNGEE:
                break;
            case SPIGOT:
                try {
                    CommandRegistry commandRegistry = new CommandRegistry(this);

                    commandRegistry.setFailureHandler((reason, sender, command, subCommand) -> {
                        switch (reason) {
                            case COMMAND_NOT_FOUND:
                                sender.sendMessage(ChatColor.RED + "Can't find the command.");
                                break;
                            case NO_PERMISSION:
                                if (subCommand != null) {
                                    sender.sendMessage(ChatColor.RED + "You don't have the permission "
                                            + ChatColor.DARK_RED + subCommand.getSubCommand().permission());
                                } else {
                                    sender.sendMessage(ChatColor.RED + "You don't have the permission "
                                            + ChatColor.DARK_RED + command.getCommand().permission());
                                }
                                break;
                            case INSUFFICIENT_PARAMETER:
                                sender.sendMessage(ChatColor.RED + "Wrong usage :" + ChatColor.DARK_RED
                                        + subCommand.getSubCommand().usage());
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

        registerTabCompletions();
    }

    public void registerTabCompletions() {
        CommandRegistry.getTabCompletable().put("player", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!(commandSender instanceof Player) || ((Player) commandSender).canSee(onlinePlayer)) {
                    complete.add(onlinePlayer.getName());
                }
            }
            return complete;
        });

        CommandRegistry.getTabCompletable().put("playername", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!(commandSender instanceof Player) || ((Player) commandSender).canSee(onlinePlayer)) {
                    complete.add(onlinePlayer.getName());
                }
            }
            return complete;
        });

        CommandRegistry.getTabCompletable().put("uuid", commandSender -> {
            Set<String> complete = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (commandSender instanceof Player) {
                    if (((Player) commandSender).canSee(onlinePlayer)) {
                        complete.add(onlinePlayer.getUniqueId().toString());
                    }
                } else {
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

    public CachingManager getCachingManager() {
        return cachingManager;
    }

    public void startUpdateChecker(UpdateCheckerConfig updateCheckerConfig) {
        this.updateCheckerConfig = updateCheckerConfig;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.javaPlugin, () -> getUpdate(Bukkit.getConsoleSender(),false),
                60, updateCheckerConfig.getCheckTime());
        Bukkit.getPluginManager().registerEvents(new PlayerLoginListener(this), javaPlugin);
    }

    @SneakyThrows
    public void getUpdate(CommandSender commandSender,boolean showNoUpdateMessage ) {
        File bStatsFolder = new File(javaPlugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        URL url = new URL("https://api.thedutchruben.nl/api/v1/version/" + this.projectId);

        Bukkit.getScheduler().runTaskAsynchronously(this.javaPlugin, () -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Plugin", this.javaPlugin.getName());
                connection.setRequestProperty("Plugin-Type", type.name());
                connection.setRequestProperty("Plugin-Version", this.javaPlugin.getDescription().getVersion());
                connection.setRequestProperty("Plugin-Server-Version", this.javaPlugin.getServer().getVersion());
                connection.setRequestProperty("Plugin-Server-BukkitVersion",
                        this.javaPlugin.getServer().getBukkitVersion());
                connection.setRequestProperty("Plugin-Server-Id", config.getString("serverUuid"));
                connection.setRequestProperty("Plugin-Server-Players",
                        String.valueOf(this.javaPlugin.getServer().getOnlinePlayers().size()));

                try (BufferedReader br = (connection.getResponseCode() == 200)
                        ? new BufferedReader(new InputStreamReader(connection.getInputStream()))
                        : new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {

                    String data = br.readLine();
                    Matcher matcher = Pattern.compile("\\d{1,2}\\.\\d{1,2}\\.\\d{1,3}", Pattern.MULTILINE)
                            .matcher(data);
                    matcher.find();
                    int diff = compareVersions(matcher.group(0), javaPlugin.getDescription().getVersion());

                    Map<String, AtomicReference<String>> map = new HashMap<>();
                    map.put("download", new AtomicReference<>(""));
                    map.put("donate", new AtomicReference<>(""));
                    map.put("changelog", new AtomicReference<>(""));

                    for (String keyValue : data.split(",")) {
                        String[] split = keyValue.split(":");
                        if (split.length == 3) {
                            AtomicReference<String> valueRef = map.get(split[0]);
                            if (valueRef != null) {
                                valueRef.set(split[1] + split[2].replaceAll("\"|}", ""));
                            }
                        }
                    }

                    if (diff > 0) {
                        commandSender
                                .sendMessage(
                                        ChatColor.translateAlternateColorCodes('&', "&7There is a plugin update of &9"
                                                + javaPlugin.getDescription().getName() + "&7 available."));
                        if (commandSender instanceof Player) {
                            commandSender.spigot().sendMessage(new ComponentBuilder()
                                    .append(MessageUtil.getUrlMessage("&9&lDownload", map.get("download").get(),
                                            "Download newest version"))
                                    .append(ChatColor.translateAlternateColorCodes('&', " &7 | "))
                                    .append(MessageUtil.getUrlMessage("&9&lDonate", map.get("donate").get(),
                                            "Donate a coffee"))
                                    .append(ChatColor.translateAlternateColorCodes('&', " &7 | "))
                                    .append(MessageUtil.getUrlMessage("&9&lChangelog", map.get("changelog").get(),
                                            "See what is changed"))
                                    .create());
                        } else {
                            commandSender.sendMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            "&9&lDownload:&r&7 " + map.get("download").get()));
                            commandSender.sendMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            "&9&lDonate:&r&7 " + map.get("donate").get()));
                            commandSender.sendMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            "&9&lChangelog:&r&7 " + map.get("changelog").get()));
                        }
                        commandSender.sendMessage(" ");
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&8Lastest version: &a" + matcher.group(0) + "&8 | Your version: &c"
                                        + javaPlugin.getDescription().getVersion()));

                    }else{
                        if(showNoUpdateMessage){
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7There is no plugin update of &9"
                                    + javaPlugin.getDescription().getName() + "&7 available."));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public int compareVersions(String version1, String version2) {
        char[] v1Chars = version1.toCharArray();
        char[] v2Chars = version2.toCharArray();

        int currentSegmentV1 = 0, currentSegmentV2 = 0;
        int i = 0, j = 0;

        // Loop through both version strings until we reach the end of both
        while (i < v1Chars.length || j < v2Chars.length) {

            // Process the next segment of version 1
            while (i < v1Chars.length && v1Chars[i] != '.') {
                currentSegmentV1 = currentSegmentV1 * 10 + (v1Chars[i] - '0');
                i++;
            }

            // Process the next segment of version 2
            while (j < v2Chars.length && v2Chars[j] != '.') {
                currentSegmentV2 = currentSegmentV2 * 10 + (v2Chars[j] - '0');
                j++;
            }

            // Compare the current segments
            if (currentSegmentV1 > currentSegmentV2) {
                return 1;  // Version 1 is greater
            } else if (currentSegmentV1 < currentSegmentV2) {
                return -1; // Version 2 is greater
            }

            // Reset segment values for the next comparison and skip the dot
            currentSegmentV1 = currentSegmentV2 = 0;
            i++;
            j++;
        }

        // If all segments are equal, the versions are the same
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

    public enum PluginType {
        BUNGEE,
        SPIGOT
    }
}
