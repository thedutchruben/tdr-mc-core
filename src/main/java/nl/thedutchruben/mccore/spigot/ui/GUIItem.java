package nl.thedutchruben.mccore.spigot.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Represents an item in a GUI with its associated click handler
 */
@Getter
@Setter
@AllArgsConstructor
public class GUIItem {
    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> clickEvent;

    /**
     * Create a GUIItem with just an ItemStack and no click event
     *
     * @param itemStack The ItemStack to display
     */
    public GUIItem(ItemStack itemStack) {
        this(itemStack, null);
    }
}
