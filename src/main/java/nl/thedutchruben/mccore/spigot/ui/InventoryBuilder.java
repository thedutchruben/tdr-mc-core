package nl.thedutchruben.mccore.spigot.ui;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryBuilder {
    private String title;
    private InventoryType inventoryType;

    private InventoryHolder inventoryHolder;
    

    public InventoryBuilder(String title) {
        this.title = title;
    }

    public InventoryBuilder type(InventoryType type){
        inventoryType = type;
        return this;
    }

    public InventoryBuilder holder(InventoryHolder holder){
        inventoryHolder = holder;
        return this;
    }



    public Inventory build(){
        Inventory inventory = Bukkit.createInventory(inventoryHolder, inventoryType, title);

        return inventory;
    }

}
