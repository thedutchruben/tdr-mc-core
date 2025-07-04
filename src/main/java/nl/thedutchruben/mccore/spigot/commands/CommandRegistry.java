package nl.thedutchruben.mccore.spigot.commands;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.utils.classes.ClassFinder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandRegistry implements CommandExecutor, TabCompleter {
    private Map<String, TdrCommand> commandMap = new HashMap<>();
    public CommandFailureHandler failureHandler = (sender, reason, command, subCommand) -> {};
    private static Map<String, TabComplete> tabCompletable = new HashMap<>();

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        final List<String> completions = new ArrayList<>();
        final Set<String> COMMANDS = new HashSet<>();
        
        TdrCommand tdrCommand = commandMap.get(command.getName());
        if (tdrCommand == null) return completions;

        if (strings.length == 1) {
            // First level - show sub-command groups and direct sub-commands
            completions.add("help");
            
            // Add sub-command groups
            for (String groupName : tdrCommand.getSubCommandGroups().keySet()) {
                TdrSubCommandGroup group = tdrCommand.getSubCommandGroups().get(groupName);
                if (group.getSubCommandGroup().permission().isEmpty() || commandSender.hasPermission(group.getSubCommandGroup().permission())) {
                    COMMANDS.add(groupName);
                }
            }
            
            // Add direct sub-commands for backward compatibility
            for (String subCommandName : tdrCommand.getDirectSubCommands().keySet()) {
                TdrSubCommand subCommand = tdrCommand.getDirectSubCommands().get(subCommandName);
                if (subCommand.getSubCommand().permission().isEmpty() || commandSender.hasPermission(subCommand.getSubCommand().permission())) {
                    COMMANDS.add(subCommandName);
                }
            }
            
            StringUtil.copyPartialMatches(strings[0], COMMANDS, completions);
            
        } else if (strings.length == 2) {
            // Second level - if first arg is a sub-command group, show its sub-commands
            TdrSubCommandGroup group = tdrCommand.getSubCommandGroup(strings[0]);
            if (group != null) {
                for (String subCommandName : group.getSubCommands().keySet()) {
                    TdrSubCommand subCommand = group.getSubCommands().get(subCommandName);
                    if (subCommand.getSubCommand().permission().isEmpty() || commandSender.hasPermission(subCommand.getSubCommand().permission())) {
                        COMMANDS.add(subCommandName);
                    }
                }
                StringUtil.copyPartialMatches(strings[1], COMMANDS, completions);
            }
            
        } else if (strings.length > 2) {
            // Third level and beyond - handle parameters
            TdrSubCommand targetSubCommand = null;
            
            // Check if it's a three-level command
            TdrSubCommandGroup group = tdrCommand.getSubCommandGroup(strings[0]);
            if (group != null) {
                targetSubCommand = group.getSubCommands().get(strings[1]);
            } else {
                // Check if it's a direct sub-command
                targetSubCommand = tdrCommand.getDirectSubCommands().get(strings[0]);
            }
            
            if (targetSubCommand != null) {
                List<String> usageParts = new ArrayList<>();
                String usage = targetSubCommand.getSubCommand().usage();
                if (!usage.isEmpty()) {
                    for (String part : usage.split(" ")) {
                        usageParts.add(part.replace("<", "").replace(">", ""));
                    }
                }
                
                int paramIndex = group != null ? strings.length - 3 : strings.length - 2;
                
                if (paramIndex >= 0 && paramIndex < usageParts.size() && 
                    paramIndex < targetSubCommand.getSubCommand().maxParams()) {
                    
                    String paramType = usageParts.get(paramIndex);
                    TabComplete tabComplete = tabCompletable.get(paramType);
                    if (tabComplete != null) {
                        for (String completion : tabComplete.getCompletions(commandSender)) {
                            COMMANDS.add(completion.replace(" ", "_"));
                        }
                        StringUtil.copyPartialMatches(strings[strings.length - 1], COMMANDS, completions);
                    }
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    public interface CommandFailureHandler {
        void handleFailure(CommandFailReason reason, CommandSender sender, TdrCommand command, TdrSubCommand tdrSubCommand);
    }

    public CommandFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(CommandFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    public CommandRegistry(Mccore mccore) throws InstantiationException, IllegalAccessException {
        scanForCommands(mccore);
    }

    private void scanForCommands(Mccore mccore) throws InstantiationException, IllegalAccessException {
        for (Class<?> mainClass : new ClassFinder().getClasses(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1])) {
            if (mainClass.isAnnotationPresent(nl.thedutchruben.mccore.spigot.commands.Command.class)) {
                processCommandClass(mccore, mainClass);
            }
        }
    }

    private void processCommandClass(Mccore mccore, Class<?> mainClass) throws InstantiationException, IllegalAccessException {
        nl.thedutchruben.mccore.spigot.commands.Command commandAnnotation = 
            mainClass.getAnnotation(nl.thedutchruben.mccore.spigot.commands.Command.class);
        
        TdrCommand tdrCommand = new TdrCommand(commandAnnotation);
        Object mainInstance = mainClass.newInstance();

        // Register the plugin command
        PluginCommand basePluginCommand = mccore.getJavaPlugin().getServer().getPluginCommand(commandAnnotation.command());
        if (basePluginCommand != null) {
            basePluginCommand.setDescription(commandAnnotation.description());
            basePluginCommand.setAliases(Arrays.asList(commandAnnotation.aliases()));
            basePluginCommand.setExecutor(this);
            basePluginCommand.setTabCompleter(this);
        }

        // Process methods in the main class (direct sub-commands, default, fallback)
        processMethodsInClass(mainClass, mainInstance, tdrCommand, null);

        // Process nested classes (sub-command groups)
        for (Class<?> nestedClass : mainClass.getDeclaredClasses()) {
            if (nestedClass.isAnnotationPresent(SubCommandGroup.class)) {
                processSubCommandGroup(nestedClass, tdrCommand);
            }
        }

        commandMap.put(commandAnnotation.command(), tdrCommand);
    }

    private void processSubCommandGroup(Class<?> nestedClass, TdrCommand tdrCommand) throws InstantiationException, IllegalAccessException {
        SubCommandGroup groupAnnotation = nestedClass.getAnnotation(SubCommandGroup.class);
        Object groupInstance = nestedClass.newInstance();
        
        TdrSubCommandGroup subCommandGroup = new TdrSubCommandGroup(groupAnnotation, groupInstance);
        
        // Process methods in the nested class
        processMethodsInClass(nestedClass, groupInstance, tdrCommand, subCommandGroup);
        
        tdrCommand.addSubCommandGroup(groupAnnotation.value(), subCommandGroup);
    }

    private void processMethodsInClass(Class<?> clazz, Object instance, TdrCommand tdrCommand, TdrSubCommandGroup subCommandGroup) {
        for (Method method : clazz.getMethods()) {
            // Process SubCommand annotation
            SubCommand subCommandAnnotation = method.getAnnotation(SubCommand.class);
            if (subCommandAnnotation != null) {
                TdrSubCommand tdrSubCommand = new TdrSubCommand(method, instance, subCommandAnnotation);
                
                if (subCommandGroup != null) {
                    // Add to sub-command group
                    subCommandGroup.addSubCommand(subCommandAnnotation.subCommand(), tdrSubCommand);
                } else {
                    // Add as direct sub-command
                    tdrCommand.addDirectSubCommand(subCommandAnnotation.subCommand(), tdrSubCommand);
                }
            }

            // Process Default annotation (only for main class)
            if (subCommandGroup == null) {
                Default defaultAnnotation = method.getAnnotation(Default.class);
                if (defaultAnnotation != null) {
                    tdrCommand.setaDefault(defaultAnnotation);
                    tdrCommand.setDefaultCommand(new TdrSubCommand(method, instance, null));
                }

                // Process Fallback annotation (only for main class)
                Fallback fallbackAnnotation = method.getAnnotation(Fallback.class);
                if (fallbackAnnotation != null) {
                    tdrCommand.setaFallback(fallbackAnnotation);
                    tdrCommand.setFallBackCommand(new TdrSubCommand(method, instance, null));
                }
            }
        }
    }

    public enum CommandFailReason {
        INSUFFICIENT_PARAMETER,
        REDUNDANT_PARAMETER,
        NO_PERMISSION,
        NOT_PLAYER,
        COMMAND_NOT_FOUND,
        REFLECTION_ERROR
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TdrCommand tdrCommand = commandMap.get(command.getName());
        if (tdrCommand == null) {
            failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, null, null);
            return false;
        }

        // Check main command permission
        nl.thedutchruben.mccore.spigot.commands.Command commandData = tdrCommand.getCommand();
        if (!commandData.permission().equals("") && !sender.hasPermission(commandData.permission())) {
            failureHandler.handleFailure(CommandFailReason.NO_PERMISSION, sender, tdrCommand, null);
            return true;
        }

        // Handle help command
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            displayHelp(sender, tdrCommand);
            return true;
        }

        // Handle no arguments (default command)
        if (args.length == 0) {
            if (tdrCommand.getDefaultCommand() != null) {
                return executeSubCommand(sender, tdrCommand, tdrCommand.getDefaultCommand(), new String[0]);
            }
            failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, tdrCommand, null);
            return true;
        }

        // Handle single argument
        if (args.length == 1) {
            // Check if it's a sub-command group
            TdrSubCommandGroup group = tdrCommand.getSubCommandGroup(args[0]);
            if (group != null) {
                // Show help for this sub-command group
                displaySubCommandGroupHelp(sender, tdrCommand, group);
                return true;
            }
            
            // Check if it's a direct sub-command
            TdrSubCommand directSubCommand = tdrCommand.getDirectSubCommands().get(args[0]);
            if (directSubCommand != null) {
                return executeSubCommand(sender, tdrCommand, directSubCommand, Arrays.copyOfRange(args, 1, args.length));
            }
            
            // Check fallback
            if (tdrCommand.getFallBackCommand() != null) {
                return executeSubCommand(sender, tdrCommand, tdrCommand.getFallBackCommand(), args);
            }
            
            failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, tdrCommand, null);
            return true;
        }

        // Handle two or more arguments
        if (args.length >= 2) {
            // Check if first argument is a sub-command group
            TdrSubCommandGroup group = tdrCommand.getSubCommandGroup(args[0]);
            if (group != null) {
                TdrSubCommand subCommand = group.getSubCommands().get(args[1]);
                if (subCommand != null) {
                    return executeSubCommand(sender, tdrCommand, subCommand, Arrays.copyOfRange(args, 2, args.length));
                }
                failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, tdrCommand, null);
                return true;
            }
            
            // Check if first argument is a direct sub-command
            TdrSubCommand directSubCommand = tdrCommand.getDirectSubCommands().get(args[0]);
            if (directSubCommand != null) {
                return executeSubCommand(sender, tdrCommand, directSubCommand, Arrays.copyOfRange(args, 1, args.length));
            }
            
            // Check fallback
            if (tdrCommand.getFallBackCommand() != null) {
                return executeSubCommand(sender, tdrCommand, tdrCommand.getFallBackCommand(), args);
            }
        }

        failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, tdrCommand, null);
        return true;
    }

    private boolean executeSubCommand(CommandSender sender, TdrCommand tdrCommand, TdrSubCommand subCommand, String[] args) {
        SubCommand subCommandAnnotation = subCommand.getSubCommand();
        
        // Check permission
        if (subCommandAnnotation != null && !subCommandAnnotation.permission().isEmpty() && !sender.hasPermission(subCommandAnnotation.permission())) {
            failureHandler.handleFailure(CommandFailReason.NO_PERMISSION, sender, tdrCommand, subCommand);
            return true;
        }

        // Parse parameters
        List<String> params = parseParameters(args);

        // Check parameter count
        if (subCommandAnnotation != null) {
            if (params.size() < subCommandAnnotation.minParams() || params.size() > subCommandAnnotation.maxParams()) {
                failureHandler.handleFailure(CommandFailReason.INSUFFICIENT_PARAMETER, sender, tdrCommand, subCommand);
                return true;
            }
        }

        // Execute the command
        try {
            subCommand.getMethod().invoke(subCommand.getInstance(), sender, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            failureHandler.handleFailure(CommandFailReason.REFLECTION_ERROR, sender, tdrCommand, subCommand);
        }

        return true;
    }

    private List<String> parseParameters(String[] args) {
        List<String> params = new ArrayList<>();
        StringBuilder combine = new StringBuilder();
        boolean combineNext = false;

        for (String param : args) {
            if (param.startsWith("\"") && param.endsWith("\"")) {
                params.add(param.replace("\"", ""));
            } else if (param.startsWith("\"")) {
                combineNext = true;
                combine.append(param);
            } else if (param.endsWith("\"")) {
                combineNext = false;
                combine.append(" ").append(param);
                params.add(combine.toString().replace("\"", ""));
                combine = new StringBuilder();
            } else {
                if (combineNext) {
                    combine.append(" ").append(param);
                } else {
                    params.add(param);
                }
            }
        }

        return params;
    }

    private void displayHelp(CommandSender sender, TdrCommand tdrCommand) {
        nl.thedutchruben.mccore.spigot.commands.Command commandData = tdrCommand.getCommand();
        sender.sendMessage(ChatColor.GOLD + "----------" + ChatColor.WHITE + "Help : " + commandData.command() + ChatColor.GOLD + " ----------");
        
        // Show default command
        if (tdrCommand.getDefaultCommand() != null) {
            sender.sendMessage(ChatColor.GOLD + "/" + commandData.command() + " " + ChatColor.GRAY + " : " + ChatColor.WHITE + "Default command");
        }
        
        // Show direct sub-commands
        for (Map.Entry<String, TdrSubCommand> entry : tdrCommand.getDirectSubCommands().entrySet()) {
            TdrSubCommand subCommand = entry.getValue();
            sender.sendMessage(ChatColor.GOLD + "/" + commandData.command() + " " + ChatColor.WHITE + entry.getKey() + 
                             " " + ChatColor.GRAY + subCommand.getSubCommand().usage() + " : " + ChatColor.WHITE + subCommand.getSubCommand().description());
        }
        
        // Show sub-command groups
        for (Map.Entry<String, TdrSubCommandGroup> entry : tdrCommand.getSubCommandGroups().entrySet()) {
            TdrSubCommandGroup group = entry.getValue();
            sender.sendMessage(ChatColor.GOLD + "/" + commandData.command() + " " + ChatColor.WHITE + entry.getKey() + 
                             " " + ChatColor.GRAY + "<subcommand> : " + ChatColor.WHITE + group.getSubCommandGroup().description());
        }
    }

    private void displaySubCommandGroupHelp(CommandSender sender, TdrCommand tdrCommand, TdrSubCommandGroup group) {
        nl.thedutchruben.mccore.spigot.commands.Command commandData = tdrCommand.getCommand();
        sender.sendMessage(ChatColor.GOLD + "----------" + ChatColor.WHITE + "Help : " + commandData.command() + " " + 
                         group.getSubCommandGroup().value() + ChatColor.GOLD + " ----------");
        
        for (Map.Entry<String, TdrSubCommand> entry : group.getSubCommands().entrySet()) {
            TdrSubCommand subCommand = entry.getValue();
            sender.sendMessage(ChatColor.GOLD + "/" + commandData.command() + " " + group.getSubCommandGroup().value() + " " + 
                             ChatColor.WHITE + entry.getKey() + " " + ChatColor.GRAY + subCommand.getSubCommand().usage() + 
                             " : " + ChatColor.WHITE + subCommand.getSubCommand().description());
        }
    }

    public static Map<String, TabComplete> getTabCompletable() {
        return tabCompletable;
    }
}
