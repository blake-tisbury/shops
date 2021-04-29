package shops.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import shops.guis.actions.*;
import shops.Shops;
import shops.managers.ShopManager;
import shops.utils.Utils;

import java.util.*;

//TODO: Buy confirmation screen
//TODO: Personal shops screen

public class ShopsGui extends BaseGui {

    private Plugin plugin;
    private int maxPageSize;
    private int maxShopsPerPage;
    private int currPage;
    private int numPages;
    private int numShops;
    private int nextSlot;
    private int prevSlot;
    private int exitSlot;
    private Inventory inv;
    private String title;
    private static HashMap<String, ItemStack> menuSlots;
    private List<String> shopIds;
    private ShopManager sm;

    public ShopsGui() {
        super(54, "Shops");

        this.plugin = Shops.getInstance();
        this.maxPageSize = 54;
        this.maxShopsPerPage = 45;
        this.numShops = 0;
        this.currPage = 0;
        this.nextSlot = 50;
        this.prevSlot = 48;
        this.exitSlot = 49;
        this.title = "Shops Menu";
        this.menuSlots = new HashMap<String, ItemStack>();
        this.sm = Shops.getShopManager();

        initMenuSlots();
    }

    @Override
    public void show(Player p) {
        // Fill the inventory menu with the appropriate heads.
        refresh();

        p.openInventory(getGui());
        openGuis.put(p.getUniqueId(), getUuid());
    }

    /**
     * Refresh GUI with current object field values
     */
    public void refresh() {
        getGui().clear();
        fillShopMenu();
        fillBottomMenuBar();
    }

    /**
     * Fill the shops sections of the shop menu with player heads
     */
    public void fillShopMenu() {
        int currPageMin = this.currPage * this.maxShopsPerPage;
        int currPageMax = currPageMin + this.maxShopsPerPage;

        for (int i = currPageMin; i < currPageMax; i++) {
            if (i >= this.numShops) {
                return;
            }

            ItemStack item = menuSlots.get(shopIds.get(i));
            if (item.getItemMeta().getDisplayName().contains("Unclaimed")) {
                setItem(i - currPageMin, item, new BuyShop(i));
            }
            else {
                setItem(i - currPageMin, item, new WarpShop(i));
            }
        }
    }

    /**
     * Adds the Next, Prev, and Exit buttons to the menu
     */
    public void fillBottomMenuBar() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS, 1);
        ItemStack exit = new ItemStack(Material.BARRIER, 1);
        GuiAction nextPage = new NextPage();
        GuiAction prevPage = new PrevPage();
        GuiAction exitMenu = new Exit();

        // Fill in Glass Panes on Bottom
        for (int i = this.maxPageSize - 9; i < this.maxPageSize; i++) {
            ItemMeta fillerMeta = filler.getItemMeta();
            fillerMeta.setDisplayName("");
            setItem(i, filler);
        }

        // If more than one page, add next button
        if (this.numPages > 1 && this.currPage < this.numPages - 1) {
            ItemStack nextArrow = new ItemStack(Material.ARROW, 1);
            ItemMeta nextMeta = nextArrow.getItemMeta();
            nextMeta.setDisplayName(ChatColor.RED + "Next Page");
            nextMeta.setLore(Arrays.asList("Click here to go forward a page"));
            nextArrow.setItemMeta(nextMeta);
            setItem(this.nextSlot, nextArrow, nextPage);
        }

        // If current page isn't 0, add previous button
        if (this.currPage != 0) {
            ItemStack prevArrow = new ItemStack(Material.ARROW, 1);
            ItemMeta prevMeta = prevArrow.getItemMeta();
            prevMeta.setDisplayName(ChatColor.RED + "Previous Page");
            prevMeta.setLore(Arrays.asList("Click here to go back a page"));
            prevArrow.setItemMeta(prevMeta);
            setItem(this.prevSlot, prevArrow, prevPage);
        }

        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "Exit");
        exitMeta.setLore(Arrays.asList("Click here to close"));
        exit.setItemMeta(exitMeta);
        setItem(this.exitSlot, exit, exitMenu);
    }

    /**
     * Refresh shop ID's with the current list of shops
     */
    public void refreshShopIds() {
        this.shopIds = this.sm.getIds();
    }

    /**
     * Initializes the HashMap used to represent the slots of the menu.
     */
    public void initMenuSlots() {
        this.shopIds = this.sm.getIds();

        // Iterate through all shop ID's, claimed and unclaimed
        for (String id : shopIds) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            List<String> headLore = new ArrayList<>();

            // Two branches for claimed and unclaimed. Sets up the item meta data to represent shops
            if (!sm.getOwner(id).equals("null")) {
                OfflinePlayer shopOwner = (Bukkit.getOfflinePlayer(UUID.fromString(sm.getOwner(id))));

                // Set Player Head data
                skullMeta.setOwningPlayer(shopOwner);
                skullMeta.setDisplayName(Utils.chat(this.plugin.getConfig().getString("claimedName").replace("{name}", sm.getName(id))));

                // Add shop information to head
                for (String ls : this.plugin.getConfig().getStringList("claimedLore")) {
                    headLore.add(Utils.chat(ls.replace("{id}", id).replace("{player}", shopOwner.getName()).replace("{description}", sm.getDescription(id))));
                }
            }
            else {
                // Sets the icon to chest, player head of ElMarcosFTW
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("ElMarcosFTW"));
                skullMeta.setDisplayName(Utils.chat(this.plugin.getConfig().getString("unclaimedName")));
                for(String ls : this.plugin.getConfig().getStringList("unclaimedLore")) {
                    headLore.add(Utils.chat(ls.replace("{id}", id)).replace("{price}", sm.getPrice(id)));
                }
            }

            // Set item meta back to item and add to HashMap
            skullMeta.setLore(headLore);
            item.setItemMeta(skullMeta);
            menuSlots.put(id, item);
            this.numShops = menuSlots.size();
            this.numPages = this.numShops / this.maxShopsPerPage;

            if (this.numShops % this.maxShopsPerPage != 0) {
                this.numPages++;
            }
        }
    }

    /**
     * Increment currPage value and refresh
     */
    public void nextPage() {
        if (this.currPage < this.numPages - 1) {
            this.currPage++;
            refresh();
        }
    }

    /**
     * Decrement currPage value and refresh
     */
    public void prevPage() {
        if (this.currPage > 0) {
            this.currPage--;
            refresh();
        }
    }

    public static HashMap<String, ItemStack> getMenuSlots() {
        return menuSlots;
    }
}
