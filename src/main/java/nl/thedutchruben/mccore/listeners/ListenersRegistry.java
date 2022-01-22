package nl.thedutchruben.mccore.listeners;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.utils.classes.ClassFinder;

import java.util.Set;

public class ListenersRegistry {
    private Mccore mccore;

    public ListenersRegistry(Mccore mccore) {
        this.mccore = mccore;

        for (Class<?> allClass : new ClassFinder().findClasses(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1])) {
            if(allClass.isAnnotationPresent(TDRListener.class)){
                try {
                    mccore.getJavaPlugin().getServer().getPluginManager().registerEvents((org.bukkit.event.Listener) allClass.newInstance(),mccore.getJavaPlugin());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
