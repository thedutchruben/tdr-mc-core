package nl.thedutchruben.mccore.listeners;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.utils.classes.ClassFinder;

public class ListenersRegistry {

    public ListenersRegistry(Mccore mccore) {

        for (Class<?> allClass : new ClassFinder().getClasses(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1])) {
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
