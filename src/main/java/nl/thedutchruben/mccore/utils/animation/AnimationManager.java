package nl.thedutchruben.mccore.utils.animation;

import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationManager {
    private static final Map<Plugin, AnimationManager> instances = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private final Map<String, Animation> animations;
    private final Map<String, Animation> namedAnimations;
    
    private AnimationManager(Plugin plugin) {
        this.plugin = plugin;
        this.animations = new ConcurrentHashMap<>();
        this.namedAnimations = new ConcurrentHashMap<>();
    }
    
    public static AnimationManager getInstance(Plugin plugin) {
        return instances.computeIfAbsent(plugin, AnimationManager::new);
    }
    
    public String registerAnimation(Animation animation) {
        String id = UUID.randomUUID().toString();
        animations.put(id, animation);
        return id;
    }
    
    public void registerAnimation(String name, Animation animation) {
        namedAnimations.put(name, animation);
        animations.put(name, animation);
    }
    
    public Animation getAnimation(String identifier) {
        Animation animation = namedAnimations.get(identifier);
        if (animation == null) {
            animation = animations.get(identifier);
        }
        return animation;
    }
    
    public void startAnimation(String identifier) {
        Animation animation = getAnimation(identifier);
        if (animation != null) {
            animation.start();
        }
    }
    
    public void stopAnimation(String identifier) {
        Animation animation = getAnimation(identifier);
        if (animation != null) {
            animation.stop();
        }
    }
    
    public void pauseAnimation(String identifier) {
        Animation animation = getAnimation(identifier);
        if (animation != null) {
            animation.pause();
        }
    }
    
    public void resumeAnimation(String identifier) {
        Animation animation = getAnimation(identifier);
        if (animation != null) {
            animation.resume();
        }
    }
    
    public void removeAnimation(String identifier) {
        Animation animation = animations.remove(identifier);
        if (animation != null) {
            animation.stop();
        }
        namedAnimations.entrySet().removeIf(entry -> entry.getValue() == animation);
    }
    
    public void stopAll() {
        animations.values().forEach(Animation::stop);
    }
    
    public void removeAll() {
        stopAll();
        animations.clear();
        namedAnimations.clear();
    }
    
    public boolean isAnimationRunning(String identifier) {
        Animation animation = getAnimation(identifier);
        return animation != null && animation.isRunning();
    }
    
    public int getActiveAnimationCount() {
        return (int) animations.values().stream().filter(Animation::isRunning).count();
    }
    
    public static void cleanup(Plugin plugin) {
        AnimationManager manager = instances.remove(plugin);
        if (manager != null) {
            manager.removeAll();
        }
    }
    
    public AnimationBuilder createAnimation() {
        return new AnimationBuilder(plugin);
    }
}