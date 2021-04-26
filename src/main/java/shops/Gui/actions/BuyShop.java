package shops.Gui.actions;

import net.tnemc.core.common.api.TNEAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import shops.Gui.BaseGui;
import shops.Gui.ShopsGui;
import shops.Shops;
import shops.Utils.ShopManager;
import shops.Utils.Utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BuyShop implements GuiAction {

    private int slot;
    private TNEAPI econ;

    public BuyShop(int slot) {
        this.slot = slot;
        this.econ = Shops.getEconomy();
    }

    // TODO: Implement Holograms
    // TODO: confirmation screen
    @Override
    public void click(Player p) {
        UUID playerUUID = p.getUniqueId();
        UUID guiUUID = BaseGui.openGuis.get(playerUUID);

        // If player is in GUI
        if (guiUUID != null) {
            HashMap<String, ItemStack> map = ShopsGui.getMenuSlots();
            ShopManager sm = Shops.getShopManager();
            List<String> ids = sm.getIds();
            String id = ids.get(slot);

            // Check if they have enough funds
            if (econ.getHoldings(p.getName()).doubleValue() < Integer.parseInt(sm.getPrice(id))) {
                p.sendMessage(Utils.chat(Shops.getInstance().getConfig().getString("messages.noFunds")));
                return;
            }

            // Update shop info
            sm.setOwner(p, id);
            sm.setName(p.getName(), id);
            sm.setDescription("", id);

            // Hologram hologram = HologramsAPI.createHologram(HolographicDisplays, l);
            // textLine = hologram.appendTextLine("A hologram line");

            econ.removeHoldings(p.getName(), BigDecimal.valueOf(Integer.parseInt(sm.getPrice(id))));
            p.sendMessage(Utils.chat(Shops.getInstance().getConfig().getString("messages.shopPurchased")));
            p.closeInventory();
        }
    }
}
