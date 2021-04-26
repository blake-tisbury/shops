package shops.Gui.actions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import shops.Shops;
import shops.Utils.ShopManager;

import java.util.List;

public class WarpShop implements GuiAction {

    private int slot;

    public WarpShop(int slot) {
        this.slot = slot;
    }

    @Override
    public void click(Player p) {
        ShopManager sm = Shops.getShopManager();

        // List of Shop ID's
        List<String> ids = sm.getIds();
        String id = ids.get(slot);
        Location l = sm.getWarp(id);

        p.closeInventory();
        p.teleport(l);
    }
}
