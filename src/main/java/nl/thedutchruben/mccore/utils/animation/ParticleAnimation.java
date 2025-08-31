package nl.thedutchruben.mccore.utils.animation;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class ParticleAnimation extends Animation {
    protected final Location baseLocation;
    protected final Particle particle;
    protected final int particleCount;
    protected final double particleSpeed;
    protected final Object particleData;
    
    public ParticleAnimation(Plugin plugin, Location baseLocation, Particle particle, int duration, int interval) {
        this(plugin, baseLocation, particle, duration, interval, 1, 0.0, null, false);
    }
    
    public ParticleAnimation(Plugin plugin, Location baseLocation, Particle particle, int duration, int interval, 
                           int particleCount, double particleSpeed, Object particleData, boolean loop) {
        super(plugin, duration, interval, loop);
        this.baseLocation = baseLocation.clone();
        this.particle = particle;
        this.particleCount = particleCount;
        this.particleSpeed = particleSpeed;
        this.particleData = particleData;
    }
    
    protected void spawnParticle(Location location) {
        if (location.getWorld() != null) {
            if (particleData != null) {
                location.getWorld().spawnParticle(particle, location, particleCount, 0, 0, 0, particleSpeed, particleData);
            } else {
                location.getWorld().spawnParticle(particle, location, particleCount, 0, 0, 0, particleSpeed);
            }
        }
    }
    
    protected void spawnParticle(Location location, Vector offset) {
        Location particleLocation = location.clone().add(offset);
        spawnParticle(particleLocation);
    }
    
    public static class Circle extends ParticleAnimation {
        private final double radius;
        private final int points;
        private final double rotation;
        
        public Circle(Plugin plugin, Location center, Particle particle, double radius, int points, int duration, int interval) {
            this(plugin, center, particle, radius, points, duration, interval, 0.0, false);
        }
        
        public Circle(Plugin plugin, Location center, Particle particle, double radius, int points, int duration, int interval, double rotation, boolean loop) {
            super(plugin, center, particle, duration, interval, 1, 0.0, null, loop);
            this.radius = radius;
            this.points = points;
            this.rotation = rotation;
        }
        
        @Override
        protected void onTick(int tick) {
            double progress = (double) tick / duration;
            double currentRotation = rotation + (progress * Math.PI * 2);
            
            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI * i / points) + currentRotation;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                
                Vector offset = new Vector(x, 0, z);
                spawnParticle(baseLocation, offset);
            }
        }
    }
    
    public static class Spiral extends ParticleAnimation {
        private final double maxRadius;
        private final double height;
        private final double spiralTurns;
        
        public Spiral(Plugin plugin, Location baseLocation, Particle particle, double maxRadius, double height, double spiralTurns, int duration, int interval) {
            super(plugin, baseLocation, particle, duration, interval);
            this.maxRadius = maxRadius;
            this.height = height;
            this.spiralTurns = spiralTurns;
        }
        
        @Override
        protected void onTick(int tick) {
            double progress = (double) tick / duration;
            double angle = progress * spiralTurns * Math.PI * 2;
            double currentRadius = maxRadius * progress;
            double currentHeight = height * progress;
            
            double x = Math.cos(angle) * currentRadius;
            double z = Math.sin(angle) * currentRadius;
            
            Vector offset = new Vector(x, currentHeight, z);
            spawnParticle(baseLocation, offset);
        }
    }
    
    public static class Wave extends ParticleAnimation {
        private final double amplitude;
        private final double wavelength;
        private final double length;
        private final Vector direction;
        
        public Wave(Plugin plugin, Location baseLocation, Particle particle, double amplitude, double wavelength, double length, Vector direction, int duration, int interval) {
            super(plugin, baseLocation, particle, duration, interval);
            this.amplitude = amplitude;
            this.wavelength = wavelength;
            this.length = length;
            this.direction = direction.clone().normalize();
        }
        
        @Override
        protected void onTick(int tick) {
            double progress = (double) tick / duration;
            int pointCount = 20;
            
            for (int i = 0; i < pointCount; i++) {
                double x = (double) i / pointCount * length;
                double y = Math.sin((x / wavelength + progress) * Math.PI * 2) * amplitude;
                
                Vector offset = direction.clone().multiply(x).setY(y);
                spawnParticle(baseLocation, offset);
            }
        }
    }
    
    public static class Explosion extends ParticleAnimation {
        private final double maxRadius;
        private final int particleCount;
        
        public Explosion(Plugin plugin, Location center, Particle particle, double maxRadius, int particleCount, int duration, int interval) {
            super(plugin, center, particle, duration, interval);
            this.maxRadius = maxRadius;
            this.particleCount = particleCount;
        }
        
        @Override
        protected void onTick(int tick) {
            double progress = (double) tick / duration;
            double currentRadius = maxRadius * progress;
            
            for (int i = 0; i < particleCount; i++) {
                double theta = Math.random() * Math.PI * 2;
                double phi = Math.random() * Math.PI;
                
                double x = currentRadius * Math.sin(phi) * Math.cos(theta);
                double y = currentRadius * Math.cos(phi);
                double z = currentRadius * Math.sin(phi) * Math.sin(theta);
                
                Vector offset = new Vector(x, y, z);
                spawnParticle(baseLocation, offset);
            }
        }
    }
    
    public static class Path extends ParticleAnimation {
        private final List<Vector> pathPoints;
        private final boolean smooth;
        
        public Path(Plugin plugin, Location baseLocation, Particle particle, List<Vector> pathPoints, int duration, int interval, boolean smooth) {
            super(plugin, baseLocation, particle, duration, interval);
            this.pathPoints = new ArrayList<>(pathPoints);
            this.smooth = smooth;
        }
        
        @Override
        protected void onTick(int tick) {
            if (pathPoints.isEmpty()) return;
            
            double progress = (double) tick / duration;
            double totalLength = pathPoints.size() - 1;
            double currentPosition = progress * totalLength;
            
            int index = (int) currentPosition;
            double fraction = currentPosition - index;
            
            if (index >= pathPoints.size() - 1) {
                spawnParticle(baseLocation, pathPoints.get(pathPoints.size() - 1));
                return;
            }
            
            Vector current = pathPoints.get(index);
            Vector next = pathPoints.get(index + 1);
            
            Vector interpolated;
            if (smooth) {
                interpolated = current.clone().add(next.clone().subtract(current).multiply(fraction));
            } else {
                interpolated = current.clone();
            }
            
            spawnParticle(baseLocation, interpolated);
        }
    }
}