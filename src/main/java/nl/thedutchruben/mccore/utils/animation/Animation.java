package nl.thedutchruben.mccore.utils.animation;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Animation {
    protected final Plugin plugin;
    protected final int duration;
    protected final int interval;
    protected final boolean loop;
    
    protected BukkitTask task;
    protected int currentTick = 0;
    protected boolean isRunning = false;
    protected boolean isPaused = false;
    
    public Animation(Plugin plugin, int duration, int interval, boolean loop) {
        this.plugin = plugin;
        this.duration = duration;
        this.interval = interval;
        this.loop = loop;
    }
    
    public Animation(Plugin plugin, int duration, int interval) {
        this(plugin, duration, interval, false);
    }
    
    public void start() {
        if (isRunning) return;
        
        isRunning = true;
        isPaused = false;
        currentTick = 0;
        
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (isPaused) return;
                
                if (currentTick >= duration) {
                    if (loop) {
                        currentTick = 0;
                        onLoop();
                    } else {
                        stop();
                        onComplete();
                        return;
                    }
                }
                
                onTick(currentTick);
                currentTick++;
            }
        }.runTaskTimer(plugin, 0L, interval);
        
        onStart();
    }
    
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        isRunning = false;
        isPaused = false;
        onStop();
    }
    
    public void pause() {
        isPaused = true;
        onPause();
    }
    
    public void resume() {
        isPaused = false;
        onResume();
    }
    
    public void reset() {
        currentTick = 0;
        onReset();
    }
    
    protected abstract void onTick(int tick);
    
    protected void onStart() {}
    protected void onStop() {}
    protected void onComplete() {}
    protected void onPause() {}
    protected void onResume() {}
    protected void onReset() {}
    protected void onLoop() {}
    
    public boolean isRunning() { return isRunning; }
    public boolean isPaused() { return isPaused; }
    public int getCurrentTick() { return currentTick; }
    public int getDuration() { return duration; }
    public int getInterval() { return interval; }
    public boolean isLooping() { return loop; }
}