package nl.thedutchruben.mccore.commands;

import nl.thedutchruben.mccore.Mccore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandRegistry implements CommandExecutor {
    private Mccore mccore;
    private Map<String, TdrCommand> commandMap = new HashMap<>();
    private CommandFailureHandler failureHandler = (sender, reason, command) -> {};

    public interface CommandFailureHandler {
        void handleFailure(CommandFailReason reason, CommandSender sender, TdrCommand command);
    }

    public CommandRegistry() {
        Reflections reflections = new Reflections("nl.thedutchruben");
        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
        for (Class<?> allClass : allClasses) {
            nl.thedutchruben.mccore.commands.Command an = allClasses.getClass().getAnnotation(nl.thedutchruben.mccore.commands.Command.class);
            if(an != null){
                for (Method method : allClass.getMethods()) {
                    nl.thedutchruben.mccore.commands.SubCommand annotation = method.getAnnotation(nl.thedutchruben.mccore.commands.SubCommand.class);

                    if (annotation != null) {
                        PluginCommand basePluginCommand = mccore.getJavaPlugin().getServer().getPluginCommand(an.command());

                        basePluginCommand.setExecutor(this);
                        commandMap.put(annotation.subCommand(), new TdrCommand(method, allClass, an,annotation));
                    }
                }
            }

        }

    }


enum CommandFailReason {
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
        return false;
    }
}
