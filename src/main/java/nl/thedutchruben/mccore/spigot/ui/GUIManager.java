package nl.thedutchruben.mccore.spigot.ui;

import lombok.Getter;
import nl.thedutchruben.mccore.Mccore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager class for handling GUI events and tracking open GUIs
 */
public class GUIManager implements Listener {
    @Getter
    private static final Map<UUID, GUI> openGuis = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Register the GUIManager as a listener with your plugin
     *
     * @param plugin The JavaPlugin instance to register with
     */
    public static void initialize(JavaPlugin plugin) {
        if (!initialized) {
            plugin.getServer().getPluginManager().registerEvents(new GUIManager(), plugin);
            initialized = true;
        }
    }

    /**
     * Register an open GUI for a player
     *
     * @param playerUuid The player's UUID
     * @param gui        The GUI that was opened
     */
    public static void registerOpenGUI(UUID playerUuid, GUI gui) {
        openGuis.put(playerUuid, gui);
    }

    /**
     * Get the currently open GUI for a player
     *
     * @param playerUuid The player's UUID
     * @return The player's current GUI, or null if none is open
     */
    public static GUI getOpenGui(UUID playerUuid) {
        return openGuis.get(playerUuid);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder()).handleClick(event);
        } else if (event.getWhoClicked() instanceof Player) {
            UUID playerUuid = event.getWhoClicked().getUniqueId();
            if (openGuis.containsKey(playerUuid)) {
                openGuis.get(playerUuid).handleClick(event);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder()).handleDrag(event);
        } else if (event.getWhoClicked() instanceof Player) {
            UUID playerUuid = event.getWhoClicked().getUniqueId();
            if (openGuis.containsKey(playerUuid)) {
                openGuis.get(playerUuid).handleDrag(event);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder()).handleClose(event);
        } else if (event.getPlayer() instanceof Player) {
            UUID playerUuid = event.getPlayer().getUniqueId();
            if (openGuis.containsKey(playerUuid)) {
                openGuis.get(playerUuid).handleClose(event);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        openGuis.remove(event.getPlayer().getUniqueId());
    }
}
