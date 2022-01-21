package nl.thedutchruben.mccore.commands;

import lombok.SneakyThrows;
import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.utils.classes.ClassFinder;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class CommandRegistry implements CommandExecutor, TabCompleter {
    private Mccore mccore;
    private Map<String, TdrCommand> commandMap = new HashMap<>();
    public CommandFailureHandler failureHandler = (sender, reason, command,subCommand) -> {};

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        final List<String> completions = new ArrayList<>();
        final Set<String> COMMANDS = new HashSet<>();

        StringBuilder sb = new StringBuilder();
        for (int i = -1; i <= strings.length - 1; i++) {
            if (i == -1)
                sb.append(command.getName().toLowerCase());
            else
                sb.append(" ").append(strings[i].toLowerCase());

            for (String usage : commandMap.keySet()) {

                if (usage.equals(sb.toString())) {

                    TdrCommand wrapper = commandMap.get(usage);
                    wrapper.getSubCommand().forEach((s1, subCommand) -> {
                        if(commandSender.hasPermission(subCommand.getSubCommand().permission())){
                            COMMANDS.add(s1);
                        }
                    });
                }
            }
        }

        StringUtil.copyPartialMatches(strings[0], COMMANDS, completions);
        Collections.sort(completions);
        return completions;
    }

    public interface CommandFailureHandler {
        void handleFailure(CommandFailReason reason, CommandSender sender, TdrCommand command,TdrSubCommand tdrSubCommand);
    }


    public CommandFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(CommandFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    @SneakyThrows
    public CommandRegistry(Mccore mccore) throws InstantiationException, IllegalAccessException {
        this.mccore = mccore;

        for (Class<?> allClass : new ClassFinder().findClasses(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1])) {
            System.out.println(allClass.getName());
            if(allClass.isAnnotationPresent(nl.thedutchruben.mccore.commands.Command.class)){
                nl.thedutchruben.mccore.commands.Command an = allClass.getAnnotation(nl.thedutchruben.mccore.commands.Command.class);
                TdrCommand tdrCommand = new TdrCommand(an);
                for (Method method : allClass.getMethods()) {
                    SubCommand annotation = method.getAnnotation(SubCommand.class);

                    if (annotation != null) {

                        PluginCommand basePluginCommand = mccore.getJavaPlugin().getServer().getPluginCommand(an.command());
                        basePluginCommand.setDescription(an.description());
                        basePluginCommand.setAliases(Arrays.asList(an.aliases()));
                        basePluginCommand.setExecutor(this);
                        basePluginCommand.setTabCompleter(this);
                        tdrCommand.getSubCommand().put(annotation.subCommand(),new TdrSubCommand(method,allClass.newInstance(),annotation));
                    }
                    Default aDefault = method.getAnnotation(Default.class);

                    if (aDefault != null) {
                        tdrCommand.setaDefault(aDefault);
                        tdrCommand.setDefaultCommand(new TdrSubCommand(method,allClass.newInstance(),annotation));
                    }

                }
                commandMap.put(an.command(),tdrCommand);
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

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = -1; i <= args.length - 1; i++) {
            if (i == -1)
                sb.append(command.getName().toLowerCase());
            else
                sb.append(" ").append(args[i].toLowerCase());

            for (String usage : commandMap.keySet()) {

                if (usage.equals(sb.toString())) {

                    TdrCommand wrapper = commandMap.get(usage);
                    nl.thedutchruben.mccore.commands.Command commanddata =  wrapper.getCommand();

                    if (!commanddata.permission().equals("") && !sender.hasPermission(commanddata.permission())) {
                        failureHandler.handleFailure(CommandFailReason.NO_PERMISSION, sender, wrapper,null);
                        return true;
                    }


                    TdrSubCommand annotation = null;
                    if(args.length == 0){
                        annotation = wrapper.getDefaultCommand();
                    }else{
                        annotation = wrapper.getSubCommand().get(args[0]);
                    }

                    if(annotation == null){
                        failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, wrapper,null);
                        return true;
                    }



                    if (!annotation.getSubCommand().permission().equals("") && !sender.hasPermission(annotation.getSubCommand().permission())) {
                        failureHandler.handleFailure(CommandFailReason.NO_PERMISSION, sender, wrapper,annotation);
                        return true;
                    }

                    String[] actualParams = Arrays.copyOfRange(args, (annotation.getSubCommand().subCommand()).split(" ").length - 1, args.length);

                    if(annotation.getSubCommand().params() != 0){
                        if(actualParams.length != annotation.getSubCommand().params()){
                            failureHandler.handleFailure(CommandFailReason.INSUFFICIENT_PARAMETER, sender, wrapper,annotation);
                            return true;
                        }
                    }
                    try {

                        annotation.getMethod().invoke(annotation.getInstance(), sender, actualParams);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }

        failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, null,null);
        return false;
    }
}
