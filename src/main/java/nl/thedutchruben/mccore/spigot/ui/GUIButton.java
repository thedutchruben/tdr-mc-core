package nl.thedutchruben.mccore.spigot.ui;

import nl.thedutchruben.mccore.global.translations.TranslationManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class for creating common GUI buttons
 */
public class GUIButton {

    /**
     * Create a basic button with name and lore
     *
     * @param material The material for the button
     * @param name The name of the button
     * @param lore The lore (description) of the button
     * @return An ItemStack configured as a button
     */
    public static ItemStack create(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

            if (lore.length > 0) {
                meta.setLore(Arrays.stream(lore)
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                        .collect(Collectors.toList()));
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Create a button with a translation key for the name and lore
     *
     * @param material The material for the button
     * @param nameKey The translation key for the button name
     * @param loreKeys The translation keys for the lore lines
     * @return An ItemStack configured as a button with translated text
     */
    public static ItemStack createTranslated(Material material, String nameKey, String... loreKeys) {
        TranslationManager translationManager = TranslationManager.getInstance();
        String translatedName = translationManager.get(nameKey);

        String[] translatedLore = new String[loreKeys.length];
        for (int i = 0; i < loreKeys.length; i++) {
            translatedLore[i] = translationManager.get(loreKeys[i]);
        }

        return create(material, translatedName, translatedLore);
    }

    /**
     * Create standard navigation buttons for a paginated GUI
     *
     * @param paginatedGUI The paginated GUI to set up navigation for
     */
    public static void createBasicNavigationButtons(PaginatedGUI paginatedGUI) {
        // Previous page button
        ItemStack previousButton = createTranslated(
            Material.ARROW,
            "gui.pagination.previous_page",
            "gui.pagination.previous_page_tooltip"
        );

        // Next page button
        ItemStack nextButton = createTranslated(
            Material.ARROW,
            "gui.pagination.next_page",
            "gui.pagination.next_page_tooltip"
        );

        paginatedGUI.setNavigationButtons(
            previousButton,
            nextButton,
            paginatedGUI.getPreviousPageButtonSlot(),
            paginatedGUI.getNextPageButtonSlot()
        );
    }

    /**
     * Create a close button for GUIs
     *
     * @return An ItemStack configured as a close button
     */
    public static ItemStack createCloseButton() {
        return createTranslated(
            Material.BARRIER,
            "gui.close",
            "gui.close_tooltip"
        );
    }

    /**
     * Create a back button for GUIs
     *
     * @return An ItemStack configured as a back button
     */
    public static ItemStack createBackButton() {
        return createTranslated(
            Material.ARROW,
            "gui.back",
            "gui.back_tooltip"
        );
    }

    /**
     * Create a confirm button for confirmation GUIs
     *
     * @return An ItemStack configured as a confirm button
     */
    public static ItemStack createConfirmButton() {
        return createTranslated(
            Material.LIME_WOOL,
            "gui.confirm",
            "gui.confirm_tooltip"
        );
    }

    /**
     * Create a cancel button for confirmation GUIs
     *
     * @return An ItemStack configured as a cancel button
     */
    public static ItemStack createCancelButton() {
        return createTranslated(
            Material.RED_WOOL,
            "gui.cancel",
            "gui.cancel_tooltip"
        );
    }
}
