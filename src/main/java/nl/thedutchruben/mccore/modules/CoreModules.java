package nl.thedutchruben.mccore.modules;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.modules.updates.PlayerLoginListener;

public class CoreModules {

    public void registerUpdateChecker(Mccore mccore){
    mccore.getJavaPlugin().getServer().getPluginManager().registerEvents(new PlayerLoginListener(),mccore.getJavaPlugin());
    }
}
