package nl.thedutchruben.mccore.commands;

import nl.thedutchruben.mccore.Mccore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandRegistry implements CommandExecutor {
    private Mccore mccore;
    private Map<String, TdrCommand> commandMap = new HashMap<>();
    public CommandFailureHandler failureHandler = (sender, reason, command) -> {};

    public interface CommandFailureHandler {
        void handleFailure(CommandFailReason reason, CommandSender sender, TdrCommand command);
    }


    public CommandFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(CommandFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    public CommandRegistry(Mccore mccore) throws InstantiationException, IllegalAccessException {
        this.mccore = mccore;
        Reflections reflections = new Reflections("nl.thedutchruben");
        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(nl.thedutchruben.mccore.commands.Command.class);

        for (Class<?> allClass : allClasses) {

            nl.thedutchruben.mccore.commands.Command an = allClass.getAnnotation(nl.thedutchruben.mccore.commands.Command.class);
            TdrCommand tdrCommand = new TdrCommand(an);
            for (Method method : allClass.getMethods()) {
                SubCommand annotation = method.getAnnotation(SubCommand.class);

                if (annotation != null) {
                    PluginCommand basePluginCommand = mccore.getJavaPlugin().getServer().getPluginCommand(an.command());
                    basePluginCommand.setDescription(an.description());
                    basePluginCommand.setAliases(Arrays.asList(an.aliases()));
                    basePluginCommand.setExecutor(this);
                    tdrCommand.getSubCommand().put(annotation.subCommand(),new TdrSubCommand(method,allClass.newInstance(),annotation));
                }
            }
            commandMap.put(an.command(),tdrCommand);

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
                        failureHandler.handleFailure(CommandFailReason.NO_PERMISSION, sender, wrapper);
                        return true;
                    }

                    TdrSubCommand annotation = wrapper.getSubCommand().get(args[0]);
                    if(annotation == null){
                        annotation = wrapper.getSubCommand().get("");
                    }

                    if (!annotation.getSubCommand().permission().equals("") && !sender.hasPermission(annotation.getSubCommand().permission())) {
                        failureHandler.handleFailure(CommandFailReason.NO_PERMISSION, sender, wrapper);
                        return true;
                    }

                    String[] actualParams = Arrays.copyOfRange(args, (annotation.getSubCommand().subCommand()).split(" ").length - 1, args.length);
                    System.out.println(Arrays.toString(actualParams));
                    try {

                        annotation.getMethod().invoke(annotation.getInstance(), sender, actualParams);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }

        // If we go here, there are no registered commands matching player's input
        failureHandler.handleFailure(CommandFailReason.COMMAND_NOT_FOUND, sender, null);
        return false;
    }
}
