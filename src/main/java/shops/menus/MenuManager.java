package shops.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import shops.Shops;

public class MenuManager {

    private Plugin plugin;
    private final MenuManager gui;

    private int maxPageSize;
    private int maxPages;
    private int currPage;
    private int numShops;
    private Inventory inv;
    private String title;

    public MenuManager() {
        this.plugin = Shops.getInstance();
        this.gui = this;
        this.maxPageSize = 27;
        this.title = "Shops Menu";
    }

    /**
     * Initialize inventory object to represent our menu
     */
    public void buildMenu() {
        this.inv = Bukkit.createInventory(null, InventoryType.CHEST, this.title);

        for (int i = 0; i < this.maxPageSize; i++) {
            setItemSlot(i, new ItemStack(Material.CHEST, 1));
        }
    }

    /**
     * Show this inventory/menu to the given player
     * @param p player to be shown to
     */
    public void showMenu(Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            p.openInventory(getInventory());
        }, 2);
    }

    /**
     * Sets item slot specifically based off index
     * @param slot slot index to fill
     * @param item item to fill with
     */
    public void setItemSlot(int slot, ItemStack item) {
        getInventory().setItem(slot, item);
    }

    public void setItemSlot(int slot, ItemStack item, int page) {
        getInventory().setItem(slot, item);
    }

    // Getters
    public Inventory getInventory() {
        if (inv == null) {
            buildMenu();
        }
        return this.inv;
    }

    public MenuManager getGui() {
        return this.gui;
    }

    // Setters
    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public void setNumShops(int numShops) {
        this.numShops = numShops;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
