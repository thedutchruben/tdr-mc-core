package nl.thedutchruben.mccore.spigot.ui;

import lombok.Getter;
import nl.thedutchruben.mccore.global.translations.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A GUI with pagination support
 * <p>This class provides an easy way to create paginated GUIs in Minecraft</p>
 */
public class PaginatedGUI {
    @Getter
    private final List<GUI> pages = new ArrayList<>();
    @Getter
    private final String baseTitle;
    @Getter
    private final int rows;
    @Getter
    private final List<GUIItem> items = new ArrayList<>();
    @Getter
    private final Map<Integer, GUIItem> staticItems = new HashMap<>();
    @Getter
    private final int itemsPerPage;
    @Getter
    private ItemStack previousPageButton;
    @Getter
    private ItemStack nextPageButton;
    @Getter
    private Integer previousPageButtonSlot;
    @Getter
    private Integer nextPageButtonSlot;

    /**
     * Creates a new PaginatedGUI
     *
     * @param rows     Number of rows (1-6)
     * @param title    Base title of the inventory
     * @param reserved Number of slots reserved for static items (navigation, etc.)
     */
    public PaginatedGUI(int rows, String title, int reserved) {
        this.rows = Math.min(6, Math.max(1, rows));
        this.baseTitle = title;
        this.itemsPerPage = (rows * 9) - reserved;
    }

    /**
     * Creates a new PaginatedGUI with standard navigation buttons
     *
     * @param rows  Number of rows (1-6)
     * @param title Base title of the inventory
     */
    public PaginatedGUI(int rows, String title) {
        this(rows, title, 9); // Reserve bottom row for navigation by default

        // Set up default navigation in the bottom row
        this.previousPageButtonSlot = (rows - 1) * 9 + 3;
        this.nextPageButtonSlot = (rows - 1) * 9 + 5;

        // Default navigation buttons
        GUIButton.createBasicNavigationButtons(this);
    }

    /**
     * Add an item to the paginated content
     *
     * @param item The GUIItem to add
     * @return This PaginatedGUI instance for chaining
     */
    public PaginatedGUI addItem(GUIItem item) {
        items.add(item);
        return this;
    }

    /**
     * Add an item to the paginated content
     *
     * @param itemStack The ItemStack to add
     * @return This PaginatedGUI instance for chaining
     */
    public PaginatedGUI addItem(ItemStack itemStack) {
        return addItem(new GUIItem(itemStack));
    }

    /**
     * Set a static item that will appear on all pages
     *
     * @param slot The slot for the item
     * @param item The GUIItem to set
     * @return This PaginatedGUI instance for chaining
     */
    public PaginatedGUI setStaticItem(int slot, GUIItem item) {
        staticItems.put(slot, item);
        return this;
    }

    /**
     * Set a static item that will appear on all pages
     *
     * @param slot      The slot for the item
     * @param itemStack The ItemStack to set
     * @return This PaginatedGUI instance for chaining
     */
    public PaginatedGUI setStaticItem(int slot, ItemStack itemStack) {
        return setStaticItem(slot, new GUIItem(itemStack));
    }

    /**
     * Set the navigation buttons for the paginated GUI
     *
     * @param previousPageButton     The ItemStack for the previous page button
     * @param nextPageButton         The ItemStack for the next page button
     * @param previousPageButtonSlot The slot for the previous page button
     * @param nextPageButtonSlot     The slot for the next page button
     * @return This PaginatedGUI instance for chaining
     */
    public PaginatedGUI setNavigationButtons(ItemStack previousPageButton, ItemStack nextPageButton,
                                            int previousPageButtonSlot, int nextPageButtonSlot) {
        this.previousPageButton = previousPageButton;
        this.nextPageButton = nextPageButton;
        this.previousPageButtonSlot = previousPageButtonSlot;
        this.nextPageButtonSlot = nextPageButtonSlot;
        return this;
    }

    /**
     * Build all the pages for this paginated GUI
     *
     * @return This PaginatedGUI instance for chaining
     */
    public PaginatedGUI build() {
        pages.clear();

        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1; // At least one page even if empty

        for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            // Get the translated page format and apply the page numbers
            String pageFormat = TranslationManager.getInstance().get("gui.pagination.page_format");
            String title = baseTitle;

            if (pageFormat != null && !pageFormat.isEmpty()) {
                title += " - " + pageFormat
                    .replace("%current%", String.valueOf(pageNum + 1))
                    .replace("%total%", String.valueOf(totalPages));
            }

            GUI page = new GUI(rows, title);

            // Add static items to all pages
            for (Map.Entry<Integer, GUIItem> entry : staticItems.entrySet()) {
                page.setItem(entry.getKey(), entry.getValue());
            }

            // Add paginated items to this page
            int startIndex = pageNum * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, items.size());

            int slot = 0;
            for (int i = startIndex; i < endIndex; i++) {
                // Skip slots used by static items
                while (staticItems.containsKey(slot)) {
                    slot++;
                }

                page.setItem(slot, items.get(i));
                slot++;
            }

            // Add navigation buttons if there are multiple pages
            if (totalPages > 1) {
                final int currentPage = pageNum;

                // Previous page button
                if (pageNum > 0 && previousPageButtonSlot != null && previousPageButton != null) {
                    page.setItem(previousPageButtonSlot, previousPageButton, event -> {
                        if (event.getWhoClicked() instanceof Player) {
                            open((Player) event.getWhoClicked(), currentPage - 1);
                        }
                    });
                }

                // Next page button
                if (pageNum < totalPages - 1 && nextPageButtonSlot != null && nextPageButton != null) {
                    page.setItem(nextPageButtonSlot, nextPageButton, event -> {
                        if (event.getWhoClicked() instanceof Player) {
                            open((Player) event.getWhoClicked(), currentPage + 1);
                        }
                    });
                }
            }

            pages.add(page);
        }

        return this;
    }

    /**
     * Open a specific page of this paginated GUI for a player
     *
     * @param player   The player to open the GUI for
     * @param pageNum  The page number to open (0-based index)
     */
    public void open(Player player, int pageNum) {
        if (pages.isEmpty()) {
            build();
        }

        pageNum = Math.max(0, Math.min(pageNum, pages.size() - 1));
        pages.get(pageNum).open(player);
    }

    /**
     * Open the first page of this paginated GUI for a player
     *
     * @param player The player to open the GUI for
     */
    public void open(Player player) {
        open(player, 0);
    }
}
