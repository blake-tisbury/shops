package shops.Gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import shops.Gui.actions.GuiAction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseGui {
    // Map of GUI's and their associated UUID's
    public static Map<UUID, BaseGui> guisByUuid = new HashMap<>();

    // Map of Player UUID's and the GUI's they have open
    public static Map<UUID, UUID> openGuis = new HashMap<>();

    private Inventory gui;
    private Map<Integer, GuiAction> actions;
    private UUID uuid;

    public BaseGui(int size, String name) {
        uuid = UUID.randomUUID();
        gui = Bukkit.createInventory(null, size, name);
        actions = new HashMap<>();
        guisByUuid.put(this.uuid, this);
    }

    public Inventory getGui() { return this.gui; }

    /**
     * Set item in this GUI with a given action
     * @param slot slot to put item in
     * @param item item to fill with
     * @param action action associated with this item
     */
    public void setItem(int slot, ItemStack item, GuiAction action) {
        this.gui.setItem(slot, item);

        if (action != null) {
            actions.put(slot, action);
        }
    }

    /**
     * Set item in this GUI
     * @param slot slot to put item in
     * @param item item to fill with
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Show this GUI to given player
     * @param p player to show GUI to
     */
    public void show(Player p) {
        p.openInventory(this.gui);
        openGuis.put(p.getUniqueId(), this.uuid);
    }

    public void close(Player p) {
        p.closeInventory();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static Map<UUID, BaseGui> getGuisByUuid() {
        return guisByUuid;
    }

    public static Map<UUID, UUID> getOpenGuis() {
        return openGuis;
    }

    public Map<Integer, GuiAction> getActions() {
        return actions;
    }

    /**
     * Delete this GUI for all players
     */
    public void delete() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = openGuis.get(p.getUniqueId());
            if (playerUUID.equals(this.uuid)) {
                p.closeInventory();
            }
        }
        guisByUuid.remove(this.uuid);
    }
}
