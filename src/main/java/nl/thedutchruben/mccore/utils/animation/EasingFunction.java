package nl.thedutchruben.mccore.utils.animation;

import java.util.function.Function;

public enum EasingFunction {
    LINEAR(t -> t),
    EASE_IN(t -> t * t),
    EASE_OUT(t -> 1 - Math.pow(1 - t, 2)),
    EASE_IN_OUT(t -> t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2),
    EASE_IN_CUBIC(t -> t * t * t),
    EASE_OUT_CUBIC(t -> 1 - Math.pow(1 - t, 3)),
    EASE_IN_OUT_CUBIC(t -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2),
    EASE_IN_QUART(t -> t * t * t * t),
    EASE_OUT_QUART(t -> 1 - Math.pow(1 - t, 4)),
    EASE_IN_OUT_QUART(t -> t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2),
    EASE_IN_SINE(t -> 1 - Math.cos((t * Math.PI) / 2)),
    EASE_OUT_SINE(t -> Math.sin((t * Math.PI) / 2)),
    EASE_IN_OUT_SINE(t -> -(Math.cos(Math.PI * t) - 1) / 2),
    BOUNCE_OUT(t -> {
        double n1 = 7.5625;
        double d1 = 2.75;
        
        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            return n1 * (t -= 1.5 / d1) * t + 0.75;
        } else if (t < 2.5 / d1) {
            return n1 * (t -= 2.25 / d1) * t + 0.9375;
        } else {
            return n1 * (t -= 2.625 / d1) * t + 0.984375;
        }
    });
    
    private final Function<Double, Double> function;
    
    EasingFunction(Function<Double, Double> function) {
        this.function = function;
    }
    
    public double apply(double t) {
        return function.apply(Math.max(0, Math.min(1, t)));
    }
}