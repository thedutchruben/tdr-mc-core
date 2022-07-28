package nl.thedutchruben.mccore.utils.hologram;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.List;

public class Hologram {
    private Location location;
    private String text;
    private ArmorStand armorStand;

    public Hologram(Location location, String text) {
        this.location = location;
        this.text = text;
    }

    public static List<Hologram> createHolograms(Location location, List<String> text,double space) {
        List<Hologram> holograms = new java.util.ArrayList<>();
        int i = 0;
        for (String s : text) {
            holograms.add(new Hologram(location.clone().add(0,-(space*i),0), s));
            i++;
        }

        return holograms;
    }

    public ArmorStand spawnHologram() {
        armorStand = (ArmorStand) location.clone().getWorld().spawnEntity(location.clone(), EntityType.ARMOR_STAND);
        armorStand.setPersistent(false);
        armorStand.setVisible(false);
        armorStand.setCustomName(text);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        armorStand.setArms(false);
        armorStand.setInvulnerable(true);
        return armorStand;
    }

    public void removeHologram() {
        armorStand.remove();
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public Location getLocation() {
        return location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

}