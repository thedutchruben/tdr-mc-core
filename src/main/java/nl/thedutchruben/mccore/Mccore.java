package nl.thedutchruben.mccore;


import nl.thedutchruben.mccore.commands.CommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;


public final class Mccore {
    private JavaPlugin javaPlugin;
    public Mccore(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        new CommandRegistry();
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }
}
