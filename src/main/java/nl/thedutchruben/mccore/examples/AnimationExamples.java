package nl.thedutchruben.mccore.examples;

import nl.thedutchruben.mccore.utils.animation.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class AnimationExamples {
    private final Plugin plugin;
    private final AnimationManager animationManager;
    
    public AnimationExamples(Plugin plugin) {
        this.plugin = plugin;
        this.animationManager = AnimationManager.getInstance(plugin);
    }
    
    public void createCircleParticleAnimation(Location center) {
        ParticleAnimation.Circle circleAnimation = new ParticleAnimation.Circle(
            plugin,
            center,
            Particle.VILLAGER_HAPPY,
            3.0, // radius
            20,  // points
            100, // duration (5 seconds at 20 ticks/second)
            2,   // interval (every 2 ticks)
            0.0, // rotation
            true // loop
        );
        
        String animationId = animationManager.registerAnimation(circleAnimation);
        animationManager.startAnimation(animationId);
    }
    
    public void createSpiralAnimation(Location base) {
        ParticleAnimation.Spiral spiralAnimation = new ParticleAnimation.Spiral(
            plugin,
            base,
            Particle.ENCHANTMENT_TABLE,
            2.0, // max radius
            5.0, // height
            3.0, // spiral turns
            200, // duration (10 seconds)
            1    // interval
        );
        
        animationManager.registerAnimation("spiral_effect", spiralAnimation);
        animationManager.startAnimation("spiral_effect");
    }
    
    public void createWaveAnimation(Location start, Vector direction) {
        ParticleAnimation.Wave waveAnimation = new ParticleAnimation.Wave(
            plugin,
            start,
            Particle.DRIP_WATER,
            1.0, // amplitude
            2.0, // wavelength
            10.0, // length
            direction,
            150, // duration
            2    // interval
        );
        
        animationManager.registerAnimation("water_wave", waveAnimation);
        animationManager.startAnimation("water_wave");
    }
    
    public void createExplosionAnimation(Location center) {
        ParticleAnimation.Explosion explosionAnimation = new ParticleAnimation.Explosion(
            plugin,
            center,
            Particle.EXPLOSION_LARGE,
            5.0, // max radius
            50,  // particle count
            30,  // duration
            1    // interval
        );
        
        String animationId = animationManager.registerAnimation(explosionAnimation);
        animationManager.startAnimation(animationId);
    }
    
    public void createPathAnimation(Location start) {
        ParticleAnimation.Path pathAnimation = new ParticleAnimation.Path(
            plugin,
            start,
            Particle.REDSTONE,
            Arrays.asList(
                new Vector(0, 0, 0),
                new Vector(2, 1, 0),
                new Vector(4, 2, 2),
                new Vector(6, 1, 4),
                new Vector(8, 0, 6)
            ),
            100, // duration
            2,   // interval
            true // smooth interpolation
        );
        
        animationManager.registerAnimation("particle_path", pathAnimation);
        animationManager.startAnimation("particle_path");
    }
    
    public void createPlayerTeleportAnimation(Player player, Location destination) {
        LocationAnimation.Builder builder = new LocationAnimation.Builder(
            plugin,
            60, // 3 seconds
            location -> player.teleport(location)
        );
        
        LocationAnimation teleportAnimation = builder
            .addKeyframe(0, player.getLocation(), EasingFunction.EASE_IN)
            .addKeyframe(30, player.getLocation().add(0, 5, 0), EasingFunction.EASE_OUT)
            .addKeyframe(60, destination, EasingFunction.EASE_IN_OUT)
            .build();
        
        animationManager.registerAnimation("player_teleport", teleportAnimation);
        animationManager.startAnimation("player_teleport");
    }
    
    public void createCustomAnimation() {
        Animation customAnimation = animationManager.createAnimation()
            .duration(100)
            .interval(5)
            .loop(true)
            .onTick(tick -> {
                // Custom animation logic per tick
                System.out.println("Custom animation tick: " + tick);
            })
            .onStart(() -> System.out.println("Custom animation started!"))
            .onComplete(() -> System.out.println("Custom animation completed!"))
            .build();
        
        animationManager.registerAnimation("custom_debug", customAnimation);
        animationManager.startAnimation("custom_debug");
    }
    
    public void createComplexParticleShow(Location center) {
        // Create multiple animations that work together
        
        // Base circle
        ParticleAnimation.Circle baseCircle = new ParticleAnimation.Circle(
            plugin, center, Particle.FLAME, 4.0, 24, 200, 2, 0.0, true
        );
        
        // Rising spiral
        ParticleAnimation.Spiral spiral = new ParticleAnimation.Spiral(
            plugin, center, Particle.ENCHANTMENT_TABLE, 2.0, 8.0, 4.0, 200, 1
        );
        
        // Explosions at intervals
        Animation explosionTimer = animationManager.createAnimation()
            .duration(200)
            .interval(40)
            .onTick(tick -> {
                if (tick % 40 == 0) {
                    Location explosionLoc = center.clone().add(
                        (Math.random() - 0.5) * 6,
                        Math.random() * 3,
                        (Math.random() - 0.5) * 6
                    );
                    createExplosionAnimation(explosionLoc);
                }
            })
            .build();
        
        animationManager.registerAnimation("show_base_circle", baseCircle);
        animationManager.registerAnimation("show_spiral", spiral);
        animationManager.registerAnimation("show_explosions", explosionTimer);
        
        animationManager.startAnimation("show_base_circle");
        animationManager.startAnimation("show_spiral");
        animationManager.startAnimation("show_explosions");
    }
    
    public void stopAllAnimations() {
        animationManager.stopAll();
    }
    
    public void stopSpecificAnimation(String name) {
        animationManager.stopAnimation(name);
    }
}