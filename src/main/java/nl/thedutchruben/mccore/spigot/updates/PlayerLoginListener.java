package nl.thedutchruben.mccore.spigot.updates;

import nl.thedutchruben.mccore.Mccore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLoginListener implements Listener {
    private Mccore mccore;

    public PlayerLoginListener(Mccore mccore) {
        this.mccore = mccore;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(event.getPlayer().hasPermission(mccore.getUpdateCheckerConfig().getPermission())){
            mccore.getUpdate(event.getPlayer());
        }
    }
}
