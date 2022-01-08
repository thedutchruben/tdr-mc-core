package nl.thedutchruben.mccore.commands;

import nl.thedutchruben.mccore.Mccore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry implements CommandExecutor {
    private Mccore mccore;
    private Map<String, TdrCommand> commandMap = new HashMap<>();
    private CommandFailureHandler failureHandler = (sender, reason, command) -> {};

    public interface CommandFailureHandler {
        void handleFailure(CommandFailReason reason, CommandSender sender, TdrCommand command);
    }

    public void registerCommands(Object object) {
        for (Method method : object.getClass().getMethods()) {
            nl.thedutchruben.mccore.commands.Command annotation = method.getAnnotation(nl.thedutchruben.mccore.commands.Command.class);

            if (annotation != null) {
                String base = annotation.command().split(" ")[0].substring(1);
                PluginCommand basePluginCommand = mccore.getJavaPlugin().getServer().getPluginCommand(base);

                basePluginCommand.setExecutor(this);
                commandMap.put(annotation.command().substring(1), new TdrCommand(method, object, annotation));
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
