package nl.thedutchruben.mccore.utils.animation;

public class Keyframe<T> {
    private final int tick;
    private final T value;
    private final EasingFunction easing;
    
    public Keyframe(int tick, T value) {
        this(tick, value, EasingFunction.LINEAR);
    }
    
    public Keyframe(int tick, T value, EasingFunction easing) {
        this.tick = tick;
        this.value = value;
        this.easing = easing;
    }
    
    public int getTick() {
        return tick;
    }
    
    public T getValue() {
        return value;
    }
    
    public EasingFunction getEasing() {
        return easing;
    }
}