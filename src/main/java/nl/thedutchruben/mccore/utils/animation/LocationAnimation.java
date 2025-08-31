package nl.thedutchruben.mccore.utils.animation;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LocationAnimation extends Animation {
    private final List<Keyframe<Location>> keyframes;
    private final Consumer<Location> locationConsumer;
    
    public LocationAnimation(Plugin plugin, int duration, int interval, Consumer<Location> locationConsumer) {
        super(plugin, duration, interval);
        this.keyframes = new ArrayList<>();
        this.locationConsumer = locationConsumer;
    }
    
    public LocationAnimation addKeyframe(int tick, Location location) {
        return addKeyframe(tick, location, EasingFunction.LINEAR);
    }
    
    public LocationAnimation addKeyframe(int tick, Location location, EasingFunction easing) {
        keyframes.add(new Keyframe<>(tick, location.clone(), easing));
        keyframes.sort((a, b) -> Integer.compare(a.getTick(), b.getTick()));
        return this;
    }
    
    @Override
    protected void onTick(int tick) {
        if (keyframes.isEmpty()) return;
        
        Location interpolatedLocation = interpolateLocation(tick);
        if (interpolatedLocation != null && locationConsumer != null) {
            locationConsumer.accept(interpolatedLocation);
        }
    }
    
    private Location interpolateLocation(int currentTick) {
        if (keyframes.isEmpty()) return null;
        
        if (currentTick <= keyframes.get(0).getTick()) {
            return keyframes.get(0).getValue().clone();
        }
        
        if (currentTick >= keyframes.get(keyframes.size() - 1).getTick()) {
            return keyframes.get(keyframes.size() - 1).getValue().clone();
        }
        
        Keyframe<Location> before = null;
        Keyframe<Location> after = null;
        
        for (int i = 0; i < keyframes.size() - 1; i++) {
            if (currentTick >= keyframes.get(i).getTick() && currentTick <= keyframes.get(i + 1).getTick()) {
                before = keyframes.get(i);
                after = keyframes.get(i + 1);
                break;
            }
        }
        
        if (before == null || after == null) return null;
        
        double progress = (double) (currentTick - before.getTick()) / (after.getTick() - before.getTick());
        progress = after.getEasing().apply(progress);
        
        Location beforeLoc = before.getValue();
        Location afterLoc = after.getValue();
        
        double x = lerp(beforeLoc.getX(), afterLoc.getX(), progress);
        double y = lerp(beforeLoc.getY(), afterLoc.getY(), progress);
        double z = lerp(beforeLoc.getZ(), afterLoc.getZ(), progress);
        float yaw = lerpAngle(beforeLoc.getYaw(), afterLoc.getYaw(), progress);
        float pitch = lerpAngle(beforeLoc.getPitch(), afterLoc.getPitch(), progress);
        
        return new Location(beforeLoc.getWorld(), x, y, z, yaw, pitch);
    }
    
    private double lerp(double start, double end, double progress) {
        return start + (end - start) * progress;
    }
    
    private float lerpAngle(float start, float end, double progress) {
        float diff = end - start;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        return start + (float) (diff * progress);
    }
    
    public static class Builder {
        private final LocationAnimation animation;
        
        public Builder(Plugin plugin, int duration, Consumer<Location> locationConsumer) {
            this.animation = new LocationAnimation(plugin, duration, 1, locationConsumer);
        }
        
        public Builder interval(int interval) {
            return new Builder(animation.plugin, animation.duration, animation.locationConsumer) {
                {
                    this.animation.keyframes.addAll(Builder.this.animation.keyframes);
                }
            };
        }
        
        public Builder addKeyframe(int tick, Location location) {
            animation.addKeyframe(tick, location);
            return this;
        }
        
        public Builder addKeyframe(int tick, Location location, EasingFunction easing) {
            animation.addKeyframe(tick, location, easing);
            return this;
        }
        
        public LocationAnimation build() {
            return animation;
        }
    }
}