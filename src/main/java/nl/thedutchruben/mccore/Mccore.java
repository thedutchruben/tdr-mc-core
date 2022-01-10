package nl.thedutchruben.mccore;


import nl.thedutchruben.mccore.commands.CommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;


public final class Mccore {
    private JavaPlugin javaPlugin;

    public Mccore(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        try {
            CommandRegistry commandRegistry = new CommandRegistry(this);
            commandRegistry.setFailureHandler((reason, sender, command) -> {
                switch (reason) {
                    case COMMAND_NOT_FOUND:
                        sender.sendMessage("Can't find the command. Please check your spellings!");            break;
                }
            });
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }
}
