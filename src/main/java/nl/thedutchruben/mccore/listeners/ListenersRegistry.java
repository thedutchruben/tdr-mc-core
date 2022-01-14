package nl.thedutchruben.mccore.listeners;

import nl.thedutchruben.mccore.Mccore;
import org.reflections.Reflections;

import java.util.Set;

public class ListenersRegistry {
    private Mccore mccore;

    public ListenersRegistry(Mccore mccore) {
        this.mccore = mccore;
        Reflections reflections = new Reflections(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1]);
        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(Listener.class);
        for (Class<?> allClass : allClasses) {
            try {
                mccore.getJavaPlugin().getServer().getPluginManager().registerEvents((org.bukkit.event.Listener) allClass.newInstance(),mccore.getJavaPlugin());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
