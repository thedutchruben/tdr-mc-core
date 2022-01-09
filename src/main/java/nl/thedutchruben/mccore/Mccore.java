package nl.thedutchruben.mccore;


import nl.thedutchruben.mccore.commands.CommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;


public final class Mccore {
    private JavaPlugin javaPlugin;

    public Mccore(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        try {
            new CommandRegistry(this);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }
}
