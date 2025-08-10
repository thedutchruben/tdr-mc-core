# Command System Guide

This comprehensive guide covers the TDR MC Core command system, from basic setup to advanced usage patterns.

## Table of Contents

- [Getting Started](#getting-started)
- [Command Architecture](#command-architecture)
- [Basic Commands](#basic-commands)
- [Sub-Command Groups](#sub-command-groups)
- [Advanced Features](#advanced-features)
- [Tab Completion](#tab-completion)
- [Error Handling](#error-handling)
- [Best Practices](#best-practices)
- [Real-World Examples](#real-world-examples)

## Getting Started

### Project Setup

First, add TDR MC Core to your plugin:

```xml
<dependency>
    <groupId>nl.thedutchruben</groupId>
    <artifactId>mccore</artifactId>
    <version>1.7.0</version>
</dependency>
```

### Initialize TDR MC Core

In your main plugin class:

```java
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Initialize TDR MC Core
        new Mccore(this, "your-tdr-id", "your-project-id", Mccore.PluginType.SPIGOT);
        
        getLogger().info("Plugin enabled with TDR MC Core!");
    }
}
```

### Register Your Commands in plugin.yml

Don't forget to register your commands in `plugin.yml`:

```yaml
commands:
  mycommand:
    description: My awesome command
    usage: /mycommand
    permission: myplugin.use
  economy:
    description: Economy system
    usage: /economy
    aliases: [eco, money]
```

## Command Architecture

TDR MC Core uses a three-level hierarchical command system:

```
┌─ Command (@Command)
│  ├─ Default (@Default)
│  ├─ Fallback (@Fallback)  
│  ├─ Direct Sub-Commands (@SubCommand)
│  └─ Sub-Command Groups (@SubCommandGroup)
│     └─ Group Sub-Commands (@SubCommand)
```

### Command Flow

1. **Command Execution**: User runs `/command arg1 arg2`
2. **Permission Check**: Validates base command permission
3. **Route Resolution**: Determines which handler to execute
4. **Parameter Validation**: Checks parameter count and format
5. **Method Invocation**: Calls the appropriate command method

## Basic Commands

### Minimal Command

The simplest possible command:

```java
@Command(command = "hello")
public class HelloCommand {
    
    @Default
    public void defaultCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("Hello, " + sender.getName() + "!");
    }
}
```

**Usage:** `/hello`

### Command with Sub-Commands

```java
@Command(command = "time", description = "Time management commands", permission = "myplugin.time")
public class TimeCommand {
    
    @Default
    public void defaultCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot check time!");
            return;
        }
        
        Player player = (Player) sender;
        long time = player.getWorld().getTime();
        sender.sendMessage("Current time: " + time);
    }
    
    @SubCommand(
        subCommand = "set",
        description = "Set the time",
        permission = "myplugin.time.set",
        usage = "<time>",
        minParams = 1,
        maxParams = 1
    )
    public void setTime(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot set time!");
            return;
        }
        
        Player player = (Player) sender;
        try {
            long time = Long.parseLong(args.get(0));
            player.getWorld().setTime(time);
            sender.sendMessage("Time set to " + time);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid time format!");
        }
    }
    
    @SubCommand(
        subCommand = "day",
        description = "Set time to day",
        permission = "myplugin.time.set"
    )
    public void setDay(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot set time!");
            return;
        }
        
        Player player = (Player) sender;
        player.getWorld().setTime(1000);
        sender.sendMessage("Time set to day!");
    }
    
    @SubCommand(
        subCommand = "night",
        description = "Set time to night",
        permission = "myplugin.time.set"
    )
    public void setNight(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot set time!");
            return;
        }
        
        Player player = (Player) sender;
        player.getWorld().setTime(13000);
        sender.sendMessage("Time set to night!");
    }
    
    @Fallback
    public void fallback(CommandSender sender, List<String> args) {
        sender.sendMessage("§cUnknown time command. Use '/time help' for help.");
    }
}
```

**Usage:**
- `/time` - Show current time
- `/time set 12000` - Set specific time
- `/time day` - Set to day
- `/time night` - Set to night

## Sub-Command Groups

Sub-command groups allow you to organize related commands logically:

```java
@Command(command = "server", description = "Server management", permission = "server.use")
public class ServerCommand {
    
    @Default
    public void defaultCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("§6=== Server Management ===");
        sender.sendMessage("§eUse '/server help' for available commands");
    }
    
    // Player management group
    @SubCommandGroup(value = "player", description = "Player management", permission = "server.player")
    public static class PlayerManagement {
        
        @SubCommand(
            subCommand = "kick",
            description = "Kick a player",
            permission = "server.player.kick",
            usage = "<player> [reason]",
            minParams = 1,
            maxParams = 2
        )
        public void kick(CommandSender sender, List<String> args) {
            String playerName = args.get(0);
            String reason = args.size() > 1 ? args.get(1) : "No reason specified";
            
            Player target = Bukkit.getPlayer(playerName);
            if (target == null) {
                sender.sendMessage("§cPlayer not found: " + playerName);
                return;
            }
            
            target.kickPlayer("§cKicked by " + sender.getName() + ": " + reason);
            sender.sendMessage("§aKicked " + playerName + " for: " + reason);
        }
        
        @SubCommand(
            subCommand = "ban",
            description = "Ban a player",
            permission = "server.player.ban",
            usage = "<player> [reason]",
            minParams = 1,
            maxParams = 2
        )
        public void ban(CommandSender sender, List<String> args) {
            String playerName = args.get(0);
            String reason = args.size() > 1 ? args.get(1) : "No reason specified";
            
            BanList banList = Bukkit.getBanList(BanList.Type.NAME);
            banList.addBan(playerName, reason, null, sender.getName());
            
            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                target.kickPlayer("§cBanned: " + reason);
            }
            
            sender.sendMessage("§aBanned " + playerName + " for: " + reason);
        }
        
        @SubCommand(
            subCommand = "info",
            description = "Get player information",
            permission = "server.player.info",
            usage = "<player>",
            minParams = 1,
            maxParams = 1
        )
        public void info(CommandSender sender, List<String> args) {
            String playerName = args.get(0);
            Player target = Bukkit.getPlayer(playerName);
            
            if (target == null) {
                sender.sendMessage("§cPlayer not found: " + playerName);
                return;
            }
            
            sender.sendMessage("§6=== Player Info: " + target.getName() + " ===");
            sender.sendMessage("§eUUID: " + target.getUniqueId());
            sender.sendMessage("§eWorld: " + target.getWorld().getName());
            sender.sendMessage("§eGamemode: " + target.getGameMode());
            sender.sendMessage("§eHealth: " + target.getHealth() + "/" + target.getMaxHealth());
            sender.sendMessage("§eLevel: " + target.getLevel());
        }
    }
    
    // World management group
    @SubCommandGroup(value = "world", description = "World management", permission = "server.world")
    public static class WorldManagement {
        
        @SubCommand(
            subCommand = "list",
            description = "List all worlds",
            permission = "server.world.list"
        )
        public void listWorlds(CommandSender sender, List<String> args) {
            sender.sendMessage("§6=== World List ===");
            for (World world : Bukkit.getWorlds()) {
                int players = world.getPlayers().size();
                sender.sendMessage("§e" + world.getName() + " §7(" + players + " players)");
            }
        }
        
        @SubCommand(
            subCommand = "tp",
            description = "Teleport to world spawn",
            permission = "server.world.tp",
            usage = "<world>",
            minParams = 1,
            maxParams = 1,
            console = false
        )
        public void teleportToWorld(CommandSender sender, List<String> args) {
            Player player = (Player) sender;
            String worldName = args.get(0);
            
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                sender.sendMessage("§cWorld not found: " + worldName);
                return;
            }
            
            player.teleport(world.getSpawnLocation());
            sender.sendMessage("§aTeleported to " + worldName);
        }
        
        @SubCommand(
            subCommand = "info",
            description = "Get world information",
            permission = "server.world.info",
            usage = "<world>",
            minParams = 1,
            maxParams = 1
        )
        public void worldInfo(CommandSender sender, List<String> args) {
            String worldName = args.get(0);
            World world = Bukkit.getWorld(worldName);
            
            if (world == null) {
                sender.sendMessage("§cWorld not found: " + worldName);
                return;
            }
            
            sender.sendMessage("§6=== World Info: " + world.getName() + " ===");
            sender.sendMessage("§eEnvironment: " + world.getEnvironment());
            sender.sendMessage("§eDifficulty: " + world.getDifficulty());
            sender.sendMessage("§eTime: " + world.getTime());
            sender.sendMessage("§eWeather: " + (world.hasStorm() ? "Storm" : "Clear"));
            sender.sendMessage("§ePlayers: " + world.getPlayers().size());
        }
    }
    
    // Direct sub-commands for simple operations
    @SubCommand(
        subCommand = "reload",
        description = "Reload server configuration",
        permission = "server.reload",
        console = true
    )
    public void reload(CommandSender sender, List<String> args) {
        // Reload logic here
        sender.sendMessage("§aServer configuration reloaded!");
    }
    
    @SubCommand(
        subCommand = "version",
        description = "Show server version",
        permission = "server.version",
        console = true
    )
    public void version(CommandSender sender, List<String> args) {
        sender.sendMessage("§6Server Version: §e" + Bukkit.getVersion());
        sender.sendMessage("§6Bukkit Version: §e" + Bukkit.getBukkitVersion());
    }
}
```

**Usage:**
- `/server` - Default command
- `/server player kick Steve` - Kick Steve
- `/server player ban Griefer Griefing` - Ban player
- `/server world list` - List worlds  
- `/server world tp nether` - Teleport to nether
- `/server reload` - Reload config
- `/server version` - Show version

## Advanced Features

### Parameter Types and Validation

```java
@Command(command = "admin", permission = "admin.use")
public class AdminCommand {
    
    @SubCommand(
        subCommand = "give",
        description = "Give items to player",
        usage = "<player> <material> [amount] [data]",
        minParams = 2,
        maxParams = 4
    )
    public void giveItem(CommandSender sender, List<String> args) {
        // Required parameters
        String playerName = args.get(0);
        String materialName = args.get(1);
        
        // Optional parameters with defaults
        int amount = 1;
        if (args.size() > 2) {
            try {
                amount = Integer.parseInt(args.get(2));
                if (amount <= 0 || amount > 64) {
                    sender.sendMessage("§cAmount must be between 1 and 64!");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount: " + args.get(2));
                return;
            }
        }
        
        short data = 0;
        if (args.size() > 3) {
            try {
                data = Short.parseShort(args.get(3));
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid data value: " + args.get(3));
                return;
            }
        }
        
        // Validate player
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + playerName);
            return;
        }
        
        // Validate material
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid material: " + materialName);
            return;
        }
        
        // Give item
        ItemStack item = new ItemStack(material, amount, data);
        target.getInventory().addItem(item);
        
        sender.sendMessage("§aGave " + amount + "x " + material.name() + " to " + target.getName());
        target.sendMessage("§aReceived " + amount + "x " + material.name() + " from " + sender.getName());
    }
}
```

### Console vs Player Commands

```java
@Command(command = "broadcast", permission = "broadcast.use")
public class BroadcastCommand {
    
    @SubCommand(
        subCommand = "all",
        description = "Broadcast to all players",
        usage = "<message>",
        minParams = 1,
        maxParams = Integer.MAX_VALUE,
        console = true  // Can be used from console
    )
    public void broadcastAll(CommandSender sender, List<String> args) {
        String message = String.join(" ", args);
        Bukkit.broadcastMessage("§6[Broadcast] §f" + message);
        sender.sendMessage("§aBroadcast sent to all players");
    }
    
    @SubCommand(
        subCommand = "world",
        description = "Broadcast to current world",
        usage = "<message>",
        minParams = 1,
        maxParams = Integer.MAX_VALUE,
        console = false  // Players only
    )
    public void broadcastWorld(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command is for players only!");
            return;
        }
        
        Player player = (Player) sender;
        String message = String.join(" ", args);
        
        for (Player worldPlayer : player.getWorld().getPlayers()) {
            worldPlayer.sendMessage("§6[World Broadcast] §f" + message);
        }
        
        sender.sendMessage("§aBroadcast sent to " + player.getWorld().getPlayers().size() + " players in " + player.getWorld().getName());
    }
}
```

### Dynamic Command Behavior

```java
@Command(command = "kit", permission = "kit.use")
public class KitCommand {
    
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 300000; // 5 minutes
    
    @SubCommand(
        subCommand = "starter",
        description = "Get starter kit",
        usage = "",
        console = false
    )
    public void starterKit(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        String playerId = player.getUniqueId().toString();
        
        // Check cooldown
        if (cooldowns.containsKey(playerId)) {
            long timeLeft = cooldowns.get(playerId) + COOLDOWN_TIME - System.currentTimeMillis();
            if (timeLeft > 0) {
                long minutes = timeLeft / 60000;
                long seconds = (timeLeft % 60000) / 1000;
                player.sendMessage("§cKit cooldown: " + minutes + "m " + seconds + "s remaining");
                return;
            }
        }
        
        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cYour inventory is full!");
            return;
        }
        
        // Give kit items
        player.getInventory().addItem(
            new ItemStack(Material.WOODEN_SWORD),
            new ItemStack(Material.WOODEN_PICKAXE),
            new ItemStack(Material.BREAD, 16),
            new ItemStack(Material.OAK_LOG, 32)
        );
        
        // Set cooldown
        cooldowns.put(playerId, System.currentTimeMillis());
        
        player.sendMessage("§aYou received the starter kit!");
    }
    
    @SubCommand(
        subCommand = "cooldown",
        description = "Check kit cooldown",
        console = false
    )
    public void checkCooldown(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        String playerId = player.getUniqueId().toString();
        
        if (!cooldowns.containsKey(playerId)) {
            player.sendMessage("§aNo cooldown - kit available!");
            return;
        }
        
        long timeLeft = cooldowns.get(playerId) + COOLDOWN_TIME - System.currentTimeMillis();
        if (timeLeft <= 0) {
            cooldowns.remove(playerId);
            player.sendMessage("§aNo cooldown - kit available!");
        } else {
            long minutes = timeLeft / 60000;
            long seconds = (timeLeft % 60000) / 1000;
            player.sendMessage("§eKit cooldown: " + minutes + "m " + seconds + "s remaining");
        }
    }
}
```

## Tab Completion

TDR MC Core provides automatic tab completion for parameter types specified in the `usage` attribute:

### Built-in Tab Completion Types

```java
@SubCommand(
    subCommand = "teleport",
    usage = "<player> <world>",  // Automatically completes player names and world names
    minParams = 2,
    maxParams = 2
)
public void teleport(CommandSender sender, List<String> args) {
    // Implementation
}
```

**Available types:**
- `player` - Online player names
- `uuid` - Player UUIDs  
- `world` - World names
- `material` - Material types
- `entitytype` - Entity types
- `permission` - Available permissions
- `color` - Chat colors
- `plugin` - Loaded plugins

### Custom Tab Completion

Register custom tab completions in your main class:

```java
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        new Mccore(this, "tdr-id", "project-id", Mccore.PluginType.SPIGOT);
        
        // Register custom tab completions
        CommandRegistry.getTabCompletable().put("kit", sender -> {
            Set<String> kits = new HashSet<>();
            kits.add("starter");
            kits.add("pvp");
            kits.add("builder");
            kits.add("admin");
            return kits;
        });
        
        CommandRegistry.getTabCompletable().put("gamemode", sender -> {
            Set<String> modes = new HashSet<>();
            modes.add("survival");
            modes.add("creative"); 
            modes.add("adventure");
            modes.add("spectator");
            return modes;
        });
    }
}
```

Then use in commands:

```java
@SubCommand(
    subCommand = "give",
    usage = "<player> <kit>",  // Uses custom 'kit' tab completion
    minParams = 2,
    maxParams = 2
)
public void giveKit(CommandSender sender, List<String> args) {
    // Implementation
}
```

## Error Handling

### Built-in Error Handling

TDR MC Core automatically handles many error scenarios, but you can customize the error messages:

```java
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        Mccore mccore = new Mccore(this, "tdr-id", "project-id", Mccore.PluginType.SPIGOT);
        
        // Get command registry and set custom failure handler
        try {
            CommandRegistry registry = new CommandRegistry(mccore);
            registry.setFailureHandler((reason, sender, command, subCommand) -> {
                switch (reason) {
                    case COMMAND_NOT_FOUND:
                        sender.sendMessage("§cUnknown command. Use '/help' for available commands.");
                        break;
                    case NO_PERMISSION:
                        sender.sendMessage("§cInsufficient permissions!");
                        break;
                    case INSUFFICIENT_PARAMETER:
                        if (subCommand != null) {
                            sender.sendMessage("§cUsage: /" + command.getCommand().command() + " " + subCommand.getSubCommand().subCommand() + " " + subCommand.getSubCommand().usage());
                        }
                        break;
                    case NOT_PLAYER:
                        sender.sendMessage("§cThis command can only be used by players!");
                        break;
                    case REFLECTION_ERROR:
                        sender.sendMessage("§cAn internal error occurred. Please contact an administrator.");
                        break;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Manual Error Handling

```java
@SubCommand(subCommand = "economy", usage = "<add|remove|set> <player> <amount>", minParams = 3, maxParams = 3)
public void manageEconomy(CommandSender sender, List<String> args) {
    String action = args.get(0).toLowerCase();
    String playerName = args.get(1);
    
    // Validate action
    if (!action.equals("add") && !action.equals("remove") && !action.equals("set")) {
        sender.sendMessage("§cInvalid action: " + action + ". Use add, remove, or set.");
        return;
    }
    
    // Validate player
    OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
    if (!target.hasPlayedBefore() && !target.isOnline()) {
        sender.sendMessage("§cPlayer has never played: " + playerName);
        return;
    }
    
    // Validate amount
    double amount;
    try {
        amount = Double.parseDouble(args.get(2));
        if (amount < 0) {
            sender.sendMessage("§cAmount cannot be negative!");
            return;
        }
    } catch (NumberFormatException e) {
        sender.sendMessage("§cInvalid amount: " + args.get(2));
        return;
    }
    
    // Process the command
    switch (action) {
        case "add":
            // Add money logic
            sender.sendMessage("§aAdded $" + amount + " to " + playerName);
            break;
        case "remove":
            // Remove money logic
            sender.sendMessage("§aRemoved $" + amount + " from " + playerName);
            break;
        case "set":
            // Set money logic
            sender.sendMessage("§aSet " + playerName + "'s balance to $" + amount);
            break;
    }
}
```

## Best Practices

### 1. Command Organization

```java
// Good: Organized by functionality
@Command(command = "shop")
public class ShopCommand {
    
    @SubCommandGroup(value = "admin", permission = "shop.admin")
    public static class AdminCommands {
        // Admin-only commands here
    }
    
    @SubCommandGroup(value = "player", permission = "shop.use")  
    public static class PlayerCommands {
        // Player commands here
    }
}

// Bad: Everything mixed together
@Command(command = "shop")
public class ShopCommand {
    
    @SubCommand(subCommand = "buy") // Player command
    public void buy(...) { }
    
    @SubCommand(subCommand = "admin-reload") // Admin command - should be grouped
    public void adminReload(...) { }
}
```

### 2. Permission Hierarchy

```java
@Command(command = "myplugin", permission = "myplugin.use")
public class MyPluginCommand {
    
    @SubCommandGroup(value = "admin", permission = "myplugin.admin")
    public static class AdminCommands {
        
        @SubCommand(subCommand = "reload", permission = "myplugin.admin.reload")
        public void reload(...) { }
        
        @SubCommand(subCommand = "debug", permission = "myplugin.admin.debug")  
        public void debug(...) { }
    }
}
```

**Permission nodes:**
- `myplugin.use` - Basic plugin access
- `myplugin.admin` - Admin group access  
- `myplugin.admin.reload` - Specific reload permission
- `myplugin.admin.debug` - Specific debug permission

### 3. Input Validation

```java
@SubCommand(subCommand = "setspawn", usage = "[world]", maxParams = 1)
public void setSpawn(CommandSender sender, List<String> args) {
    // Always check if sender is a player when needed
    if (!(sender instanceof Player)) {
        sender.sendMessage("§cOnly players can set spawn!");
        return;
    }
    
    Player player = (Player) sender;
    World world = player.getWorld();
    
    // Handle optional parameters safely
    if (!args.isEmpty()) {
        World targetWorld = Bukkit.getWorld(args.get(0));
        if (targetWorld == null) {
            sender.sendMessage("§cWorld not found: " + args.get(0));
            return;
        }
        world = targetWorld;
    }
    
    // Set spawn logic
    world.setSpawnLocation(player.getLocation());
    sender.sendMessage("§aSpawn set in " + world.getName());
}
```

### 4. Async Operations

```java
@SubCommand(subCommand = "lookup", usage = "<player>", minParams = 1, maxParams = 1)
public void lookupPlayer(CommandSender sender, List<String> args) {
    String playerName = args.get(0);
    
    // Perform database lookup asynchronously
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        PlayerData data = database.getPlayerData(playerName);
        
        // Return to main thread for sending messages
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (data == null) {
                sender.sendMessage("§cPlayer data not found: " + playerName);
            } else {
                sender.sendMessage("§6=== Player Data ===");
                sender.sendMessage("§eName: " + data.getName());
                sender.sendMessage("§eFirst Join: " + data.getFirstJoin());
                sender.sendMessage("§eLast Seen: " + data.getLastSeen());
            }
        });
    });
}
```

### 5. Configuration Integration

```java
@Command(command = "config")
public class ConfigCommand {
    
    private final FileConfiguration config;
    
    public ConfigCommand(FileConfiguration config) {
        this.config = config;
    }
    
    @SubCommand(subCommand = "reload", permission = "plugin.config.reload")
    public void reloadConfig(CommandSender sender, List<String> args) {
        try {
            plugin.reloadConfig();
            sender.sendMessage("§aConfiguration reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage("§cError reloading configuration: " + e.getMessage());
        }
    }
    
    @SubCommand(subCommand = "set", usage = "<key> <value>", minParams = 2, maxParams = 2)
    public void setConfig(CommandSender sender, List<String> args) {
        String key = args.get(0);
        String value = args.get(1);
        
        // Try to parse as number first
        try {
            if (value.contains(".")) {
                config.set(key, Double.parseDouble(value));
            } else {
                config.set(key, Integer.parseInt(value));
            }
        } catch (NumberFormatException e) {
            // Parse as boolean
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                config.set(key, Boolean.parseBoolean(value));
            } else {
                // Store as string
                config.set(key, value);
            }
        }
        
        plugin.saveConfig();
        sender.sendMessage("§aSet " + key + " = " + value);
    }
}
```

## Real-World Examples

### Complete Economy System

```java
@Command(command = "economy", description = "Economy management", permission = "economy.use", aliases = {"eco", "money"})
public class EconomyCommand {
    
    private final EconomyManager economyManager;
    
    public EconomyCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }
    
    @Default(console = false)
    public void defaultCommand(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        double balance = economyManager.getBalance(player);
        player.sendMessage("§6Your balance: §a$" + String.format("%.2f", balance));
    }
    
    @SubCommand(
        subCommand = "balance",
        description = "Check balance",
        usage = "[player]",
        maxParams = 1
    )
    public void balance(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole must specify a player!");
                return;
            }
            
            Player player = (Player) sender;
            double balance = economyManager.getBalance(player);
            player.sendMessage("§6Your balance: §a$" + String.format("%.2f", balance));
        } else {
            if (!sender.hasPermission("economy.balance.others")) {
                sender.sendMessage("§cYou don't have permission to check other players' balance!");
                return;
            }
            
            OfflinePlayer target = Bukkit.getOfflinePlayer(args.get(0));
            double balance = economyManager.getBalance(target);
            sender.sendMessage("§6" + target.getName() + "'s balance: §a$" + String.format("%.2f", balance));
        }
    }
    
    @SubCommand(
        subCommand = "pay",
        description = "Pay another player",
        usage = "<player> <amount>",
        minParams = 2,
        maxParams = 2,
        console = false
    )
    public void pay(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        String targetName = args.get(0);
        
        if (player.getName().equalsIgnoreCase(targetName)) {
            player.sendMessage("§cYou cannot pay yourself!");
            return;
        }
        
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage("§cPlayer not found: " + targetName);
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args.get(1));
            if (amount <= 0) {
                player.sendMessage("§cAmount must be positive!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount: " + args.get(1));
            return;
        }
        
        if (!economyManager.hasBalance(player, amount)) {
            player.sendMessage("§cInsufficient funds!");
            return;
        }
        
        economyManager.withdraw(player, amount);
        economyManager.deposit(target, amount);
        
        player.sendMessage("§aPaid $" + String.format("%.2f", amount) + " to " + target.getName());
        target.sendMessage("§aReceived $" + String.format("%.2f", amount) + " from " + player.getName());
    }
    
    @SubCommandGroup(value = "admin", description = "Administrative commands", permission = "economy.admin")
    public static class AdminCommands {
        
        @SubCommand(
            subCommand = "give",
            description = "Give money to player",
            usage = "<player> <amount>",
            minParams = 2,
            maxParams = 2,
            console = true
        )
        public void give(CommandSender sender, List<String> args) {
            String playerName = args.get(0);
            
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            
            double amount;
            try {
                amount = Double.parseDouble(args.get(1));
                if (amount <= 0) {
                    sender.sendMessage("§cAmount must be positive!");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount: " + args.get(1));
                return;
            }
            
            EconomyManager.getInstance().deposit(target, amount);
            sender.sendMessage("§aGave $" + String.format("%.2f", amount) + " to " + target.getName());
            
            if (target.isOnline()) {
                target.getPlayer().sendMessage("§aReceived $" + String.format("%.2f", amount) + " from server");
            }
        }
        
        @SubCommand(
            subCommand = "take",
            description = "Take money from player",
            usage = "<player> <amount>",
            minParams = 2,
            maxParams = 2,
            console = true
        )
        public void take(CommandSender sender, List<String> args) {
            String playerName = args.get(0);
            
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            
            double amount;
            try {
                amount = Double.parseDouble(args.get(1));
                if (amount <= 0) {
                    sender.sendMessage("§cAmount must be positive!");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount: " + args.get(1));
                return;
            }
            
            if (!EconomyManager.getInstance().hasBalance(target, amount)) {
                sender.sendMessage("§cPlayer doesn't have enough money!");
                return;
            }
            
            EconomyManager.getInstance().withdraw(target, amount);
            sender.sendMessage("§aTook $" + String.format("%.2f", amount) + " from " + target.getName());
            
            if (target.isOnline()) {
                target.getPlayer().sendMessage("§c$" + String.format("%.2f", amount) + " was taken from your account");
            }
        }
        
        @SubCommand(
            subCommand = "set",
            description = "Set player's balance",
            usage = "<player> <amount>",
            minParams = 2,
            maxParams = 2,
            console = true
        )
        public void set(CommandSender sender, List<String> args) {
            String playerName = args.get(0);
            
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
            
            double amount;
            try {
                amount = Double.parseDouble(args.get(1));
                if (amount < 0) {
                    sender.sendMessage("§cAmount cannot be negative!");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount: " + args.get(1));
                return;
            }
            
            EconomyManager.getInstance().setBalance(target, amount);
            sender.sendMessage("§aSet " + target.getName() + "'s balance to $" + String.format("%.2f", amount));
            
            if (target.isOnline()) {
                target.getPlayer().sendMessage("§eYour balance has been set to $" + String.format("%.2f", amount));
            }
        }
        
        @SubCommand(
            subCommand = "top",
            description = "Show top balances",
            usage = "[amount]",
            maxParams = 1,
            console = true
        )
        public void top(CommandSender sender, List<String> args) {
            int limit = 10;
            
            if (!args.isEmpty()) {
                try {
                    limit = Integer.parseInt(args.get(0));
                    if (limit <= 0 || limit > 50) {
                        sender.sendMessage("§cLimit must be between 1 and 50!");
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid limit: " + args.get(0));
                    return;
                }
            }
            
            List<Map.Entry<String, Double>> topBalances = EconomyManager.getInstance().getTopBalances(limit);
            
            sender.sendMessage("§6=== Top " + limit + " Balances ===");
            for (int i = 0; i < topBalances.size(); i++) {
                Map.Entry<String, Double> entry = topBalances.get(i);
                sender.sendMessage("§e#" + (i + 1) + " §f" + entry.getKey() + " §a$" + String.format("%.2f", entry.getValue()));
            }
        }
    }
    
    @Fallback
    public void fallback(CommandSender sender, List<String> args) {
        sender.sendMessage("§cUnknown economy command. Use '/economy help' for available commands.");
    }
}
```

This comprehensive guide covers all aspects of the TDR MC Core command system. Use it as a reference when implementing commands in your plugins!