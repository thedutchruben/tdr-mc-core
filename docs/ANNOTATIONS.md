# Annotation Reference

This guide provides comprehensive documentation for all annotations available in the TDR MC Core command system.

## Overview

TDR MC Core uses a powerful annotation-based command system that allows you to create complex command hierarchies with minimal code. The system supports three levels of commands:

1. **Main Commands** (`@Command`) - The root command
2. **Sub-Command Groups** (`@SubCommandGroup`) - Logical groupings of related sub-commands  
3. **Sub-Commands** (`@SubCommand`) - Individual command actions

## Core Annotations

### @Command

The `@Command` annotation defines a main command and is applied to classes.

**Target:** `Class`  
**Required:** Yes (for command classes)

#### Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `command` | `String` | ✓ | - | The name of the command (e.g., "mycommand" for `/mycommand`) |
| `description` | `String` | ✗ | `""` | Description shown in help messages |
| `permission` | `String` | ✗ | `""` | Base permission required to use the command |
| `console` | `boolean` | ✗ | `false` | Whether the command can be executed from console |
| `aliases` | `String[]` | ✗ | `{}` | Alternative names for the command |

#### Example

```java
@Command(
    command = "myplugin",
    description = "Main plugin command",
    permission = "myplugin.use",
    console = true,
    aliases = {"mp", "plugin"}
)
public class MyPluginCommand {
    // Command methods go here
}
```

### @SubCommand

The `@SubCommand` annotation defines individual sub-commands and is applied to methods.

**Target:** `Method`  
**Method Signature:** `public void methodName(CommandSender sender, List<String> args)`

#### Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `subCommand` | `String` | ✓ | - | The name of the sub-command |
| `description` | `String` | ✗ | `""` | Description shown in help messages |
| `permission` | `String` | ✗ | `""` | Permission required for this sub-command |
| `usage` | `String` | ✗ | `""` | Usage string shown in help (e.g., "<player> <amount>") |
| `minParams` | `int` | ✗ | `0` | Minimum number of parameters required |
| `maxParams` | `int` | ✗ | `0` | Maximum number of parameters allowed |
| `console` | `boolean` | ✗ | `false` | Whether this sub-command can be executed from console |

#### Example

```java
@SubCommand(
    subCommand = "reload",
    description = "Reload the plugin configuration",
    permission = "myplugin.reload",
    usage = "[config-file]",
    minParams = 0,
    maxParams = 1,
    console = true
)
public void reloadCommand(CommandSender sender, List<String> args) {
    // Implementation
}
```

### @SubCommandGroup

The `@SubCommandGroup` annotation defines a group of related sub-commands and is applied to nested static classes.

**Target:** `Class` (nested static classes only)

#### Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `value` | `String` | ✓ | - | The name of the sub-command group |
| `description` | `String` | ✗ | `""` | Description shown in help messages |
| `permission` | `String` | ✗ | `""` | Base permission required for the group |

#### Example

```java
@SubCommandGroup(
    value = "admin",
    description = "Administrative commands",
    permission = "myplugin.admin"
)
public static class AdminCommands {
    
    @SubCommand(subCommand = "reload", description = "Reload the plugin")
    public void reload(CommandSender sender, List<String> args) {
        // Implementation
    }
    
    @SubCommand(subCommand = "debug", description = "Toggle debug mode")
    public void debug(CommandSender sender, List<String> args) {
        // Implementation
    }
}
```

### @Default

The `@Default` annotation defines the default command that executes when no sub-command is specified.

**Target:** `Method`  
**Method Signature:** `public void methodName(CommandSender sender, List<String> args)`

#### Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `permission` | `String` | ✗ | `""` | Permission required for the default command |
| `console` | `boolean` | ✗ | `false` | Whether the default command can be executed from console |
| `usage` | `String` | ✗ | `""` | Usage string for the default command |
| `minParams` | `int` | ✗ | `0` | Minimum parameters for default command |
| `maxParams` | `int` | ✗ | `0` | Maximum parameters for default command |

#### Example

```java
@Default(
    permission = "myplugin.default",
    console = true,
    usage = "[option]",
    minParams = 0,
    maxParams = 1
)
public void defaultCommand(CommandSender sender, List<String> args) {
    sender.sendMessage("Welcome to MyPlugin!");
    sender.sendMessage("Use '/myplugin help' for available commands.");
}
```

### @Fallback

The `@Fallback` annotation defines a fallback command that executes when an unknown sub-command is used.

**Target:** `Method`  
**Method Signature:** `public void methodName(CommandSender sender, List<String> args)`

#### Attributes

| Attribute | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `permission` | `String` | ✗ | `""` | Permission required for the fallback command |
| `console` | `boolean` | ✗ | `false` | Whether the fallback command can be executed from console |
| `usage` | `String` | ✗ | `""` | Usage string for the fallback command |
| `minParams` | `int` | ✗ | `0` | Minimum parameters for fallback command |
| `maxParams` | `int` | ✗ | `0` | Maximum parameters for fallback command |

#### Example

```java
@Fallback(permission = "myplugin.use")
public void fallbackCommand(CommandSender sender, List<String> args) {
    sender.sendMessage("§cUnknown command. Use '/myplugin help' for help.");
}
```

## Command Structure Examples

### Simple Command

```java
@Command(command = "hello", description = "Say hello")
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
@Command(command = "economy", description = "Economy management", permission = "economy.use")
public class EconomyCommand {
    
    @Default
    public void defaultCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("Use '/economy help' for available commands.");
    }
    
    @SubCommand(subCommand = "balance", description = "Check your balance")
    public void balance(CommandSender sender, List<String> args) {
        // Show balance
    }
    
    @SubCommand(
        subCommand = "pay", 
        description = "Pay another player", 
        usage = "<player> <amount>",
        minParams = 2,
        maxParams = 2
    )
    public void pay(CommandSender sender, List<String> args) {
        String targetPlayer = args.get(0);
        double amount = Double.parseDouble(args.get(1));
        // Process payment
    }
}
```

**Usage:** 
- `/economy` - Default command
- `/economy balance` - Check balance
- `/economy pay Steve 100` - Pay Steve 100

### Three-Level Command Hierarchy

```java
@Command(command = "guild", description = "Guild management system")
public class GuildCommand {
    
    @Default
    public void defaultCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("Guild System v1.0");
    }
    
    // Direct sub-command
    @SubCommand(subCommand = "list", description = "List all guilds")
    public void listGuilds(CommandSender sender, List<String> args) {
        // List guilds
    }
    
    // Member management group
    @SubCommandGroup(value = "member", description = "Member management", permission = "guild.member")
    public static class MemberCommands {
        
        @SubCommand(subCommand = "invite", description = "Invite a player", usage = "<player>", minParams = 1, maxParams = 1)
        public void invite(CommandSender sender, List<String> args) {
            // Invite player
        }
        
        @SubCommand(subCommand = "kick", description = "Kick a member", usage = "<player>", minParams = 1, maxParams = 1)
        public void kick(CommandSender sender, List<String> args) {
            // Kick member
        }
        
        @SubCommand(subCommand = "promote", description = "Promote a member", usage = "<player>", minParams = 1, maxParams = 1)
        public void promote(CommandSender sender, List<String> args) {
            // Promote member
        }
    }
    
    // Admin commands group
    @SubCommandGroup(value = "admin", description = "Admin commands", permission = "guild.admin")
    public static class AdminCommands {
        
        @SubCommand(subCommand = "create", description = "Create a guild", usage = "<name>", minParams = 1, maxParams = 1)
        public void create(CommandSender sender, List<String> args) {
            // Create guild
        }
        
        @SubCommand(subCommand = "delete", description = "Delete a guild", usage = "<name>", minParams = 1, maxParams = 1)
        public void delete(CommandSender sender, List<String> args) {
            // Delete guild
        }
    }
}
```

**Usage:**
- `/guild` - Default command
- `/guild list` - List all guilds
- `/guild member invite Steve` - Invite Steve to guild
- `/guild admin create Warriors` - Create Warriors guild

## Parameter Handling

### Parameter Types

The `args` parameter in command methods is a `List<String>` containing the command arguments. Parameters are automatically parsed and support quoted strings:

```bash
/command param1 param2 "quoted parameter" param4
```

Results in: `["param1", "param2", "quoted parameter", "param4"]`

### Parameter Validation

Use `minParams` and `maxParams` to automatically validate parameter counts:

```java
@SubCommand(
    subCommand = "teleport",
    usage = "<player> [world] [x] [y] [z]",
    minParams = 1,  // At least player name required
    maxParams = 5   // Player, world, x, y, z maximum
)
public void teleport(CommandSender sender, List<String> args) {
    String playerName = args.get(0);
    
    if (args.size() >= 5) {
        // Teleport to coordinates in world
        String world = args.get(1);
        double x = Double.parseDouble(args.get(2));
        double y = Double.parseDouble(args.get(3));
        double z = Double.parseDouble(args.get(4));
    } else {
        // Teleport to player
    }
}
```

## Permission System

### Hierarchical Permissions

Permissions work hierarchically:

1. **Command Permission** - Required to use any part of the command
2. **Group Permission** - Required to access sub-command groups
3. **Sub-Command Permission** - Required for specific sub-commands

```java
@Command(command = "manage", permission = "plugin.manage")
public class ManageCommand {
    
    @SubCommandGroup(value = "users", permission = "plugin.manage.users")
    public static class UserCommands {
        
        @SubCommand(subCommand = "ban", permission = "plugin.manage.users.ban")
        public void ban(CommandSender sender, List<String> args) {
            // Requires: plugin.manage AND plugin.manage.users AND plugin.manage.users.ban
        }
    }
}
```

### Console Access

Use the `console` attribute to control console access:

```java
@SubCommand(
    subCommand = "location",
    description = "Get your current location",
    console = false  // Players only
)
public void location(CommandSender sender, List<String> args) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("This command is for players only!");
        return;
    }
    
    Player player = (Player) sender;
    // Show location
}
```

## Tab Completion

TDR MC Core provides built-in tab completion for common parameter types:

- `player` - Online player names
- `uuid` - Player UUIDs
- `world` - World names
- `material` - Material types
- `entitytype` - Entity types
- `permission` - Available permissions
- `color` - Chat colors
- `plugin` - Loaded plugins

Use these in your `usage` strings:

```java
@SubCommand(
    subCommand = "give",
    usage = "<player> <material> [amount]",
    minParams = 2,
    maxParams = 3
)
public void give(CommandSender sender, List<String> args) {
    // Tab completion will show players for first param, materials for second
}
```

## Best Practices

### Command Organization

1. **Use meaningful names** for commands and sub-commands
2. **Group related functionality** using `@SubCommandGroup`
3. **Provide clear descriptions** for help messages
4. **Set appropriate permissions** for security

### Method Naming

Follow consistent naming conventions:

```java
@SubCommand(subCommand = "reload")
public void reloadCommand(CommandSender sender, List<String> args) { }

@SubCommand(subCommand = "save-data")
public void saveDataCommand(CommandSender sender, List<String> args) { }
```

### Parameter Validation

Always validate parameters before use:

```java
@SubCommand(subCommand = "setlevel", usage = "<player> <level>", minParams = 2, maxParams = 2)
public void setLevel(CommandSender sender, List<String> args) {
    String playerName = args.get(0);
    
    try {
        int level = Integer.parseInt(args.get(1));
        if (level < 0 || level > 100) {
            sender.sendMessage("Level must be between 0 and 100!");
            return;
        }
        // Process command
    } catch (NumberFormatException e) {
        sender.sendMessage("Invalid level number!");
    }
}
```

### Error Handling

Provide helpful error messages:

```java
@SubCommand(subCommand = "teleport", usage = "<player>", minParams = 1, maxParams = 1)
public void teleport(CommandSender sender, List<String> args) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("§cOnly players can use this command!");
        return;
    }
    
    Player target = Bukkit.getPlayer(args.get(0));
    if (target == null) {
        sender.sendMessage("§cPlayer '" + args.get(0) + "' not found!");
        return;
    }
    
    // Process teleport
}
```