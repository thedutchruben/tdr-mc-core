package nl.thedutchruben.mccore.modules.updates;

import nl.thedutchruben.mccore.Mccore;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    public void onLogin(PlayerLoginEvent event){
        Mccore.getInstance().getUpdate(event.getPlayer());
    }
}
