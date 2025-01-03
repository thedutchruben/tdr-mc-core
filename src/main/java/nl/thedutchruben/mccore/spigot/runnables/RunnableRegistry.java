package nl.thedutchruben.mccore.spigot.runnables;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.utils.classes.ClassFinder;

/**
 * Manages the registration of various types of tasks (synchronous, asynchronous, and cron-based)
 * for a Minecraft plugin.
 */
public class RunnableRegistry {
    private Mccore mccore; // The main plugin instance
    private CronScheduler cronScheduler; // Scheduler for cron-based tasks

    /**
     * Constructs a RunnableRegistry and registers tasks based on annotations.
     *
     * @param mccore the main plugin instance
     */
    public RunnableRegistry(Mccore mccore) {
        this.mccore = mccore;
        this.cronScheduler = new CronScheduler();

        // Iterate through all classes in the plugin's package
        for (Class<?> allClass : new ClassFinder().getClasses(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1])) {
            // Register asynchronous repeating tasks
            if (allClass.isAnnotationPresent(ASyncRepeatingTask.class)) {
                ASyncRepeatingTask annotation = allClass.getAnnotation(ASyncRepeatingTask.class);
                try {
                    mccore.getJavaPlugin().getServer().getScheduler().runTaskTimerAsynchronously(
                            mccore.getJavaPlugin(),
                            (Runnable) allClass.newInstance(),
                            annotation.startTime(),
                            annotation.repeatTime()
                    );
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // Register synchronous repeating tasks
            if (allClass.isAnnotationPresent(SyncRepeatingTask.class)) {
                SyncRepeatingTask annotation = allClass.getAnnotation(SyncRepeatingTask.class);
                try {
                    mccore.getJavaPlugin().getServer().getScheduler().runTaskTimer(
                            mccore.getJavaPlugin(),
                            (Runnable) allClass.newInstance(),
                            annotation.startTime(),
                            annotation.repeatTime()
                    );
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // Register asynchronous cron-based tasks
            if (allClass.isAnnotationPresent(ASyncCrontabTask.class)) {
                ASyncCrontabTask annotation = allClass.getAnnotation(ASyncCrontabTask.class);
                try {
                    this.cronScheduler.scheduleTask(annotation.cronTab(), (Runnable) allClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}