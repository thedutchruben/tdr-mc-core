package nl.thedutchruben.mccore.utils;

import nl.thedutchruben.mccore.Mccore;
import java.util.logging.Level;

public class CoreLogger {

    public static void log(String message){
        Mccore.getInstance().getJavaPlugin().getLogger().info("[TDRMCore] " + message);
    }

    /**
     * Log an informational message
     * @param message The message to log
     */
    public static void info(String message) {
        Mccore.getInstance().getJavaPlugin().getLogger().info("[TDRMCore] " + message);
    }

    /**
     * Log a warning message
     * @param message The message to log
     */
    public static void warning(String message) {
        Mccore.getInstance().getJavaPlugin().getLogger().warning("[TDRMCore] " + message);
    }

    /**
     * Log a severe error message
     * @param message The message to log
     */
    public static void severe(String message) {
        Mccore.getInstance().getJavaPlugin().getLogger().severe("[TDRMCore] " + message);
    }

    /**
     * Log a debug message (only shown when debugging is enabled)
     * @param message The message to log
     */
    public static void debug(String message) {
        // You can add a config check here to only show debug messages when enabled
        Mccore.getInstance().getJavaPlugin().getLogger().log(Level.FINE, "[TDRMCore] " + message);
    }
}