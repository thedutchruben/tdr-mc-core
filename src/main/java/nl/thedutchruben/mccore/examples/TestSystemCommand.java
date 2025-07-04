package nl.thedutchruben.mccore.examples;

import nl.thedutchruben.mccore.spigot.commands.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Example command demonstrating the three-level command system
 * 
 * This creates commands like:
 * /testsystem - Default command
 * /testsystem help - Auto-generated help
 * /testsystem version - Direct sub-command
 * /testsystem user - Shows user sub-command help
 * /testsystem user create player1 test@example.com - Creates a user
 * /testsystem admin reload - Reloads the plugin
 */
@Command(command = "testsystem", description = "Test the three-level command system", permission = "testsystem.use")
public class TestSystemCommand {

    @Default(permission = "testsystem.default")
    public void defaultCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("§a=== Test System Command ===");
        sender.sendMessage("§bThis is the default command!");
        sender.sendMessage("§eUse /testsystem help for more options");
    }

    @Fallback(permission = "testsystem.fallback")
    public void fallbackCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("§cUnknown command. Use /testsystem help for help.");
    }

    // Direct sub-command for backward compatibility testing
    @SubCommand(subCommand = "version", description = "Show version information", permission = "testsystem.version")
    public void versionCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("§bTest System v1.0.0");
        sender.sendMessage("§fThree-level command system working!");
    }

    // Sub-command group for user management
    @SubCommandGroup(value = "user", description = "User management commands", permission = "testsystem.user")
    public static class UserCommands {

        @SubCommand(subCommand = "create", description = "Create a new user", permission = "testsystem.user.create", 
                   usage = "<username> <email>", minParams = 2, maxParams = 2)
        public void createUser(CommandSender sender, List<String> args) {
            String username = args.get(0);
            String email = args.get(1);
            sender.sendMessage("§aCreated user: " + username + " with email: " + email);
        }

        @SubCommand(subCommand = "delete", description = "Delete a user", permission = "testsystem.user.delete", 
                   usage = "<username>", minParams = 1, maxParams = 1)
        public void deleteUser(CommandSender sender, List<String> args) {
            String username = args.get(0);
            sender.sendMessage("§cDeleted user: " + username);
        }

        @SubCommand(subCommand = "list", description = "List all users", permission = "testsystem.user.list")
        public void listUsers(CommandSender sender, List<String> args) {
            sender.sendMessage("§eUser list:");
            sender.sendMessage("§f- player1");
            sender.sendMessage("§f- player2");
            sender.sendMessage("§f- player3");
        }

        @SubCommand(subCommand = "info", description = "Show user information", permission = "testsystem.user.info", 
                   usage = "<username>", minParams = 1, maxParams = 1)
        public void userInfo(CommandSender sender, List<String> args) {
            String username = args.get(0);
            sender.sendMessage("§bUser info for: " + username);
            sender.sendMessage("§fEmail: " + username + "@example.com");
            sender.sendMessage("§fJoined: 2024-01-01");
        }
    }

    // Sub-command group for admin tools
    @SubCommandGroup(value = "admin", description = "Administrative commands", permission = "testsystem.admin")
    public static class AdminCommands {

        @SubCommand(subCommand = "reload", description = "Reload the plugin", permission = "testsystem.admin.reload")
        public void reloadPlugin(CommandSender sender, List<String> args) {
            sender.sendMessage("§aPlugin reloaded successfully!");
        }

        @SubCommand(subCommand = "stats", description = "Show plugin statistics", permission = "testsystem.admin.stats")
        public void showStats(CommandSender sender, List<String> args) {
            sender.sendMessage("§bPlugin Statistics:");
            sender.sendMessage("§fUsers: 42");
            sender.sendMessage("§fCommands executed: 1337");
            sender.sendMessage("§fUptime: 5 hours");
        }

        @SubCommand(subCommand = "backup", description = "Create a backup", permission = "testsystem.admin.backup", 
                   usage = "[filename]", minParams = 0, maxParams = 1)
        public void createBackup(CommandSender sender, List<String> args) {
            String filename = args.isEmpty() ? "backup-" + System.currentTimeMillis() : args.get(0);
            sender.sendMessage("§aCreating backup: " + filename + ".zip");
        }
    }

    // Sub-command group for player-only commands
    @SubCommandGroup(value = "player", description = "Player-only commands", permission = "testsystem.player")
    public static class PlayerCommands {

        @SubCommand(subCommand = "info", description = "Show player info", permission = "testsystem.player.info")
        public void playerInfoCommand(CommandSender sender, List<String> args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players!");
                return;
            }
            
            Player player = (Player) sender;
            sender.sendMessage("§bPlayer Information:");
            sender.sendMessage("§fName: " + player.getName());
            sender.sendMessage("§fUUID: " + player.getUniqueId());
            sender.sendMessage("§fWorld: " + player.getWorld().getName());
            sender.sendMessage("§fLocation: " + (int)player.getLocation().getX() + ", " + 
                             (int)player.getLocation().getY() + ", " + (int)player.getLocation().getZ());
        }

        @SubCommand(subCommand = "heal", description = "Heal the player", permission = "testsystem.player.heal")
        public void healCommand(CommandSender sender, List<String> args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players!");
                return;
            }
            
            Player player = (Player) sender;
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            sender.sendMessage("§aYou have been healed!");
        }
    }
}
