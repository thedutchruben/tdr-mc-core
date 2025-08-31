# Animation Module Documentation

The TDR MC Core Animation Module provides a comprehensive system for creating and managing particle effects, location-based animations, and custom animations in Minecraft plugins.

## Overview

The animation module consists of several core components:

- **Animation System**: Base classes for creating time-based animations
- **Particle Animations**: Pre-built particle effects (circles, spirals, waves, etc.)
- **Location Animations**: Smooth movement and teleportation effects
- **Animation Manager**: Centralized management and lifecycle control
- **Easing Functions**: Mathematical curves for smooth animations

## Core Components

### 1. Animation Base Class

The `Animation` class is the foundation of all animations:

```java
public abstract class Animation {
    protected final Plugin plugin;
    protected final int duration;     // Total animation length in ticks
    protected final int interval;     // Delay between animation updates
    protected final boolean loop;     // Whether animation repeats
    
    public void start();              // Start the animation
    public void stop();               // Stop the animation
    public void pause();              // Pause the animation
    public void resume();             // Resume from pause
    public void reset();              // Reset to beginning
    
    protected abstract void onTick(int tick);  // Called each animation frame
}
```

### 2. Animation Builder

Create custom animations using the builder pattern:

```java
Animation customAnimation = new AnimationBuilder(plugin)
    .duration(100)                    // 5 seconds at 20 TPS
    .interval(2)                      // Update every 2 ticks
    .loop(true)                       // Repeat forever
    .onTick(tick -> {
        // Your animation logic here
        player.sendMessage("Tick: " + tick);
    })
    .onStart(() -> player.sendMessage("Animation started!"))
    .onComplete(() -> player.sendMessage("Animation finished!"))
    .build();
```

### 3. Particle Animations

Pre-built particle effects for common patterns:

#### Circle Animation
```java
ParticleAnimation.Circle circleAnimation = new ParticleAnimation.Circle(
    plugin,
    centerLocation,
    Particle.VILLAGER_HAPPY,
    3.0,    // radius
    20,     // number of points
    100,    // duration in ticks
    2,      // interval
    0.0,    // rotation offset
    true    // loop
);
```

#### Spiral Animation
```java
ParticleAnimation.Spiral spiralAnimation = new ParticleAnimation.Spiral(
    plugin,
    baseLocation,
    Particle.ENCHANTMENT_TABLE,
    2.0,    // max radius
    5.0,    // height
    3.0,    // number of turns
    200,    // duration
    1       // interval
);
```

#### Wave Animation
```java
ParticleAnimation.Wave waveAnimation = new ParticleAnimation.Wave(
    plugin,
    startLocation,
    Particle.DRIP_WATER,
    1.0,         // amplitude (wave height)
    2.0,         // wavelength
    10.0,        // total length
    direction,   // wave direction vector
    150,         // duration
    2            // interval
);
```

#### Explosion Animation
```java
ParticleAnimation.Explosion explosionAnimation = new ParticleAnimation.Explosion(
    plugin,
    centerLocation,
    Particle.EXPLOSION_LARGE,
    5.0,    // max radius
    50,     // particle count
    30,     // duration
    1       // interval
);
```

#### Path Animation
```java
List<Vector> pathPoints = Arrays.asList(
    new Vector(0, 0, 0),
    new Vector(2, 1, 0),
    new Vector(4, 2, 2),
    new Vector(6, 1, 4)
);

ParticleAnimation.Path pathAnimation = new ParticleAnimation.Path(
    plugin,
    startLocation,
    Particle.REDSTONE,
    pathPoints,
    100,    // duration
    2,      // interval
    true    // smooth interpolation
);
```

### 4. Location Animations

Smooth movement animations for entities or camera effects:

```java
LocationAnimation teleportAnimation = new LocationAnimation.Builder(
    plugin,
    60,     // duration (3 seconds)
    location -> player.teleport(location)  // callback for each frame
)
.addKeyframe(0, startLocation, EasingFunction.EASE_IN)
.addKeyframe(30, midLocation, EasingFunction.EASE_OUT)
.addKeyframe(60, endLocation, EasingFunction.EASE_IN_OUT)
.build();
```

### 5. Easing Functions

Mathematical curves for natural-looking animations:

```java
public enum EasingFunction {
    LINEAR,           // Constant speed
    EASE_IN,         // Slow start, fast end
    EASE_OUT,        // Fast start, slow end
    EASE_IN_OUT,     // Slow start and end
    EASE_IN_CUBIC,   // Stronger ease in
    EASE_OUT_CUBIC,  // Stronger ease out
    EASE_IN_OUT_CUBIC, // Stronger ease in/out
    EASE_IN_SINE,    // Sine wave ease in
    EASE_OUT_SINE,   // Sine wave ease out
    EASE_IN_OUT_SINE, // Sine wave ease in/out
    BOUNCE_OUT       // Bouncing effect
}
```

### 6. Animation Manager

Centralized management of all animations:

```java
AnimationManager manager = AnimationManager.getInstance(plugin);

// Register and control animations
String animationId = manager.registerAnimation(animation);
manager.registerAnimation("my_animation", animation);

// Control animations
manager.startAnimation("my_animation");
manager.pauseAnimation("my_animation");
manager.resumeAnimation("my_animation");
manager.stopAnimation("my_animation");

// Batch operations
manager.stopAll();
manager.removeAll();

// Query state
boolean isRunning = manager.isAnimationRunning("my_animation");
int activeCount = manager.getActiveAnimationCount();
```

## Usage Examples

### Basic Particle Circle

```java
public class MyPlugin extends JavaPlugin {
    
    public void createParticleCircle(Location center) {
        AnimationManager manager = AnimationManager.getInstance(this);
        
        ParticleAnimation.Circle circle = new ParticleAnimation.Circle(
            this, center, Particle.FLAME, 3.0, 24, 100, 2, 0.0, true
        );
        
        manager.registerAnimation("flame_circle", circle);
        manager.startAnimation("flame_circle");
    }
}
```

### Teleportation Effect

```java
public void smoothTeleport(Player player, Location destination) {
    AnimationManager manager = AnimationManager.getInstance(this);
    
    LocationAnimation teleport = new LocationAnimation.Builder(
        this, 40, location -> player.teleport(location)
    )
    .addKeyframe(0, player.getLocation())
    .addKeyframe(20, player.getLocation().add(0, 3, 0), EasingFunction.EASE_OUT)
    .addKeyframe(40, destination, EasingFunction.EASE_IN)
    .build();
    
    manager.registerAnimation("player_teleport_" + player.getUniqueId(), teleport);
    manager.startAnimation("player_teleport_" + player.getUniqueId());
}
```

### Complex Particle Show

```java
public void createFireworkShow(Location center) {
    AnimationManager manager = AnimationManager.getInstance(this);
    
    // Base rotating circle
    ParticleAnimation.Circle baseCircle = new ParticleAnimation.Circle(
        this, center, Particle.FLAME, 4.0, 24, 400, 2, 0.0, true
    );
    
    // Rising spiral
    ParticleAnimation.Spiral spiral = new ParticleAnimation.Spiral(
        this, center, Particle.ENCHANTMENT_TABLE, 2.0, 8.0, 4.0, 400, 1
    );
    
    // Timed explosions
    Animation explosionTimer = manager.createAnimation()
        .duration(400)
        .interval(80)  // Every 4 seconds
        .onTick(tick -> {
            if (tick % 80 == 0) {
                Location explosionLoc = center.clone().add(
                    (Math.random() - 0.5) * 6,
                    Math.random() * 3,
                    (Math.random() - 0.5) * 6
                );
                
                ParticleAnimation.Explosion explosion = new ParticleAnimation.Explosion(
                    this, explosionLoc, Particle.EXPLOSION_LARGE, 3.0, 30, 20, 1
                );
                
                String explosionId = manager.registerAnimation(explosion);
                manager.startAnimation(explosionId);
            }
        })
        .build();
    
    manager.registerAnimation("show_circle", baseCircle);
    manager.registerAnimation("show_spiral", spiral);
    manager.registerAnimation("show_explosions", explosionTimer);
    
    // Start all animations
    manager.startAnimation("show_circle");
    manager.startAnimation("show_spiral");
    manager.startAnimation("show_explosions");
}
```

### Custom Animation with State

```java
public void createHealthBarAnimation(Player player) {
    AnimationManager manager = AnimationManager.getInstance(this);
    
    Animation healthPulse = manager.createAnimation()
        .duration(40)  // 2 seconds
        .interval(4)   // Update every 4 ticks
        .loop(true)
        .onTick(tick -> {
            double health = player.getHealth();
            double maxHealth = player.getMaxHealth();
            double healthPercent = health / maxHealth;
            
            // Create pulsing effect based on health
            double pulseIntensity = 1.0 - healthPercent;  // More intense when low health
            double pulseValue = Math.sin(tick * 0.3) * pulseIntensity;
            
            // Spawn particles above player's head
            Location headLocation = player.getLocation().add(0, 2.5, 0);
            int particleCount = (int) (healthPercent * 10);  // Fewer particles = less health
            
            Color particleColor = healthPercent > 0.5 ? Color.GREEN : 
                                 healthPercent > 0.2 ? Color.YELLOW : Color.RED;
            
            // This would require additional particle color support
            player.getWorld().spawnParticle(
                Particle.REDSTONE, 
                headLocation, 
                particleCount,
                0.5, 0.1, 0.5,  // spread
                0,              // speed
                new Particle.DustOptions(particleColor, 1.0f)
            );
        })
        .build();
    
    manager.registerAnimation("health_display_" + player.getUniqueId(), healthPulse);
    manager.startAnimation("health_display_" + player.getUniqueId());
}
```

## Best Practices

### 1. Plugin Shutdown Cleanup

Always clean up animations when your plugin disables:

```java
@Override
public void onDisable() {
    AnimationManager.cleanup(this);
}
```

### 2. Performance Considerations

- Use appropriate intervals (avoid interval=1 for complex animations)
- Limit the number of concurrent animations
- Monitor `getActiveAnimationCount()` for debugging
- Use `stopAll()` during server lag for emergency cleanup

### 3. Memory Management

- Remove animations when no longer needed with `removeAnimation()`
- Avoid creating animations in tight loops
- Use named animations for reusable effects

### 4. Error Handling

```java
public void safeStartAnimation(String name) {
    if (manager.getAnimation(name) != null) {
        if (!manager.isAnimationRunning(name)) {
            manager.startAnimation(name);
        }
    } else {
        getLogger().warning("Animation '" + name + "' not found");
    }
}
```

### 5. Testing Animations

The module includes comprehensive tests in `AnimationModuleTest.java`. Run tests with:

```bash
mvn test -Dtest=AnimationModuleTest
```

## Integration with TDR MC Core

The animation module integrates seamlessly with other TDR MC Core components:

- **Command System**: Create animation commands using `@SubCommand` annotations
- **GUI System**: Trigger animations from GUI interactions
- **Caching System**: Store animation configurations persistently
- **Utility Classes**: Combine with `ItemBuilder`, `MessageUtil`, etc.

## Future Enhancements

Potential future additions:

- **3D Model Animations**: Support for complex 3D shapes
- **Sound Integration**: Synchronize sounds with visual effects
- **Block Animations**: Animate block changes and structures
- **Entity Animations**: Control entity movement and behavior
- **Timeline System**: Complex multi-track animations

## API Reference

### Core Classes

- `Animation` - Base animation class
- `AnimationBuilder` - Fluent API for creating animations
- `AnimationManager` - Centralized animation management
- `EasingFunction` - Mathematical easing curves
- `Keyframe<T>` - Animation keyframe data structure

### Particle Animation Classes

- `ParticleAnimation` - Base class for particle effects
- `ParticleAnimation.Circle` - Circular particle patterns
- `ParticleAnimation.Spiral` - Spiral particle effects
- `ParticleAnimation.Wave` - Wave-based particle animations
- `ParticleAnimation.Explosion` - Explosion particle effects  
- `ParticleAnimation.Path` - Path-following particle animations

### Location Animation Classes

- `LocationAnimation` - Smooth location interpolation
- `LocationAnimation.Builder` - Builder for location animations

### Package Structure

```
nl.thedutchruben.mccore.utils.animation/
├── Animation.java               # Base animation class
├── AnimationBuilder.java        # Animation creation helper
├── AnimationManager.java        # Central management
├── EasingFunction.java          # Mathematical curves
├── Keyframe.java               # Animation keyframes
├── LocationAnimation.java       # Location-based animations
└── ParticleAnimation.java       # Particle effect animations
```

This documentation provides a complete guide to implementing and using the animation module in your Minecraft plugins.