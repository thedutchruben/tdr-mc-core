package nl.thedutchruben.mccore.utils.particle;


import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

@UtilityClass
public class ParticleUtil {

    public void line(Location start, Location end, Particle particle) {

        Vector dir = end.getDirection().multiply(2);

        for (double i = 0; i < 10; i += 0.5) {
            dir.multiply(i);
            start.add(dir);

            start.getWorld().spawnParticle(particle,start,1);

            start.subtract(dir);
            dir.normalize();
        }
    }
}
