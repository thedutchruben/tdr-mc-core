package nl.thedutchruben.mccore.utils.animation;

import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class AnimationBuilder {
    private final Plugin plugin;
    private int duration = 20;
    private int interval = 1;
    private boolean loop = false;
    private Consumer<Integer> tickHandler;
    private Runnable startHandler;
    private Runnable stopHandler;
    private Runnable completeHandler;
    private Runnable pauseHandler;
    private Runnable resumeHandler;
    private Runnable resetHandler;
    private Runnable loopHandler;
    
    public AnimationBuilder(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public AnimationBuilder duration(int ticks) {
        this.duration = ticks;
        return this;
    }
    
    public AnimationBuilder interval(int ticks) {
        this.interval = ticks;
        return this;
    }
    
    public AnimationBuilder loop(boolean loop) {
        this.loop = loop;
        return this;
    }
    
    public AnimationBuilder loop() {
        return loop(true);
    }
    
    public AnimationBuilder onTick(Consumer<Integer> handler) {
        this.tickHandler = handler;
        return this;
    }
    
    public AnimationBuilder onStart(Runnable handler) {
        this.startHandler = handler;
        return this;
    }
    
    public AnimationBuilder onStop(Runnable handler) {
        this.stopHandler = handler;
        return this;
    }
    
    public AnimationBuilder onComplete(Runnable handler) {
        this.completeHandler = handler;
        return this;
    }
    
    public AnimationBuilder onPause(Runnable handler) {
        this.pauseHandler = handler;
        return this;
    }
    
    public AnimationBuilder onResume(Runnable handler) {
        this.resumeHandler = handler;
        return this;
    }
    
    public AnimationBuilder onReset(Runnable handler) {
        this.resetHandler = handler;
        return this;
    }
    
    public AnimationBuilder onLoop(Runnable handler) {
        this.loopHandler = handler;
        return this;
    }
    
    public Animation build() {
        return new Animation(plugin, duration, interval, loop) {
            @Override
            protected void onTick(int tick) {
                if (tickHandler != null) {
                    tickHandler.accept(tick);
                }
            }
            
            @Override
            protected void onStart() {
                if (startHandler != null) {
                    startHandler.run();
                }
            }
            
            @Override
            protected void onStop() {
                if (stopHandler != null) {
                    stopHandler.run();
                }
            }
            
            @Override
            protected void onComplete() {
                if (completeHandler != null) {
                    completeHandler.run();
                }
            }
            
            @Override
            protected void onPause() {
                if (pauseHandler != null) {
                    pauseHandler.run();
                }
            }
            
            @Override
            protected void onResume() {
                if (resumeHandler != null) {
                    resumeHandler.run();
                }
            }
            
            @Override
            protected void onReset() {
                if (resetHandler != null) {
                    resetHandler.run();
                }
            }
            
            @Override
            protected void onLoop() {
                if (loopHandler != null) {
                    loopHandler.run();
                }
            }
        };
    }
}