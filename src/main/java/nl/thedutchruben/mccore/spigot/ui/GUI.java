package nl.thedutchruben.mccore.spigot.ui;

import lombok.Getter;
import lombok.Setter;
import nl.thedutchruben.mccore.Mccore;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * GUI class for creating custom inventories
 * <p>This class provides a base for creating custom GUIs in Minecraft</p>
 */
public class GUI implements InventoryHolder {
    @Getter
    private final Inventory inventory;
    @Getter
    @Setter
    private String title;
    @Getter
    private final int rows;
    @Getter
    private final Map<Integer, GUIItem> items = new HashMap<>();
    @Getter
    @Setter
    private Consumer<InventoryCloseEvent> closeAction;
    @Getter
    @Setter
    private boolean cancelAllClicks = true;

    /**
     * Creates a new GUI with a specified number of rows (1-6) and a title
     *
     * @param rows  Number of rows (1-6)
     * @param title Title of the inventory
     */
    public GUI(int rows, String title) {
        this.rows = Math.min(6, Math.max(1, rows));
        this.title = title;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    /**
     * Set an item in the GUI
     *
     * @param slot The slot to set the item in (0-53)
     * @param item The item to set
     * @return This GUI instance for chaining
     */
    public GUI setItem(int slot, GUIItem item) {
        if (slot >= 0 && slot < inventory.getSize()) {
            items.put(slot, item);
            inventory.setItem(slot, item.getItemStack());
        }
        return this;
    }

    /**
     * Set an item in the GUI with a click handler
     *
     * @param slot       The slot to set the item in (0-53)
     * @param itemStack  The ItemStack to display
     * @param clickEvent The handler for click events on this item
     * @return This GUI instance for chaining
     */
    public GUI setItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> clickEvent) {
        return setItem(slot, new GUIItem(itemStack, clickEvent));
    }

    /**
     * Set an item in the GUI without a click handler
     *
     * @param slot      The slot to set the item in (0-53)
     * @param itemStack The ItemStack to display
     * @return This GUI instance for chaining
     */
    public GUI setItem(int slot, ItemStack itemStack) {
        return setItem(slot, new GUIItem(itemStack));
    }

    /**
     * Fill the entire GUI with the specified item
     *
     * @param item Item to fill with
     * @return This GUI instance for chaining
     */
    public GUI fill(GUIItem item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            setItem(i, item);
        }
        return this;
    }

    /**
     * Fill the empty slots of the GUI with the specified item
     *
     * @param item Item to fill with
     * @return This GUI instance for chaining
     */
    public GUI fillEmpty(GUIItem item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                setItem(i, item);
            }
        }
        return this;
    }

    /**
     * Add a border to the GUI with the specified item
     *
     * @param item Item to use as border
     * @return This GUI instance for chaining
     */
    public GUI addBorder(GUIItem item) {
        int size = inventory.getSize();
        int rowSize = 9;
        int rows = size / rowSize;

        // Top and bottom rows
        for (int i = 0; i < rowSize; i++) {
            setItem(i, item); // Top row
            setItem(size - rowSize + i, item); // Bottom row
        }

        // Left and right columns
        for (int i = 1; i < rows - 1; i++) {
            setItem(i * rowSize, item); // Left column
            setItem(i * rowSize + rowSize - 1, item); // Right column
        }

        return this;
    }

    /**
     * Handle an inventory click event
     *
     * @param event The inventory click event
     */
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot >= 0 && slot < inventory.getSize()) {
            GUIItem item = items.get(slot);
            if (item != null && item.getClickEvent() != null) {
                item.getClickEvent().accept(event);
            }
        }
        if (cancelAllClicks) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle an inventory drag event
     *
     * @param event The inventory drag event
     */
    public void handleDrag(InventoryDragEvent event) {
        if (cancelAllClicks) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle an inventory close event
     *
     * @param event The inventory close event
     */
    public void handleClose(InventoryCloseEvent event) {
        if (closeAction != null) {
            closeAction.accept(event);
        }
        GUIManager.getOpenGuis().remove(event.getPlayer().getUniqueId());
    }

    /**
     * Open this GUI for a player
     *
     * @param player The player to open the GUI for
     */
    public void open(Player player) {
        GUIManager.registerOpenGUI(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    /**
     * Update the GUI for all viewers
     */
    public void update() {
        for (int slot : items.keySet()) {
            inventory.setItem(slot, items.get(slot).getItemStack());
        }
    }

    /**
     * Close the GUI for all viewers
     */
    public void close() {
        for (HumanEntity viewer : inventory.getViewers()) {
            viewer.closeInventory();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
