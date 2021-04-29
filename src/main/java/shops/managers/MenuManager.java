package shops.managers;

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
import shops.Shops;
import shops.Utils.Utils;

import java.util.*;

public class MenuManager {

    private Plugin plugin;
    private final MenuManager gui;

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
    private HashMap<String, ItemStack> menuSlots;
    private List<String> shopIds;
    private ShopManager sm;
    private static ArrayList<String> viewing;

    public MenuManager() {
        this.plugin = Shops.getInstance();
        this.gui = this;
        this.maxPageSize = 54;
        this.maxShopsPerPage = 45;
        this.numShops = 0;
        this.currPage = 0;
        this.nextSlot = 50;
        this.prevSlot = 48;
        this.exitSlot = 49;
        this.title = "Shops Menu";
        this.menuSlots = new HashMap<String, ItemStack>();
        this.sm = new ShopManager((Shops) this.plugin);
        viewing = new ArrayList<String>();
    }

    /**
     * Initialize inventory object to represent our menu
     */
    public void buildMenu() {
        this.inv = Bukkit.createInventory(null, this.maxPageSize, this.title);

        // Init Shop IDs list
        refreshShopIds();

        // Init the HashMap to represent shops and their associated player heads
        initMenuSlots();

        // Fill the inventory menu with the appropriate heads.
        fillShopMenu();

        fillBottomMenuBar();
    }

    public void updateMenu(Inventory inv) {
        // Fill the inventory menu with the appropriate heads.
        fillShopMenu(inv);
        fillBottomMenuBar(inv);
    }


    /**
     * Show this inventory/menu to the given player
     * @param p player to be shown to
     */
    public void showMenu(Player p) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            p.openInventory(getInventory());
        }, 3);
    }

    /**
     * Sets item slot specifically based off index
     * @param slot slot index to fill
     * @param item item to fill with
     */
    public void setMenuSlot(int slot, ItemStack item) {
        getInventory().setItem(slot, item);
    }

    public void setMenuSlot(int slot, ItemStack item, Inventory inv) {
        inv.setItem(slot, item);
    }

    public void fillShopMenu() {
        fillShopMenu(getInventory());
    }

    public void fillShopMenu(Inventory inv) {
        int currPageMin = this.currPage * this.maxShopsPerPage;
        int currPageMax = currPageMin + this.maxShopsPerPage;
        System.out.println("[TESTING] min: " + currPageMin + " max: " + currPageMax + " shops: " + numShops + " array size:" + this.menuSlots.values().toArray().length);

        for (int i = currPageMin; i < currPageMax; i++) {
            if (i >= this.numShops) {
                return;
            }
            ItemStack item = (ItemStack) this.menuSlots.values().toArray()[i];
            setMenuSlot(i - currPageMin, item, inv);
        }
    }

    public void fillBottomMenuBar() {
        fillBottomMenuBar(getInventory());
    }

    /**
     * Adds the Next, Prev, and Exit buttons to the menu
     */
    public void fillBottomMenuBar(Inventory inv) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS, 1);
        ItemStack exit = new ItemStack(Material.BARRIER, 1);

        // Fill in Glass Panes on Bottom
        for (int i = this.maxPageSize - 9; i < this.maxPageSize; i++) {
            ItemMeta fillerMeta = filler.getItemMeta();
            fillerMeta.setDisplayName("");
            setMenuSlot(i, filler, inv);
        }

        // If more than one page, add next button
        if (this.numPages > 1 && this.currPage < this.numPages - 1) {
            ItemStack nextArrow = new ItemStack(Material.ARROW, 1);
            ItemMeta nextMeta = nextArrow.getItemMeta();
            nextMeta.setDisplayName(ChatColor.RED + "Next Page");
            nextMeta.setLore(Arrays.asList("Click here to go forward a page"));
            nextArrow.setItemMeta(nextMeta);
            setMenuSlot(this.nextSlot, nextArrow, inv);
        }

        // If current page isn't 0, add previous button
        if (this.currPage != 0) {
            ItemStack prevArrow = new ItemStack(Material.ARROW, 1);
            ItemMeta prevMeta = prevArrow.getItemMeta();
            prevMeta.setDisplayName(ChatColor.RED + "Previous Page");
            prevMeta.setLore(Arrays.asList("Click here to go back a page"));
            prevArrow.setItemMeta(prevMeta);
            setMenuSlot(this.prevSlot, prevArrow, inv);
        }

        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "Exit");
        exitMeta.setLore(Arrays.asList("Click here to close"));
        exit.setItemMeta(exitMeta);
        setMenuSlot(this.exitSlot, exit, inv);
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
                    headLore.add(Utils.chat(ls.replace("{id}", id).replace("{player}", shopOwner.getName()).replace("{description}", "")));
                    if (ls.contains("{description}")) {
                        for (String s2 : Utils.split(sm.getDescription(id))) {
                            headLore.add(Utils.chat("&7" + s2));
                        }
                    }
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
            this.menuSlots.put(id, item);
            this.numShops = this.menuSlots.size();
            this.numPages = this.numShops / this.maxShopsPerPage;

            if (this.numShops % this.maxShopsPerPage != 0) {
                this.numPages++;
            }
        }
    }

    public void addViewing(Player p) {
        viewing.add(p.getName());
    }

    public static ArrayList<String> getViewing() {
        return viewing;
    }

    // Getters
    public Inventory getInventory() {
        if (this.inv == null) {
            buildMenu();
        }
        return this.inv;
    }

    public MenuManager getGui() {
        return this.gui;
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
