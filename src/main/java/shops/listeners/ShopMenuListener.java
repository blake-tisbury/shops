package shops.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import shops.Gui.BaseGui;
import shops.Gui.actions.GuiAction;
import shops.menus.MenuManager;

import java.util.UUID;

public class ShopMenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        UUID playerUUID = p.getUniqueId();

        UUID guiUUID = BaseGui.openGuis.get(playerUUID);
        if (guiUUID != null) {
            e.setCancelled(true);
            BaseGui gui = BaseGui.getGuisByUuid().get(guiUUID);
            GuiAction action = gui.getActions().get(e.getRawSlot());

            if (action != null) {
                action.click(p);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        UUID playerUUID = p.getUniqueId();

        BaseGui.getOpenGuis().remove(playerUUID);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = (Player) e.getPlayer();
        UUID playerUUID = p.getUniqueId();

        BaseGui.getOpenGuis().remove(playerUUID);
    }
}
