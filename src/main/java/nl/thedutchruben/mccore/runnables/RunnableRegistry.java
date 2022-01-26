package nl.thedutchruben.mccore.runnables;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.commands.SubCommand;
import nl.thedutchruben.mccore.listeners.TDRListener;
import nl.thedutchruben.mccore.utils.classes.ClassFinder;

public class RunnableRegistry {
    private Mccore mccore;

    public RunnableRegistry(Mccore mccore) {
        this.mccore = mccore;

        for (Class<?> allClass : new ClassFinder().findClasses(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1])) {
            if(allClass.isAnnotationPresent(ASyncRepeatingTask.class)){
                ASyncRepeatingTask annotation = allClass.getAnnotation(ASyncRepeatingTask.class);
                try {
                    mccore.getJavaPlugin().getServer().getScheduler().runTaskTimerAsynchronously(mccore.getJavaPlugin(), (Runnable) allClass.newInstance(),annotation.startTime(),annotation.repeatTime());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if(allClass.isAnnotationPresent(SyncRepeatingTask.class)){
                SyncRepeatingTask annotation = allClass.getAnnotation(SyncRepeatingTask.class);
                try {
                    mccore.getJavaPlugin().getServer().getScheduler().runTaskTimer(mccore.getJavaPlugin(), (Runnable) allClass.newInstance(),annotation.startTime(),annotation.repeatTime());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
