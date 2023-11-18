package nl.thedutchruben.mccore.utils;

import nl.thedutchruben.mccore.Mccore;

public class CoreLogger {

    public static void log(String message){
        Mccore.getInstance().getJavaPlugin().getLogger().info("[TDRMCore] " + message);
    }
}
